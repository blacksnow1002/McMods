package com.blacksnow1002.realmmod.client.cultivation.input;

import com.blacksnow1002.realmmod.RealmMod;
import com.blacksnow1002.realmmod.client.setup.ModKeyBindings;
import com.blacksnow1002.realmmod.common.network.ModMessages;
import com.blacksnow1002.realmmod.system.cultivation.network.C2S.StartCultivationStatusPacket;
import net.minecraft.client.Minecraft;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;


@Mod.EventBusSubscriber(modid = RealmMod.MOD_ID, value = Dist.CLIENT)
public class CultivationStatusInputHandler {

    @SubscribeEvent
    public static void onKeyInput(InputEvent.Key event) {
        Minecraft mc = Minecraft.getInstance();
        if(mc.player == null || mc.screen != null) return;

        if (ModKeyBindings.CULTIVATION_STATUS_KEY.isDown()) {
            ModMessages.sendToServer(new StartCultivationStatusPacket(2));
        }
    }
}
