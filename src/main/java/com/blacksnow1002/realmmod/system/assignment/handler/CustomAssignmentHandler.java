package com.blacksnow1002.realmmod.system.assignment.handler;

import com.blacksnow1002.realmmod.system.assignment.AssignmentSystem;
import net.minecraft.server.level.ServerPlayer;

import java.util.*;

/**
 * 統一的 Custom Task Handler - 與 AssignmentSystem 整合
 * 所有自定義任務邏輯都在這裡管理
 *
 * 使用方式：
 * 1. 在 ServerTickEvent 中調用 updateAllCustomTasks(player)
 * 2. 在特定事件中調用 onSpellCast(player, spellId) 等
 * 3. 玩家離線時調用 clearPlayerData(playerUUID)
 */
public class CustomAssignmentHandler {

    // 儲存 <玩家UUID, 任務數據>
    private static final Map<UUID, PlayerTaskCache> playerTaskCache = new HashMap<>();
    private static final AssignmentSystem system = AssignmentSystem.getInstance();

    /**
     * 主 tick 更新方法 - 所有 custom 任務一次更新
     * 在 ServerTickEvent 中每 tick 調用一次
     */
    public static void updateAllCustomTasks(ServerPlayer player) {
        UUID uuid = player.getUUID();
        PlayerTaskCache cache = playerTaskCache.computeIfAbsent(uuid, k -> new PlayerTaskCache());

        // 檢查玩家接取的任務，逐一更新
        for (CustomTaskType taskType : CustomTaskType.values()) {
            // 先檢查玩家是否接取了這個任務
            if (!system.isAssignmentAccepted(player, taskType.assignmentId)) {
                cache.resetTask(taskType);
                continue;
            }

            // 只有接取的任務才執行檢查
            TaskData data = cache.getOrCreateTaskData(taskType);
            taskType.handler.update(player, data);
        }
    }

    /**
     * 事件觸發：玩家施放法術
     */
    public static void onSpellCast(ServerPlayer player, String spellId) {
        UUID uuid = player.getUUID();
        PlayerTaskCache cache = playerTaskCache.get(uuid);
        if (cache == null) return;

        for (CustomTaskType taskType : CustomTaskType.values()) {
            if (!system.isAssignmentAccepted(player, taskType.assignmentId)) continue;

            if (taskType.handler instanceof EventBasedTaskHandler) {
                TaskData data = cache.getTaskData(taskType);
                if (data != null) {
                    ((EventBasedTaskHandler) taskType.handler).onEvent(player, data, spellId);
                }
            }
        }
    }

    /**
     * 事件觸發：玩家擊殺實體
     */
    public static void onEntityKilled(ServerPlayer player, String entityId) {
        UUID uuid = player.getUUID();
        PlayerTaskCache cache = playerTaskCache.get(uuid);
        if (cache == null) return;

        for (CustomTaskType taskType : CustomTaskType.values()) {
            if (!system.isAssignmentAccepted(player, taskType.assignmentId)) continue;

            if (taskType.handler instanceof EventBasedTaskHandler) {
                TaskData data = cache.getTaskData(taskType);
                if (data != null) {
                    ((EventBasedTaskHandler) taskType.handler).onEvent(player, data, entityId);
                }
            }
        }
    }

    /**
     * 清除玩家的所有 custom 任務數據
     */
    public static void clearPlayerData(UUID playerUUID) {
        playerTaskCache.remove(playerUUID);
    }

    // ==================== 任務類型定義 ====================

    /**
     * Custom 任務類型列舉 - 輕鬆添加新類型
     */
    public enum CustomTaskType {
        // 冥想任務
        LAVA_MEDITATION(
                "fire_supreme_level_3",
                new MeditationTaskHandler(MeditationType.LAVA)
        ),
        WATER_MEDITATION(
                "fire_supreme_level_3",
                new MeditationTaskHandler(MeditationType.WATER)
        ),

        // 法術施放任務
        SPELL_CAST_FIREBALL(
                "fire_supreme_level_4",
                new SpellCastHandler("fireball", "cast_fireball", 10)
        ),
        SPELL_CAST_FREEZE(
                "water_supreme_level_2",
                new SpellCastHandler("freeze", "cast_freeze", 5)
        );

