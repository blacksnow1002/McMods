package com.blacksnow1002.realmmod.client.key;

import com.blacksnow1002.realmmod.network.StartMeditationPacket;
import com.blacksnow1002.realmmod.network.ModMessages;
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

    private static final int HOLD_TIME = 20; // 約3秒（20tick * 3）

    @SubscribeEvent
    public static void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase != TickEvent.Phase.END) return;
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null) return;

        boolean holdingKey = ModKeyBindings.MEDITATION_KEY.isDown();

        if (holdingKey) {
            meditateHoldTicks++;
            if (meditateHoldTicks == HOLD_TIME) {
                ModMessages.sendToServer(new StartMeditationPacket(1)); // keyId = 1
            }
        } else {
            meditateHoldTicks = 0;
        }
    }
}
