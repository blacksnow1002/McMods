package com.blacksnow1002.realmmod.handlers.tick;

import com.blacksnow1002.realmmod.RealmMod;
import com.blacksnow1002.realmmod.spell.BaseSpell;
import com.blacksnow1002.realmmod.spell.spells.SpiritOutSpell;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;


@Mod.EventBusSubscriber(modid = RealmMod.MOD_ID)
public class SpellTickHandler {
    @SubscribeEvent
    public static void onServerTick(TickEvent.ServerTickEvent event) {
        if (event.phase == TickEvent.Phase.END) {
            BaseSpell.tickAllCooldowns();

            event.getServer().getAllLevels().forEach(level -> {
                SpiritOutSpell.tickSpiritOuts(level);
            });
        }
    }

    @SubscribeEvent
    public static void onPlayerLogout(PlayerEvent.PlayerLoggedOutEvent event) {
        if (event.getEntity() instanceof ServerPlayer player) {
            SpiritOutSpell.onPlayerLogout(player);
        }
    }
}
