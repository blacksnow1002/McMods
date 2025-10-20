package com.blacksnow1002.realmmod.handlers;

import com.blacksnow1002.realmmod.capability.ModCapabilities;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class PlayerReceiveHandler {
    private static final int TICK_INTERVAL = 20;

//    @SubscribeEvent
    public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
        if (event.phase != TickEvent.Phase.END) return;
        if (!(event.player instanceof ServerPlayer player)) return;

        if (player.tickCount % TICK_INTERVAL != 0) return;

        player.getCapability(ModCapabilities.PLAYER_TOTAL_ATTRIBUTE_CAP).ifPresent((cap) -> {
            player.getCapability(ModCapabilities.MANA_CAP).ifPresent((mana) -> {
                // ===== 生命回復 =====
                int healthReceiveAmount = cap.getPlayerTotalHealthReceive();
                if (healthReceiveAmount > 0 && player.getHealth() < player.getMaxHealth()) {
                    player.heal(healthReceiveAmount);
                }

                // ===== 真元回復 =====
                int manaReceiveAmount = cap.getPlayerTotalManaReceive();
                if (manaReceiveAmount > 0 && mana.getMana() < cap.getPlayerTotalMaxMana()) {
                    mana.addMana(manaReceiveAmount);
                }
            });
        });


    }
}
