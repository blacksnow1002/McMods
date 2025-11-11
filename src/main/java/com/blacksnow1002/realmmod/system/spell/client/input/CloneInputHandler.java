package com.blacksnow1002.realmmod.system.spell.client.input;

import com.blacksnow1002.realmmod.RealmMod;
import com.blacksnow1002.realmmod.core.client.ModKeyBindings;
import com.blacksnow1002.realmmod.core.network.ModMessages;
import com.blacksnow1002.realmmod.system.spell.network.C2S.CloneSpellPacket;
import net.minecraft.client.Minecraft;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;


@Mod.EventBusSubscriber(modid = RealmMod.MOD_ID, value = Dist.CLIENT)
public class CloneInputHandler {

    @SubscribeEvent
    public static void onKeyInput(InputEvent.Key event) {

        Minecraft mc = Minecraft.getInstance();
        if(mc.player == null || mc.screen != null) {
            return;
        }

        if (ModKeyBindings.CLONE_SPELL_KEY.consumeClick()) {
            ModMessages.sendToServer(new CloneSpellPacket(0));
        }
    }
}
