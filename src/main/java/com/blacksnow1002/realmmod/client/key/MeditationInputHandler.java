package com.blacksnow1002.realmmod.client.key;

import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import com.blacksnow1002.realmmod.RealmMod;

@Mod.EventBusSubscriber(modid = RealmMod.MOD_ID, value = Dist.CLIENT)
public class MeditationInputHandler {
    private static int meditateHoldTicks = 0;
    private static boolean isMeditating = false;

    private static final int HOLD_TIME = 60; // 約3秒（20tick * 3）

    @SubscribeEvent
    public static void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase != TickEvent.Phase.END) return;
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null) return;

        boolean holdingKey = ModKeyBindings.MEDITATION_KEY.isDown() && mc.player.isShiftKeyDown();

        if (holdingKey && !isMeditating) {
            meditateHoldTicks++;
            if (meditateHoldTicks >= HOLD_TIME) {
                isMeditating = true;
                meditateHoldTicks = 0;
            }
        }
    }
}
