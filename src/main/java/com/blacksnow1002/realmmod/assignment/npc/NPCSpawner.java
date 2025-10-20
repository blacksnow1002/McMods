package com.blacksnow1002.realmmod.assignment.npc;

import com.blacksnow1002.realmmod.ModEntities;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;


public class NPCSpawner {

    public static boolean isNPCPresent(ServerLevel level, String npcId) {
        for (Entity entity : level.getAllEntities()) {
            if (entity instanceof CustomNPCEntity customNPCEntity) {
                if (npcId.equals(customNPCEntity.getNpcId())) return true;
            }
        }
        return false;
    }

    public static void spawnNPC(ServerLevel level, String npcId, double x, double y, double z) {
        BaseNPC baseNpc = NPCRegistry.getInstance().getNPC(npcId);
        if (baseNpc == null) {
            System.out.println("NPC 不存在: " + npcId);
            return;
        }

        CustomNPCEntity entity = new CustomNPCEntity(ModEntities.CUSTOM_NPC.get(), level);
        entity.setNpcId(npcId);

        entity.setCustomName(Component.literal(baseNpc.getNpcName()));
        entity.setCustomNameVisible(true);

        entity.setPos(x, y, z);
        level.addFreshEntity(entity);
    }
}
