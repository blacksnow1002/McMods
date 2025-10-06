package com.blacksnow1002.realmmod.client.key.input_handlers;

import com.blacksnow1002.realmmod.RealmMod;
import com.blacksnow1002.realmmod.client.key.ModKeyBindings;
import com.blacksnow1002.realmmod.network.ModMessages;
import com.blacksnow1002.realmmod.network.packets.MarkedTeleportSpellPacket;
import net.minecraft.client.Minecraft;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;


@Mod.EventBusSubscriber(modid = RealmMod.MOD_ID, value = Dist.CLIENT)
public class MarkedTeleportSpellInputHandler {

    @SubscribeEvent
    public static void onKeyInput(InputEvent.Key event) {
        Minecraft mc = Minecraft.getInstance();
        if(mc.player == null || mc.screen != null) return;

        if (ModKeyBindings.TELEPORT_SPELL_KEY.isDown()) {
            ModMessages.sendToServer(new MarkedTeleportSpellPacket(0));
        }
    }
}
