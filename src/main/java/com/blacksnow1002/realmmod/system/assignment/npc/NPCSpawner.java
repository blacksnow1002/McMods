package com.blacksnow1002.realmmod.system.assignment.npc;

import com.blacksnow1002.realmmod.core.registry.ModEntities;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.UUID;

public class NPCSpawner {
    private static final Logger LOGGER = LoggerFactory.getLogger("RealmMod-NPCSpawner");


    public static boolean isNPCPresent(ServerLevel level, String npcId) {
        NPCTrackingData data = NPCTrackingData.get(level);

        LOGGER.info("[檢查] 開始檢查 NPC: {}", npcId);

        if (!data.hasNPC(npcId)) {
            LOGGER.info("[檢查] NPC {} 不在追蹤列表中", npcId);
            return false;
        }
        LOGGER.info("[檢查] NPC {} 存在且有效", npcId);
        return true;
    }

    /**
     * 生成 NPC（用於服務器啟動）
     */
    public static void spawnNPC(ServerLevel level, String npcId, double x, double y, double z) {
        LOGGER.info("[生成] 嘗試生成 NPC: {}", npcId);

        BaseNPC baseNpc = NPCRegistry.getInstance().getNPC(npcId);
        if (baseNpc == null) {
            LOGGER.error("[生成] NPC {} 不存在於註冊表中", npcId);
            return;
        }

        CustomNPCEntity entity = new CustomNPCEntity(ModEntities.CUSTOM_NPC.get(), level);
        entity.setNpcId(npcId);
        entity.setCustomName(Component.literal(baseNpc.getNpcName()));
        entity.setCustomNameVisible(true);
        entity.setPos(x, y, z);

        level.addFreshEntity(entity);

        UUID entityUUID = entity.getUUID();
        LOGGER.info("[生成] 實體已添加，UUID: {}", entityUUID);

        NPCTrackingData.get(level).registerNPC(npcId, entityUUID);
        LOGGER.info("[生成] 成功生成並註冊 NPC: {} (UUID: {})", npcId, entityUUID);
    }

}