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
            new KeyMapping("key.realmmod.meditate", GLFW.GLFW_KEY_F, "key.categories.realmmod");

    @SubscribeEvent
    public static void register(RegisterKeyMappingsEvent event) {
        event.register(MEDITATION_KEY);
    }
}

