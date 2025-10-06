package com.blacksnow1002.realmmod.dimension.dong_tian;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;

import static com.blacksnow1002.realmmod.dimension.dong_tian.DongTianConfig.DONG_TIAN_DIMENSION;

/**
 * 洞天核心管理器
 * 處理玩家進入/離開洞天的主要邏輯
 */
public class DongTianManager {

    /**
     * 玩家進入洞天
     */
    public static boolean enterDongTian(ServerPlayer player) {
        // 1. 檢查是否已解鎖洞天
        PlayerDongTianData dongTianData = PlayerDongTianData.get(player);
        if (!dongTianData.isDongTianUnlocked()) {
            player.sendSystemMessage(Component.literal("§c你尚未開闢洞天！"));
            return false;
        }

        // 2. 檢查是否已經在洞天內
        if (player.level().dimension() == DONG_TIAN_DIMENSION) {
            player.sendSystemMessage(Component.literal("§c你已經在洞天之中！"));
            return false;
        }

        // 3. 保存當前位置
        dongTianData.saveExitPoint(player);
        dongTianData.saveToPlayer(player);

        // 4. 獲取洞天維度
        MinecraftServer server = player.getServer();
        if (server == null) return false;

        ServerLevel dongTianWorld = server.getLevel(DONG_TIAN_DIMENSION);
        if (dongTianWorld == null) {
            player.sendSystemMessage(Component.literal("§c洞天維度未正確初始化！"));
            return false;
        }

        // 5. 獲取玩家的洞天位置
        DongTianChunkAllocator allocator = DongTianChunkAllocator.get(dongTianWorld);
        ChunkPos dongTianChunk = allocator.allocateChunkForPlayer(player.getUUID());

        // 6. 計算傳送座標（洞天中心）
        int centerX = dongTianChunk.x * 16 + 8;
        int centerZ = dongTianChunk.z * 16 + 8;
        int spawnY = DongTianConfig.SPAWN_Y;

        // 7. 確保目標區塊已載入
        ensureChunksLoaded(dongTianWorld, dongTianChunk);

        // 9. 傳送玩家
        try {
            player.teleportTo(dongTianWorld, centerX + 0.5, spawnY, centerZ + 0.5, player.getYRot(), player.getXRot());
            DongTianLifecycleManager.onPlayerEnterDongTian(player, player.getUUID(), dongTianWorld);
        } catch (Exception e) {
            player.sendSystemMessage(Component.literal("§c洞天傳送失敗，已返回主世界"));
            player.teleportTo(player.server.overworld(), 0.5, 100, 0.5, 0, 0);
        }

        player.sendSystemMessage(Component.literal("§a你進入了自己的洞天福地"));
        return true;
    }

    /**
     * 玩家離開洞天，返回進入前的位置
     */
    public static boolean exitDongTian(ServerPlayer player) {
        // 1. 檢查是否在洞天內
        if (player.level().dimension() != DONG_TIAN_DIMENSION) {
            player.sendSystemMessage(Component.literal("§c你不在洞天之中！"));
            return false;
        }

        ServerLevel dongTianWorld = (ServerLevel) player.level();

        // 2. 獲取返回位置
        PlayerDongTianData dongTianData = PlayerDongTianData.get(player);
        BlockPos returnPos = dongTianData.getLastExitPos();

        if (returnPos == null) {
            // 如果沒有記錄，返回主世界出生點
            MinecraftServer server = player.getServer();
            if (server == null) return false;

            ServerLevel overworld = server.overworld();
            returnPos = overworld.getSharedSpawnPos();
            player.teleportTo(overworld, returnPos.getX(), returnPos.getY(), returnPos.getZ(), 0, 0);
        } else {
            // 返回記錄的位置
            MinecraftServer server = player.getServer();
            if (server == null) return false;

            ServerLevel targetWorld = server.getLevel(dongTianData.getLastDimension());
            if (targetWorld == null) {
                targetWorld = server.overworld();
            }

            player.teleportTo(
                    targetWorld,
                    returnPos.getX() + 0.5,
                    returnPos.getY(),
                    returnPos.getZ() + 0.5,
                    player.getYRot(),
                    player.getXRot()
            );
        }

        DongTianLifecycleManager.onPlayerLeaveDongTian(player, player.getUUID(), dongTianWorld);
        player.sendSystemMessage(Component.literal("§a你離開了洞天"));
        return true;
    }

