package com.blacksnow1002.realmmod.event.handlers;

import com.blacksnow1002.realmmod.RealmMod;
import com.blacksnow1002.realmmod.spell.BaseSpell;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = RealmMod.MOD_ID)
public class SpellTickHandler {
    @SubscribeEvent
    public static void onServerTick(TickEvent.ServerTickEvent event) {
        if (event.phase == TickEvent.Phase.END) {
            BaseSpell.tickAllCooldowns();
        }
    }
}
