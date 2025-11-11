package com.blacksnow1002.realmmod.system.spell.spells;

import com.blacksnow1002.realmmod.system.cultivation.CultivationRealm;
import com.blacksnow1002.realmmod.util.DelayedTaskHandler;
import com.blacksnow1002.realmmod.common.network.ModMessages;
import com.blacksnow1002.realmmod.system.spell.network.S2C.LingMuSyncPacket;
import com.blacksnow1002.realmmod.system.spell.BaseSpell;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;

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
