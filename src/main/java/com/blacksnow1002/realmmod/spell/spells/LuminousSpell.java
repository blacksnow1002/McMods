package com.blacksnow1002.realmmod.spell.spells;

import com.blacksnow1002.realmmod.spell.BaseSpell;
import com.blacksnow1002.realmmod.capability.CultivationRealm;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.core.particles.ParticleTypes;

public class LuminousSpell extends BaseSpell {

    @Override
    public String getName() {
        return "明光術";
    }

    @Override
    public CultivationRealm getRequiredRealm() {
        return CultivationRealm.second;
    }

    @Override
    public int getCooldownTicks() {
        return 20 * 60;
    }

    @Override
    public void cast(ServerPlayer player, ServerLevel level) {
        player.addEffect(new MobEffectInstance(MobEffects.NIGHT_VISION, 20 * 5, 0, false, false));
        level.playSound(null, player.blockPosition(), SoundEvents.AMETHYST_BLOCK_CHIME, SoundSource.PLAYERS, 1.0F, 1.4F);
        level.sendParticles(ParticleTypes.END_ROD, player.getX(), player.getEyeY(), player.getZ(), 15, 0.2, 0.2, 0.2, 0.01);
        player.displayClientMessage(Component.translatable(
                "message.realmmod.spell.luminous"),
                true);
    }
}
