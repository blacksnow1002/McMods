package com.blacksnow1002.realmmod.spell.spells;

import com.blacksnow1002.realmmod.capability.CultivationRealm;
import com.blacksnow1002.realmmod.capability.ModCapabilities;
import com.blacksnow1002.realmmod.event.handlers.DelayedTaskHandler;
import com.blacksnow1002.realmmod.network.ModMessages;
import com.blacksnow1002.realmmod.network.packets.LingMuSyncPacket;
import com.blacksnow1002.realmmod.spell.BaseSpell;
import com.google.common.graph.Network;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraftforge.common.capabilities.Capability;

public class LingMuSpell extends BaseSpell {

    @Override
    public String getName() {
        return "靈目術";
    }

    @Override
    public CultivationRealm getRequiredRealm() {
        return CultivationRealm.third;
    }

    @Override
    public int getCooldownTicks() {
        return 20 * 10;
    }

    @Override
    public boolean cast(ServerPlayer player, ServerLevel level) {
        var tag = player.getPersistentData();

        tag.putBoolean("LingMuActive", true);
        player.displayClientMessage(Component.translatable(
                "message.realmmod.spell.ling_mu.start"),
                true);
        ModMessages.sendToPlayer(new LingMuSyncPacket(true), player);

        DelayedTaskHandler.schedule(() -> {
            tag.putBoolean("LingMuActive", false);
            player.displayClientMessage(Component.translatable(
                    "message.realmmod.spell.ling_mu.end"),
                    true);
            ModMessages.sendToPlayer(new LingMuSyncPacket(false), player);
        }, 200);
        return true;
    }
}
