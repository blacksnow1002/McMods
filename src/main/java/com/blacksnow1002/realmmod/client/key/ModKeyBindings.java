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
    public static final KeyMapping FLY_SPELL_KEY =
            new KeyMapping("key.realmmod.fly_spell", GLFW.GLFW_KEY_J, "key.categories.realmmod");
    public static final KeyMapping SPIRIT_OUT_KEY =
            new KeyMapping("key.realmmod.spirit_out", GLFW.GLFW_KEY_K, "key.categories.realmmod");
    public static final KeyMapping SET_MARK_SPELL_KEY =
            new KeyMapping("key.realmmod.set_mark", GLFW.GLFW_KEY_I, "key.categories.realmmod");
    public static final KeyMapping MARKED_TELEPORT_SPELL_KEY =
            new KeyMapping("key.realmmod.marked_teleport", GLFW.GLFW_KEY_O, "key.categories.realmmod");
    public static final KeyMapping SHORT_TELEPORT_SPELL_KEY =
            new KeyMapping("key.realmmod.short_teleport", GLFW.GLFW_KEY_COMMA, "key.categories.realmmod");
    public static final KeyMapping GIANT_SPELL_KEY =
            new KeyMapping("key.realmmod.giant", GLFW.GLFW_KEY_PERIOD, "key.categories.realmmod");
    public static final KeyMapping CLONE_SPELL_KEY =
            new KeyMapping("key.realmmod.clone", GLFW.GLFW_KEY_SEMICOLON, "key.categories.realmmod");
    public static final KeyMapping TRANSFORM_SPELL_KEY =
            new KeyMapping("key.realmmod.transform", GLFW.GLFW_KEY_M, "key.categories.realmmod");
    public static final KeyMapping CREATE_SPELL_KEY =
            new KeyMapping("key.realmmod.create", GLFW.GLFW_KEY_N, "key.categories.realmmod");

    @SubscribeEvent
    public static void register(RegisterKeyMappingsEvent event) {
        event.register(MEDITATION_KEY);
        event.register(CULTIVATION_STATUS_KEY);
        event.register(LUMINOUS_SPELL_KEY);
        event.register(LING_MU_SPELL_KEY);
        event.register(FLY_SPELL_KEY);
        event.register(SPIRIT_OUT_KEY);
        event.register(SET_MARK_SPELL_KEY);
        event.register(MARKED_TELEPORT_SPELL_KEY);
        event.register(SHORT_TELEPORT_SPELL_KEY);
        event.register(GIANT_SPELL_KEY);
        event.register(CLONE_SPELL_KEY);
        event.register(TRANSFORM_SPELL_KEY);
        event.register(CREATE_SPELL_KEY);
    }
}

