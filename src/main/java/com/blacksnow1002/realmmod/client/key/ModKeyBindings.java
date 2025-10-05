package com.blacksnow1002.realmmod.client.key;

import com.blacksnow1002.realmmod.RealmMod;
import net.minecraft.client.KeyMapping;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RegisterKeyMappingsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.lwjgl.glfw.GLFW;

@Mod.EventBusSubscriber(modid = RealmMod.MOD_ID, value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ModKeyBindings {
    public static final KeyMapping MEDITATION_KEY =
        new KeyMapping("key.realmmod.meditate", GLFW.GLFW_KEY_G, "key.categories.realmmod");
    public static final KeyMapping CULTIVATION_STATUS_KEY =
        new KeyMapping("key.realmmod.cultivation_status", GLFW.GLFW_KEY_R, "key.categories.realmmod");
    public static final KeyMapping LUMINOUS_SPELL_KEY =
            new KeyMapping("key.realmmod.luminous_spell", GLFW.GLFW_KEY_Y, "key.categories.realmmod");
    public static final KeyMapping LING_MU_SPELL_KEY =
            new KeyMapping("key.realmmod.ling_mu_spell", GLFW.GLFW_KEY_H, "key.categories.realmmod");

    @SubscribeEvent
    public static void register(RegisterKeyMappingsEvent event) {
        event.register(MEDITATION_KEY);
        event.register(CULTIVATION_STATUS_KEY);
        event.register(LUMINOUS_SPELL_KEY);
        event.register(LING_MU_SPELL_KEY);
    }
}

