package com.blacksnow1002.realmmod.client.spell.input;

import com.blacksnow1002.realmmod.RealmMod;
import com.blacksnow1002.realmmod.client.setup.ModKeyBindings;
import com.blacksnow1002.realmmod.common.network.ModMessages;
import com.blacksnow1002.realmmod.system.spell.network.C2S.LingMuSpellPacket;
import net.minecraft.client.Minecraft;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;


@Mod.EventBusSubscriber(modid = RealmMod.MOD_ID, value = Dist.CLIENT)
public class LingMuSpellInputHandler {

    @SubscribeEvent
    public static void onKeyInput(InputEvent.Key event) {

        Minecraft mc = Minecraft.getInstance();
        if(mc.player == null || mc.screen != null) {
            return;
        }

        if (ModKeyBindings.LING_MU_SPELL_KEY.consumeClick()) {
            ModMessages.sendToServer(new LingMuSpellPacket(0));
        }
    }
}
