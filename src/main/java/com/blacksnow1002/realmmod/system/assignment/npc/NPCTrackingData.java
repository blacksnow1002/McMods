package com.blacksnow1002.realmmod.system.assignment.npc;

import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.level.storage.DimensionDataStorage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class NPCTrackingData extends SavedData {
    private static final String DATA_NAME = "realmmod_npc_tracking";
    private static final Logger LOGGER = LoggerFactory.getLogger("RealmMod-NPCTracking");

    private final Map<String, UUID> spawnedNPCs = new HashMap<>();

    public NPCTrackingData() {
        LOGGER.info("[數據] 創建新的 NPCTrackingData");
    }

    public static NPCTrackingData load(CompoundTag tag, HolderLookup.Provider provider) {
        LOGGER.info("[數據] 從 NBT 加載數據");
        NPCTrackingData data = new NPCTrackingData();
        ListTag list = tag.getList("NPCs", Tag.TAG_COMPOUND);
        LOGGER.info("[數據] 找到 {} 個已保存的 NPC", list.size());

        for (Tag t : list) {
            CompoundTag npcTag = (CompoundTag) t;
            String npcId = npcTag.getString("npcId");
            UUID uuid = npcTag.getUUID("uuid");
            data.spawnedNPCs.put(npcId, uuid);
            LOGGER.info("[數據] 加載 NPC: {} -> {}", npcId, uuid);
        }
        return data;
    }

    @Override
    public CompoundTag save(CompoundTag tag, HolderLookup.Provider provider) {
        LOGGER.info("[數據] 保存 {} 個 NPC 到 NBT", spawnedNPCs.size());
        ListTag list = new ListTag();
        for (Map.Entry<String, UUID> entry : spawnedNPCs.entrySet()) {
            CompoundTag npcTag = new CompoundTag();
            npcTag.putString("npcId", entry.getKey());
            npcTag.putUUID("uuid", entry.getValue());
            list.add(npcTag);
            LOGGER.info("[數據] 保存 NPC: {} -> {}", entry.getKey(), entry.getValue());
        }
        tag.put("NPCs", list);
        return tag;
    }

    public static NPCTrackingData get(ServerLevel level) {
        DimensionDataStorage storage = level.getDataStorage();
        NPCTrackingData data = storage.computeIfAbsent(
                new SavedData.Factory<>(
                        NPCTrackingData::new,
                        NPCTrackingData::load,
                        null
                ),
                DATA_NAME
        );
        LOGGER.info("[數據] 獲取 NPCTrackingData，當前有 {} 個 NPC", data.spawnedNPCs.size());
        return data;
    }

    public boolean hasNPC(String npcId) {
        boolean has = spawnedNPCs.containsKey(npcId);
        LOGGER.info("[數據] 檢查 {} 是否存在: {}", npcId, has);
        return has;
    }

    public UUID getNPCUUID(String npcId) {
        return spawnedNPCs.get(npcId);
    }

    public void registerNPC(String npcId, UUID uuid) {
        LOGGER.info("[數據] 註冊 NPC: {} -> {}", npcId, uuid);
        spawnedNPCs.put(npcId, uuid);
        setDirty();
    }

    public void unregisterNPC(String npcId) {
        LOGGER.info("[數據] 取消註冊 NPC: {}", npcId);
        spawnedNPCs.remove(npcId);
        setDirty();
    }
}