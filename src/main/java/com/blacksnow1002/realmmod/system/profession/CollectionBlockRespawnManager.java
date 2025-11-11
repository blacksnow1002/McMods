package com.blacksnow1002.realmmod.system.profession;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

import java.util.*;

/**
 * 採集方塊重生管理器
 * 統一管理所有被採集方塊的重生
 */
public class CollectionBlockRespawnManager {

    private static final int MAX_RESPAWNS_PER_TICK = 30;
    private static final int RESPAWN_TIME_TICKS = 60 * 20;

    private static final Queue<Map.Entry<BlockPos, RespawnData>> RESPAWN_QUEUE = new LinkedList<>();
    private static final Queue<Map.Entry<BlockPos, RespawnData>> PENDING_RESPAWNS = new LinkedList<>();

    private static int tickCounter = 0;

    public static void recordBroke(ServerLevel level, BlockPos pos, Block block, BlockState blockState) {
        BlockPos immutablePos = pos.immutable();

        RespawnData data = new RespawnData(
                level.dimension().location().toString(), // 維度ID
                block,
                blockState
        );

        RESPAWN_QUEUE.add(Map.entry(immutablePos, data));
    }

    /**
     * 每 tick 調用一次
     * 每 60 秒處理一次重生
     */
    public static void tick(ServerLevel level) {
        tickCounter++;

        // 每 60 秒處理一次重生
        if (tickCounter >= RESPAWN_TIME_TICKS) {
            tickCounter = 0;
            PENDING_RESPAWNS.addAll(RESPAWN_QUEUE);
            RESPAWN_QUEUE.clear();
        }

        // 每 tick 處理一小批
        processRespawnBatch(level, MAX_RESPAWNS_PER_TICK);
    }

    private static void processRespawnBatch(ServerLevel level, int maxCount) {
        String currentDimension = level.dimension().location().toString();
        int processed = 0;

        while (!PENDING_RESPAWNS.isEmpty() && processed < maxCount) {
            Map.Entry<BlockPos, RespawnData> entry = PENDING_RESPAWNS.poll();
            BlockPos pos = entry.getKey();
            RespawnData data = entry.getValue();

            // 只處理當前維度的方塊
            if (!data.dimension.equals(currentDimension)) {
                continue;
            }

            // 檢查該位置是否可以重生（是否為空氣或可替換）
            BlockState currentState = level.getBlockState(pos);
            if (currentState.isAir() || currentState.canBeReplaced()) {
                // 重新放置方塊
                level.setBlock(pos, data.blockState, 3);
            }

            processed++;
        }
    }

    /**
     * 獲取當前等待重生的方塊數量
     */
    public static int getWaitingRespawnCount() {
        return RESPAWN_QUEUE.size();
    }

    public static int getPendingRespawnCount() {
        return PENDING_RESPAWNS.size();
    }

    /**
     * 清空重生隊列（伺服器關閉時調用）
     */
    public static void clear() {
        RESPAWN_QUEUE.clear();
        PENDING_RESPAWNS.clear();
        tickCounter = 0;
    }

    /**
     * 重生數據類
     */
    private static class RespawnData {
        final String dimension;      // 維度ID
        final Block block;           // 方塊類型
        final BlockState blockState; // 方塊狀態

        RespawnData(String dimension, Block block, BlockState blockState) {
            this.dimension = dimension;
            this.block = block;
            this.blockState = blockState;
        }
    }
}