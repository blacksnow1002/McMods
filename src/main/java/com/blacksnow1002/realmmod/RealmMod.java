package com.blacksnow1002.realmmod;

import com.blacksnow1002.realmmod.block.ModBlocks;
import com.blacksnow1002.realmmod.capability.ModCapabilities;
import com.blacksnow1002.realmmod.command.BreakthroughCommand;
import com.blacksnow1002.realmmod.command.SetRealmCommand;
import com.blacksnow1002.realmmod.dimension.dong_tian.DongTianCommand;
import com.blacksnow1002.realmmod.dimension.dong_tian.DongTianConfig;
import com.blacksnow1002.realmmod.dimension.dong_tian.DongTianLifecycleManager;
import com.blacksnow1002.realmmod.item.ModCreativeModeTabs;
import com.blacksnow1002.realmmod.item.ModItems;
import com.blacksnow1002.realmmod.network.ModMessages;
import com.blacksnow1002.realmmod.network.packets.RealmSyncPacket;
import com.blacksnow1002.realmmod.spell.SpellRegistry;
import com.mojang.logging.LogUtils;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.BuildCreativeModeTabContentsEvent;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.server.ServerStartingEvent;
import net.minecraftforge.event.server.ServerStoppingEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.slf4j.Logger;

// The value here should match an entry in the META-INF/mods.toml file
@Mod(RealmMod.MOD_ID)
public class RealmMod
{
    // Define mod id in a common place for everything to reference
    public static final String MOD_ID = "realmmod";
    // Directly reference a slf4j logger
    private static final Logger LOGGER = LogUtils.getLogger();
    // Create a Deferred Register to hold Blocks which will all be registered under the "examplemod" namespace

    public RealmMod(FMLJavaModLoadingContext context) {
        IEventBus modEventBus = context.getModEventBus();

        // Register the commonSetup method for modloading
        modEventBus.addListener(this::commonSetup);

        ModItems.register(modEventBus);
        ModBlocks.register(modEventBus);
        ModCreativeModeTabs.register(modEventBus);

        // Register ourselves for server and other game events we are interested in
        MinecraftForge.EVENT_BUS.register(this);


        // Register the item to a creative tab
        modEventBus.addListener(this::addCreative);

        // Register our mod's ForgeConfigSpec so that Forge can create and load the config file for us
        context.registerConfig(ModConfig.Type.COMMON, Config.SPEC);
    }

    private void commonSetup(final FMLCommonSetupEvent event) {
        event.enqueueWork(() -> {
            ModMessages.register();
        });
        SpellRegistry.registerAll();
        RealmMod.LOGGER.info("[修仙模組] 已載入法術系統，共註冊 " + SpellRegistry.getAllSpells().size() + " 種法術。");
    }

    // Add the example block item to the building blocks tab
    private void addCreative(BuildCreativeModeTabContentsEvent event) {
    }

    // You can use SubscribeEvent and let the Event Bus discover methods to call
    @SubscribeEvent
    public void onServerStarting(ServerStartingEvent event) {
        DongTianConfig.initDimensionKeys();
        RealmMod.LOGGER.info("[修仙模組] 已載入洞天系統");
    }

    @SubscribeEvent
    public static void onServerStopping(ServerStoppingEvent event) {
        DongTianLifecycleManager.cleanup();
    }

    @SubscribeEvent
    public void onRegisterCommand(RegisterCommandsEvent event) {
        BreakthroughCommand.register(event.getDispatcher());
        SetRealmCommand.register(event.getDispatcher());
        DongTianCommand.register(event.getDispatcher());

    }

    // You can use EventBusSubscriber to automatically register all static methods in the class annotated with @SubscribeEvent
    @Mod.EventBusSubscriber(modid = MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
    public static class ClientModEvents {
        @SubscribeEvent
        public static void onClientSetup(FMLClientSetupEvent event) {
            // Some client setup code
        }
    }
}
