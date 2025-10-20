package com.blacksnow1002.realmmod.assignment.customAssignmentHandler;

import com.blacksnow1002.realmmod.assignment.AssignmentRegistry;
import com.blacksnow1002.realmmod.assignment.AssignmentSystem;
import net.minecraft.server.level.ServerPlayer;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class WaterMeditationHandler {

    // 追蹤玩家在岩漿中的 tick 數 <玩家UUID, tick數>
    private static final Map<UUID, Integer> waterMeditationTicks = new HashMap<>();

    /**
     * 更新水中冥想進度
     * 應該在 ServerTickEvent 或 PlayerTickEvent 中每 tick 調用一次
     */
    public static void updateWaterMeditationProgress(ServerPlayer player) {
        // 檢查玩家是否有接取這個任務
        AssignmentSystem system = AssignmentSystem.getInstance();
        if (!system.isAssignmentAccepted(player, AssignmentRegistry.AssignmentIds.FIRE_SUPREME_LEVEL_3)) {
            // 如果沒有接取任務，清除追蹤數據
            waterMeditationTicks.remove(player.getUUID());
            return;
        }

        UUID uuid = player.getUUID();

        if (player.isInWater()) {
            if (player.getPersistentData().getBoolean("realmmod.Meditation.IsMeditating")) {
                int currentTicks = waterMeditationTicks.getOrDefault(uuid, 0);
                currentTicks++;
                waterMeditationTicks.put(uuid, currentTicks);

                // 每 20 ticks (1秒) 更新一次進度
                if (currentTicks % 20 == 0) {
                    int seconds = currentTicks / 20;
                    system.updateObjectiveProgress(
                            player,
                            AssignmentRegistry.AssignmentIds.FIRE_SUPREME_LEVEL_3,
                            "meditate_in_water",
                            seconds
                    );
                }
                waterMeditationTicks.put(uuid, 0);
            }

        } else {
            // 玩家不在岩漿中，重置計數（可選：如果你想要累計的話就移除這段）
            waterMeditationTicks.put(uuid, 0);
        }
    }

    /**
     * 清除玩家的岩漿冥想數據（當任務完成或放棄時調用）
     */
    public static void clearWaterMeditationData(UUID playerUUID) {
        waterMeditationTicks.remove(playerUUID);
    }
}