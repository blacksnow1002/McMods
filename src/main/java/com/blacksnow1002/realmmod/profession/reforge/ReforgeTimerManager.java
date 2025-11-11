package com.blacksnow1002.realmmod.profession.reforge;

import com.blacksnow1002.realmmod.block.entity.ReforgeToolBlockEntity;
import com.blacksnow1002.realmmod.profession.reforge.ReforgeLogicHandler;
import com.blacksnow1002.realmmod.profession.reforge.ReforgeTask;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.level.BlockEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Mod.EventBusSubscriber
public class ReforgeTimerManager extends SavedData {

    private static final String DATA_NAME = "realmmod_reforge_timer";

    // 1. 按位置索引：用於檢查爐子是否在煉丹、取消煉丹
    private final Map<BlockPos, ReforgeTask> tasksByPosition = new ConcurrentHashMap<>();

    // 2. 按玩家索引：是否在煉丹狀態
    private final Map<UUID, ReforgeTask> tasksByUUID = new ConcurrentHashMap<>();

    // 3. 按時間戳索引：用於每秒查詢哪些任務完成了
    private final Map<Long, List<ReforgeTask>> tasksByTimestamp = new ConcurrentHashMap<>();

    public ReforgeTimerManager() {}

    /**
     * 開始新的煉丹任務
     */
    public void startReforgeTask(ReforgeTask task) {
        // 加入位置索引
        tasksByPosition.put(task.pos, task);

        tasksByUUID.put(task.playerUUID, task);

        // 加入時間戳索引
        tasksByTimestamp.computeIfAbsent(task.endTime, k -> new ArrayList<>()).add(task);

        setDirty();
    }

    /**
     * 取消煉丹任務（爐子被破壞時調用）
     */
    public void cancelReforge(BlockPos pos) {
        ReforgeTask task = tasksByPosition.remove(pos);
        if (task != null) {
            tasksByUUID.remove(task.playerUUID);
            // 同時從時間戳索引中移除
            List<ReforgeTask> tasksAtTime = tasksByTimestamp.get(task.endTime);
            if (tasksAtTime != null) {
                tasksAtTime.remove(task);
                if (tasksAtTime.isEmpty()) {
                    tasksByTimestamp.remove(task.endTime);
                }
            }
            setDirty();
        }
    }

    /**
     * 檢查某個位置是否正在煉丹
     */
    public boolean isReforgeInProgress(BlockPos pos) {
        return tasksByPosition.containsKey(pos);
    }

    public boolean isPlayerInReforge(UUID playerUUID) {
        return tasksByUUID.containsKey(playerUUID);
    }

    /**
     * 獲取煉丹剩餘時間（tick）
     */
    public long getRemainingTime(BlockPos pos, long currentTime) {
        ReforgeTask task = tasksByPosition.get(pos);
        if (task == null) return 0;
        return Math.max(0, task.endTime - currentTime);
    }

    public static ReforgeTimerManager get(ServerLevel serverLevel) {
        MinecraftServer server = serverLevel.getServer();

        ReforgeTimerManager manager = serverLevel.getDataStorage().computeIfAbsent(
                new Factory<>(
                        ReforgeTimerManager::new,
                        (tag, provider) -> load(tag, provider, server),
                        null
                ), DATA_NAME
        );

        return manager;
    }

    @Override
    public CompoundTag save(CompoundTag tag, HolderLookup.Provider provider) {
        ListTag taskList = new ListTag();

        // 只需要保存 tasksByPosition，載入時會自動重建 tasksByTimestamp
        for (ReforgeTask task : tasksByPosition.values()) {
            taskList.add(task.saveNBTData(provider));
        }

        tag.put("ReforgeTasks", taskList);
        return tag;
    }

    public static ReforgeTimerManager load(CompoundTag tag, HolderLookup.Provider provider, MinecraftServer server) {
        ReforgeTimerManager manager = new ReforgeTimerManager();
        ListTag taskList = tag.getList("ReforgeTasks", Tag.TAG_COMPOUND);

        for (Tag t : taskList) {
            ReforgeTask task = ReforgeTask.loadNBTData((CompoundTag) t, provider, server);

            // 重建雙索引
            manager.tasksByPosition.put(task.pos, task);
            manager.tasksByUUID.put(task.playerUUID, task);
            manager.tasksByTimestamp.computeIfAbsent(task.endTime, k -> new ArrayList<>()).add(task);
        }

        return manager;
    }

    /**
     * 每秒檢查是否有煉丹完成（優化版：只檢查當前時間戳）
     */
    @SubscribeEvent
    public static void onServerTick(TickEvent.ServerTickEvent event) {
        if (event.phase != TickEvent.Phase.END) return;

        ServerLevel overworld = event.getServer().overworld();
        ReforgeTimerManager manager = get(overworld);

        long currentTime = overworld.getGameTime();

        // 直接查找當前時間戳的任務（高效！）
        List<ReforgeTask> completedTasks = manager.tasksByTimestamp.remove(currentTime);

        if (completedTasks != null && !completedTasks.isEmpty()) {
            for (ReforgeTask task : completedTasks) {
                manager.completeReforge(task, event.getServer());
                manager.tasksByPosition.remove(task.pos);
            }
            manager.setDirty();
        }
    }

    /**
     * 完成煉丹，填充輸出槽並通知玩家
     */
    private void completeReforge(ReforgeTask task, MinecraftServer server) {
//TODO: 結束邏輯
// ReforgeLogicHandler.finishReforge(task.outputPill, task.pos, server.getLevel(task.dimension));

        ServerPlayer player = server.getPlayerList().getPlayer(task.playerUUID);
        if (player != null) {
            player.sendSystemMessage(Component.literal(task.returnText));
        }
    }

    /**
     * 當方塊被破壞時取消煉丹
     */
    @SubscribeEvent
    public static void onBlockBreak(BlockEvent.BreakEvent event) {
        Level level = (Level) event.getLevel();
        if (level.isClientSide()) return;

        BlockEntity blockEntity = level.getBlockEntity(event.getPos());
        if (blockEntity instanceof ReforgeToolBlockEntity) {
            ReforgeTimerManager manager = get((ServerLevel) level);
            manager.cancelReforge(event.getPos());
        }
    }
}