package com.blacksnow1002.realmmod.client.key.input_handlers;

import com.blacksnow1002.realmmod.client.key.ModKeyBindings;
import com.blacksnow1002.realmmod.network.packets.StartMeditationPacket;
import com.blacksnow1002.realmmod.network.ModMessages;
import net.minecraft.client.Minecraft;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import com.blacksnow1002.realmmod.RealmMod;

@Mod.EventBusSubscriber(modid = RealmMod.MOD_ID, value = Dist.CLIENT)
public class MeditationInputHandler {
    private static int meditateHoldTicks = 0;

    private static final int HOLD_TIME = 20;

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
