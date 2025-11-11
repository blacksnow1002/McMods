package com.blacksnow1002.realmmod.system.dimension.dongtian;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.ChunkPos;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 洞天生命週期管理器
 * 負責處理洞天的tick更新、區塊載入卸載等效能優化邏輯
 */
@Mod.EventBusSubscriber
public class DongTianLifecycleManager {

    // 記錄每個洞天的最後訪問時間
    private static final Map<UUID, Long> lastAccessTime = new ConcurrentHashMap<>();

    // 記錄哪些玩家當前在洞天中
    private static final Set<UUID> playersInDongTian = ConcurrentHashMap.newKeySet();

    // 記錄洞天的tick計數器
    private static final Map<UUID, Integer> dongTianTickCounters = new ConcurrentHashMap<>();

    // 待卸載的洞天隊列 <洞天主人UUID, 卸載時間戳>
    private static final Map<UUID, Long> pendingUnload = new ConcurrentHashMap<>();

    // 記錄哪些洞天的區塊已被強制載入
    private static final Set<UUID> forceLoadedDongTians = ConcurrentHashMap.newKeySet();

    /**
     * 當玩家進入洞天時調用
     * 在 DongTianManager.enterDongTian() 中調用
     */
    public static void onPlayerEnterDongTian(ServerPlayer player, UUID dongTianOwner, ServerLevel dongTianLevel) {
        playersInDongTian.add(player.getUUID());
        lastAccessTime.put(dongTianOwner, System.currentTimeMillis());

        // 如果這個洞天正在等待卸載,取消卸載
        pendingUnload.remove(dongTianOwner);

        // 確保洞天區塊被強制載入
        if (!forceLoadedDongTians.contains(dongTianOwner)) {
            forceLoadDongTian(dongTianOwner, dongTianLevel);
        }
    }

    /**
     * 當玩家離開洞天時調用
     * 在 DongTianManager.exitDongTian() 中調用
     */
    public static void onPlayerLeaveDongTian(ServerPlayer player, UUID dongTianOwner, ServerLevel dongTianLevel) {
        playersInDongTian.remove(player.getUUID());

        // 檢查是否還有其他玩家在這個洞天中
        if (!hasPlayersInDongTian(dongTianOwner, dongTianLevel)) {
            // 標記為待卸載
            long unloadTime = System.currentTimeMillis() +
                    (DongTianConfig.UNLOAD_DELAY_SECONDS * 1000L);
            pendingUnload.put(dongTianOwner, unloadTime);
        }
    }

    /**
     * 檢查某個洞天是否有玩家
     */
    private static boolean hasPlayersInDongTian(UUID dongTianOwner, ServerLevel dongTianLevel) {
        if (dongTianLevel == null) return false;

        DongTianChunkAllocator allocator = DongTianChunkAllocator.get(dongTianLevel);
        ChunkPos dongTianChunk = allocator.allocateChunkForPlayer(dongTianOwner);
        int halfSize = DongTianConfig.DONG_TIAN_SIZE_IN_CHUNKS / 2;

        // 檢查洞天範圍內是否有玩家
        for (ServerPlayer player : dongTianLevel.players()) {
            ChunkPos playerChunk = new ChunkPos(player.blockPosition());

            // 檢查玩家是否在這個洞天的範圍內
            if (Math.abs(playerChunk.x - dongTianChunk.x) <= halfSize &&
                    Math.abs(playerChunk.z - dongTianChunk.z) <= halfSize) {
                return true;
            }
        }

        return false;
    }

