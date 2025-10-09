package com.blacksnow1002.realmmod.spell.spells;

import com.blacksnow1002.realmmod.capability.cultivation.CultivationRealm;
import com.blacksnow1002.realmmod.event.handlers.TeleportHandler;
import com.blacksnow1002.realmmod.spell.BaseSpell;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.phys.Vec3;

import java.util.UUID;


public class MarkedTeleportSpell extends BaseSpell {
    // 可傳送的最大距離（方塊）
    private static final double MAX_TELEPORT_DISTANCE = 500.0;

    @Override
    public String getName() {
        return "縮地成寸";
    }

    @Override
    public CultivationRealm getRequiredRealm() {
        return CultivationRealm.sixth;
    }

    @Override
    public int getCooldownTicks() {
        return 20 * 10; // 10秒冷卻
    }

    @Override
    public boolean cast(ServerPlayer player, ServerLevel level) {
        UUID playerId = player.getUUID();
        if (!TeleportHandler.hasMark(playerId)) {
            player.displayClientMessage(Component.translatable("message.realmmod.spell.teleport.no_mark_first"), true   );
            player.sendSystemMessage(Component.translatable("message.realmmod.spell.teleport.no_mark_second"));
            return false;
        }
        TeleportHandler.MarkedLocation marked = TeleportHandler.getMark(playerId);

        if (!level.dimension().location().toString().equals(marked.dimension)) {
            player.sendSystemMessage(Component.translatable("message.realmmod.spell.teleport.different_dimension"));
            return false;
        }

        Vec3 currentPos = player.position();
        double distance = currentPos.distanceTo(new Vec3(marked.x, marked.y, marked.z));

        // 檢查距離是否在範圍內
        if (distance > MAX_TELEPORT_DISTANCE) {
            player.displayClientMessage(Component.translatable("message.realmmod.spell.teleport.distance",
                    distance,
                    MAX_TELEPORT_DISTANCE),
                    true);
            return false;
        }

        // 傳送前的音效
        level.playSound(null, player.blockPosition(),
                SoundEvents.ENDERMAN_TELEPORT,
                SoundSource.PLAYERS, 1.0F, 1.0F);

        // 執行傳送
        player.teleportTo(marked.x, marked.y, marked.z);

        // 傳送後的音效
        level.playSound(null, marked.blockPos,
                SoundEvents.ENDERMAN_TELEPORT,
                SoundSource.PLAYERS, 1.0F, 1.2F);

        // 發送訊息
        player.sendSystemMessage(Component.translatable("message.realmmod.spell.teleport.success"));
        return true;
    }
}