    /**
     * 解鎖洞天（玩家達到煉虛期時調用）
     */
    public static void unlockDongTian(ServerPlayer player) {
        PlayerDongTianData dongTianData = PlayerDongTianData.get(player);

        if (dongTianData.isDongTianUnlocked()) {
            player.sendSystemMessage(Component.literal("§e你已經開闢過洞天了"));
            return;
        }

        dongTianData.unlockDongTian();
        dongTianData.saveToPlayer(player);

        MinecraftServer server = player.getServer();
        if (server == null) return;

        ServerLevel dongTianWorld = server.getLevel(DONG_TIAN_DIMENSION);
        if (dongTianWorld == null) {
            player.sendSystemMessage(Component.literal("§c洞天維度未正確初始化！"));
            return;
        }

        // 分配洞天區塊
        DongTianChunkAllocator allocator = DongTianChunkAllocator.get(dongTianWorld);
        ChunkPos dongTianChunk = allocator.allocateChunkForPlayer(player.getUUID());

        // 每個洞天寬度（可改成常量）
                final int DONG_TIAN_SIZE = 64;

        // 洞天左上角（以 Chunk 為基準）
                int baseX = dongTianChunk.x * 16;
                int baseZ = dongTianChunk.z * 16;

        // 中心位置 = 左上角 + 一半寬度
                int centerX = baseX + DONG_TIAN_SIZE / 2;
                int centerZ = baseZ + DONG_TIAN_SIZE / 2;
                int spawnY = DongTianConfig.SPAWN_Y;

        generateInitialPlatform(dongTianWorld, new BlockPos(centerX, spawnY - 1, centerZ));


        player.sendSystemMessage(Component.literal("§6恭喜！你成功開闢洞天！"));
        player.sendSystemMessage(Component.literal("§e使用 /dongTian enter 進入你的洞天"));

    }

    /**
     * 確保洞天區塊已載入
     */
    private static void ensureChunksLoaded(ServerLevel world, ChunkPos centerChunk) {
        int halfSize = DongTianConfig.DONG_TIAN_SIZE_IN_CHUNKS / 2;

        for (int x = -halfSize; x <= halfSize; x++) {
            for (int z = -halfSize; z <= halfSize; z++) {
                ChunkPos targetChunk = new ChunkPos(centerChunk.x + x, centerChunk.z + z);
                world.getChunk(targetChunk.x, targetChunk.z);
            }
        }
    }

    /**
     * 生成初始平台
     * 在玩家第一次進入洞天時創建一個基礎平台
     */
    private static void generateInitialPlatform(ServerLevel world, BlockPos centerPos) {
        // 設定平台半徑（64×64 的一半 = 32）
        int size = 64;

        // 從下往上生成三層：石頭 → 泥土 → 草地
        for (int x = -size; x < 0; x++) {
            for (int z = -size; z < 0; z++) {
                BlockPos grassPos = centerPos.offset(x, 0, z);
                world.setBlock(grassPos, net.minecraft.world.level.block.Blocks.GRASS_BLOCK.defaultBlockState(), 3);
            }
        }
    }

    /**
     * 檢查指定位置是否屬於某個玩家的洞天
     */
    public static boolean isPlayerOwnDongTian(ServerLevel world, BlockPos pos, ServerPlayer player) {
        if (world.dimension() != DONG_TIAN_DIMENSION) {
            return false;
        }

        DongTianChunkAllocator allocator = DongTianChunkAllocator.get(world);
        ChunkPos chunkPos = new ChunkPos(pos);

        java.util.UUID owner = allocator.getOwnerAtPosition(chunkPos);
        return owner != null && owner.equals(player.getUUID());
    }
}