    /**
     * 伺服器Tick事件 - 處理洞天更新和卸載
     */
    @SubscribeEvent
    public static void onServerTick(TickEvent.ServerTickEvent event) {
        if (event.phase != TickEvent.Phase.END) return;

        ServerLevel dongTianLevel = event.getServer().getLevel(DongTianConfig.DONG_TIAN_DIMENSION);
        if (dongTianLevel == null) return;

        long currentTime = System.currentTimeMillis();

        // 處理洞天的定時更新
        for (Map.Entry<UUID, Integer> entry : dongTianTickCounters.entrySet()) {
            UUID owner = entry.getKey();
            int counter = entry.getValue() + 1;

            if (counter >= DongTianConfig.TICK_INTERVAL) {
                // 執行洞天的tick更新
                tickDongTian(owner, dongTianLevel);
                dongTianTickCounters.put(owner, 0);
            } else {
                dongTianTickCounters.put(owner, counter);
            }
        }

        // 處理待卸載的洞天
        Iterator<Map.Entry<UUID, Long>> iterator = pendingUnload.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<UUID, Long> entry = iterator.next();
            if (currentTime >= entry.getValue()) {
                // 時間到了,卸載洞天
                unloadDongTian(entry.getKey(), dongTianLevel);
                iterator.remove();
            }
        }
    }

    /**
     * 執行洞天的tick更新
     * 用於自定義邏輯:靈田生長加速、煉丹爐運作等
     */
    private static void tickDongTian(UUID owner, ServerLevel level) {
        if (level == null) return;

        DongTianChunkAllocator allocator = DongTianChunkAllocator.get(level);
        ChunkPos centerPos = allocator.allocateChunkForPlayer(owner);
        int halfSize = DongTianConfig.DONG_TIAN_SIZE_IN_CHUNKS / 2;

        // 更新洞天範圍內的特殊邏輯
        for (int x = -halfSize; x <= halfSize; x++) {
            for (int z = -halfSize; z <= halfSize; z++) {
                ChunkPos chunkPos = new ChunkPos(centerPos.x + x, centerPos.z + z);

                // 確保區塊被載入
                if (level.hasChunk(chunkPos.x, chunkPos.z)) {
                    tickDongTianChunk(level, chunkPos, owner);
                }
            }
        }
    }

    /**
     * 對單個區塊執行tick更新
     * 在這裡添加你的自定義邏輯
     */
    private static void tickDongTianChunk(ServerLevel level, ChunkPos pos, UUID owner) {
        // TODO: 實作自定義邏輯
        // 例如:
        // 1. 靈田作物生長加速 (DongTianConfig.SPIRIT_FIELD_GROWTH_MULTIPLIER)
        // 2. 自動收集範圍內的掉落物
        // 3. 煉丹爐、煉器台的持續運作
        // 4. 玩家打坐時的靈力回復加成 (DongTianConfig.MEDITATION_RECOVERY_MULTIPLIER)
    }

    /**
     * 卸載洞天的區塊
     */
    private static void unloadDongTian(UUID owner, ServerLevel level) {
        if (level == null) return;

        DongTianChunkAllocator allocator = DongTianChunkAllocator.get(level);
        ChunkPos centerPos = allocator.allocateChunkForPlayer(owner);
        int halfSize = DongTianConfig.DONG_TIAN_SIZE_IN_CHUNKS / 2;

        // 卸載洞天範圍內的區塊
        for (int x = -halfSize; x <= halfSize; x++) {
            for (int z = -halfSize; z <= halfSize; z++) {
                ChunkPos chunkPos = new ChunkPos(centerPos.x + x, centerPos.z + z);

                // 移除強制載入標記
                level.setChunkForced(chunkPos.x, chunkPos.z, false);
            }
        }

        // 清理相關數據
        dongTianTickCounters.remove(owner);
        forceLoadedDongTians.remove(owner);

        System.out.println("Unloaded DongTian for player: " + owner);
    }

    /**
     * 強制載入洞天的區塊
     */
    public static void forceLoadDongTian(UUID owner, ServerLevel level) {
        if (level == null) return;

        DongTianChunkAllocator allocator = DongTianChunkAllocator.get(level);
        ChunkPos centerPos = allocator.allocateChunkForPlayer(owner);
        int halfSize = DongTianConfig.DONG_TIAN_SIZE_IN_CHUNKS / 2;

        for (int x = -halfSize; x <= halfSize; x++) {
            for (int z = -halfSize; z <= halfSize; z++) {
                ChunkPos chunkPos = new ChunkPos(centerPos.x + x, centerPos.z + z);
                level.setChunkForced(chunkPos.x, chunkPos.z, true);
            }
        }

        // 初始化tick計數器
        dongTianTickCounters.put(owner, 0);
        forceLoadedDongTians.add(owner);

        System.out.println("Force loaded DongTian for player: " + owner);
    }

    /**
     * 清理所有數據(伺服器關閉時)
     */
    public static void cleanup() {
        lastAccessTime.clear();
        playersInDongTian.clear();
        dongTianTickCounters.clear();
        pendingUnload.clear();
        forceLoadedDongTians.clear();
    }
}