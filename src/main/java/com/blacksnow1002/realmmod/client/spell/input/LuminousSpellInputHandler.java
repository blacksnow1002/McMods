package com.blacksnow1002.realmmod.client.spell.input;

import com.blacksnow1002.realmmod.RealmMod;
import com.blacksnow1002.realmmod.client.setup.ModKeyBindings;
import com.blacksnow1002.realmmod.common.network.ModMessages;
import com.blacksnow1002.realmmod.system.spell.network.C2S.SpellPacket;
import net.minecraft.client.Minecraft;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;


@Mod.EventBusSubscriber(modid = RealmMod.MOD_ID, value = Dist.CLIENT)
public class LuminousSpellInputHandler {

    @SubscribeEvent
    public static void onKeyInput(InputEvent.Key event) {
        System.out.println("客戶端：LuminousSpell KeyInput 事件觸發"); // Debug

        Minecraft mc = Minecraft.getInstance();
        if(mc.player == null || mc.screen != null) {
            return;
        }

        if (ModKeyBindings.LUMINOUS_SPELL_KEY.consumeClick()) {
            System.out.println("客戶端：Y 鍵被按下，發送封包");
            ModMessages.sendToServer(new SpellPacket(0));
        }
    }
}