        final String assignmentId;
        final TaskHandler handler;

        CustomTaskType(String assignmentId, TaskHandler handler) {
            this.assignmentId = assignmentId;
            this.handler = handler;
        }
    }

    // ==================== 任務處理接口 ====================

    /**
     * 基礎任務處理器接口
     */
    interface TaskHandler {
        void update(ServerPlayer player, TaskData data);
    }

    /**
     * 事件驅動型任務處理器
     */
    interface EventBasedTaskHandler extends TaskHandler {
        void onEvent(ServerPlayer player, TaskData data, String eventData);

        @Override
        default void update(ServerPlayer player, TaskData data) {
            // 事件驅動型任務不需要每 tick 更新
        }
    }

    // ==================== 冥想任務處理 ====================

    static class MeditationTaskHandler implements TaskHandler {
        private final MeditationType type;
        private static final String MEDITATION_KEY = "realmmod.Meditation.IsMeditating";

        MeditationTaskHandler(MeditationType type) {
            this.type = type;
        }

        @Override
        public void update(ServerPlayer player, TaskData data) {
            // 第一層：檢查冥想狀態
            if (!player.getPersistentData().getBoolean(MEDITATION_KEY)) {
                data.reset();
                return;
            }

            // 第二層：檢查環境
            if (!type.isInEnvironment(player)) {
                data.reset();
                return;
            }

            // 更新進度
            data.incrementTicks();
            if (data.getTicks() % 20 == 0) {
                system.updateObjectiveProgress(
                        player,
                        type.assignmentId,
                        type.objective,
                        data.getTicks() / 20
                );
            }
        }
    }

    // ==================== 法術施放任務處理 ====================

    static class SpellCastHandler implements EventBasedTaskHandler {
        private final String spellId;
        private final String objective;
        private final int requiredCount;

        SpellCastHandler(String spellId, String objective, int requiredCount) {
            this.spellId = spellId;
            this.objective = objective;
            this.requiredCount = requiredCount;
        }

        @Override
        public void onEvent(ServerPlayer player, TaskData data, String eventData) {
            if (!eventData.equals(this.spellId)) return;

            data.incrementCounter();

            // 找出對應的 assignment ID
            for (CustomTaskType type : CustomTaskType.values()) {
                if (type.handler == this && system.isAssignmentAccepted(player, type.assignmentId)) {
                    system.updateObjectiveProgress(
                            player,
                            type.assignmentId,
                            this.objective,
                            data.getCounter()
                    );
                    break;
                }
            }
        }
    }

    // ==================== 環境類型 ====================

    enum MeditationType {
        LAVA("fire_supreme_level_3", "meditate_in_lava") {
            @Override
            boolean isInEnvironment(ServerPlayer player) {
                return player.isInLava();
            }
        },
        WATER("fire_supreme_level_3", "meditate_in_water") {
            @Override
            boolean isInEnvironment(ServerPlayer player) {
                return player.isInWater();
            }
        };

        final String assignmentId;
        final String objective;

        MeditationType(String assignmentId, String objective) {
            this.assignmentId = assignmentId;
            this.objective = objective;
        }

        abstract boolean isInEnvironment(ServerPlayer player);
    }

    // ==================== 玩家任務數據緩存 ====================

    /**
     * 單個玩家的任務數據緩存
     */
    static class PlayerTaskCache {
        private final Map<CustomTaskType, TaskData> taskDataMap = new HashMap<>();

        TaskData getOrCreateTaskData(CustomTaskType type) {
            return taskDataMap.computeIfAbsent(type, k -> new TaskData());
        }

        TaskData getTaskData(CustomTaskType type) {
            return taskDataMap.get(type);
        }

        void resetTask(CustomTaskType type) {
            TaskData data = taskDataMap.get(type);
            if (data != null) {
                data.reset();
            }
        }
    }

    /**
     * 任務數據容器
     */
    static class TaskData {
        private int ticks = 0;
        private int counter = 0;

        void incrementTicks() {
            ticks++;
        }

        void incrementCounter() {
            counter++;
        }

        void reset() {
            ticks = 0;
            counter = 0;
        }

        int getTicks() {
            return ticks;
        }

        int getCounter() {
            return counter;
        }
    }
}