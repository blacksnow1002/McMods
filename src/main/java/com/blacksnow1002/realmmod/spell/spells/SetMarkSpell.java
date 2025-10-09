package com.blacksnow1002.realmmod.spell.spells;

import com.blacksnow1002.realmmod.capability.cultivation.CultivationRealm;
import com.blacksnow1002.realmmod.handlers.player.TeleportHandler;
import com.blacksnow1002.realmmod.spell.BaseSpell;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.phys.Vec3;

public class SetMarkSpell extends BaseSpell {

    @Override
    public String getName() {
        return "定點標記";
    }

    @Override
    public CultivationRealm getRequiredRealm() {
        return CultivationRealm.sixth;
    }

    @Override
    public int getCooldownTicks() {
        return 20 * 5; // 10秒冷卻
    }

    @Override
    public boolean cast(ServerPlayer player, ServerLevel level) {
        Vec3 pos = player.position();
        BlockPos blockPos = player.blockPosition();

        TeleportHandler.setMark(
                player.getUUID(),
                level.dimension().location().toString(),
                pos.x, pos.y, pos.z,
                blockPos
        );

        // 播放音效
        level.playSound(null, blockPos,
                SoundEvents.ENCHANTMENT_TABLE_USE,
                SoundSource.PLAYERS, 1.0F, 1.0F);

        player.displayClientMessage(Component.translatable(
                "message.realmmod.spell.set_mark.success"),
                true);
        player.sendSystemMessage(Component.translatable("message.realmmod.spell.set_mark.can_teleport"));

        return true;
    }
}