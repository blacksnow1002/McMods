package com.blacksnow1002.realmmod.dimension.dong_tian;

import net.minecraft.world.level.ChunkPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.core.HolderLookup;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * 洞天區塊分配器
 * 負責為每個玩家分配獨立的區塊空間
 * 使用SavedData持久化存儲分配信息
 */
public class DongTianChunkAllocator extends SavedData {

    private static final String DATA_NAME = "dong_tian_allocations";

    // 玩家UUID -> 洞天中心區塊座標
    private final Map<UUID, ChunkPos> playerChunkMap = new HashMap<>();

    // 當前已分配的洞天數量
    private int allocatedCount = 0;

    /**
     * 為玩家分配或獲取洞天區塊位置
     */
    public ChunkPos allocateChunkForPlayer(UUID playerUUID) {
        if (playerChunkMap.containsKey(playerUUID)) {
            return playerChunkMap.get(playerUUID);
        }

        // 計算新的洞天位置
        ChunkPos newChunkPos = calculateNewChunkPosition(allocatedCount);
        playerChunkMap.put(playerUUID, newChunkPos);
        allocatedCount++;

        setDirty(); // 標記需要保存
        return newChunkPos;
    }

    /**
     * 計算洞天位置
     * 使用網格布局，每個洞天之間有足夠間隔
     */
    private ChunkPos calculateNewChunkPosition(int index) {
        int totalSize = DongTianConfig.DONG_TIAN_SIZE_IN_CHUNKS + DongTianConfig.DONG_TIAN_SPACING;

        // 使用螺旋布局或網格布局
        int gridSize = 100; // 每行100個洞天
        int x = (index % gridSize) * totalSize;
        int z = (index / gridSize) * totalSize;

        return new ChunkPos(x, z);
    }

    /**
     * 獲取指定位置的洞天擁有者
     */
    public UUID getOwnerAtPosition(ChunkPos chunkPos) {
        for (Map.Entry<UUID, ChunkPos> entry : playerChunkMap.entrySet()) {
            ChunkPos dongTianCenter = entry.getValue();

            // 檢查是否在此洞天範圍內
            if (isInDongTianRange(chunkPos, dongTianCenter)) {
                return entry.getKey();
            }
        }
        return null;
    }

    /**
     * 檢查目標區塊是否在洞天範圍內
     */
    private boolean isInDongTianRange(ChunkPos target, ChunkPos center) {
        int halfSize = DongTianConfig.DONG_TIAN_SIZE_IN_CHUNKS / 2;

        int minX = center.x - halfSize;
        int maxX = center.x + halfSize;
        int minZ = center.z - halfSize;
        int maxZ = center.z + halfSize;

        return target.x >= minX && target.x < maxX &&
                target.z >= minZ && target.z < maxZ;
    }

    // ========== NBT序列化 ==========

    @Override
    public CompoundTag save(CompoundTag tag, HolderLookup.Provider provider) {
        CompoundTag allocationsTag = new CompoundTag();

        for (Map.Entry<UUID, ChunkPos> entry : playerChunkMap.entrySet()) {
            CompoundTag posTag = new CompoundTag();
            posTag.putInt("x", entry.getValue().x);
            posTag.putInt("z", entry.getValue().z);
            allocationsTag.put(entry.getKey().toString(), posTag);
        }

        tag.put("allocations", allocationsTag);
        tag.putInt("count", allocatedCount);

        return tag;
    }

    /**
     * 從NBT加載數據
     */
    public static DongTianChunkAllocator load(CompoundTag tag, HolderLookup.Provider provider) {
        DongTianChunkAllocator allocator = new DongTianChunkAllocator();

        CompoundTag allocationsTag = tag.getCompound("allocations");
        for (String uuidStr : allocationsTag.getAllKeys()) {
            UUID uuid = UUID.fromString(uuidStr);
            CompoundTag posTag = allocationsTag.getCompound(uuidStr);

            int x = posTag.getInt("x");
            int z = posTag.getInt("z");

            allocator.playerChunkMap.put(uuid, new ChunkPos(x, z));
        }

        allocator.allocatedCount = tag.getInt("count");

        return allocator;
    }

    // ========== SavedData獲取方法 ==========

    /**
     * 獲取或創建分配器實例
     */
    public static DongTianChunkAllocator get(ServerLevel level) {
        return level.getDataStorage().computeIfAbsent(
                new SavedData.Factory<>(
                        DongTianChunkAllocator::new,
                        DongTianChunkAllocator::load,
                        null
                ),
                DATA_NAME
        );
    }
}