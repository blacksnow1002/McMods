package com.blacksnow1002.realmmod.spell.spells;

import com.blacksnow1002.realmmod.capability.CultivationRealm;
import com.blacksnow1002.realmmod.event.handlers.DelayedTaskHandler;
import com.blacksnow1002.realmmod.network.ModMessages;
import com.blacksnow1002.realmmod.network.packets.LingMuSyncPacket;
import com.blacksnow1002.realmmod.spell.BaseSpell;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Abilities;

import java.util.UUID;

public class FlySpell extends BaseSpell {

    @Override
    public String getName() {
        return "御劍飛行";
    }

    @Override
    public CultivationRealm getRequiredRealm() {
        return CultivationRealm.fourth;
    }

    @Override
    public int getCooldownTicks() {
        return 20 * 10;
    }

    @Override
    public void cast(ServerPlayer player, ServerLevel level) {

        Abilities abilities = player.getAbilities();
        abilities.mayfly = true;
        player.onUpdateAbilities();

        player.sendSystemMessage(Component.translatable("message.realmmod.spell.fly.start"));

        DelayedTaskHandler.schedule(() -> {
            abilities.mayfly = false;
            abilities.flying = false;
            player.onUpdateAbilities();
            player.sendSystemMessage(Component.translatable("message.realmmod.spell.fly.end"));
        }, 60);


    }
}
