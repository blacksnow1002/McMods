package com.blacksnow1002.realmmod;

import com.blacksnow1002.realmmod.common.registry.ModEntities;
import com.blacksnow1002.realmmod.system.achievement.command.AchievementCommand;
import com.blacksnow1002.realmmod.system.assignment.command.AssignmentCommands;
import com.blacksnow1002.realmmod.system.assignment.AssignmentRegistry;
import com.blacksnow1002.realmmod.system.assignment.AssignmentSystem;
import com.blacksnow1002.realmmod.system.assignment.npc.BaseNPC;
import com.blacksnow1002.realmmod.system.assignment.npc.NPCRegistry;
import com.blacksnow1002.realmmod.system.assignment.npc.NPCSpawner;
import com.blacksnow1002.realmmod.common.registry.block.ModBlocks;
import com.blacksnow1002.realmmod.common.registry.block.ModBlockEntities;
import com.blacksnow1002.realmmod.client.broadcast.ClientBroadcastHandler;
import com.blacksnow1002.realmmod.client.setup.ClientSetup;
import com.blacksnow1002.realmmod.system.dimension.dongtian.DongTianConfig;
import com.blacksnow1002.realmmod.system.dimension.dongtian.DongTianLifecycleManager;
import com.blacksnow1002.realmmod.common.registry.ModCreativeModeTabs;
import com.blacksnow1002.realmmod.common.registry.ModDataComponents;
import com.blacksnow1002.realmmod.common.registry.ModItems;
import com.blacksnow1002.realmmod.system.dimension.dongtian.command.DongTianCommand;
import com.blacksnow1002.realmmod.system.mailbox.command.MailCommand;
import com.blacksnow1002.realmmod.client.mailbox.screen.MailboxScreen;
import com.blacksnow1002.realmmod.system.economy.money.market.command.MarketCommand;
import com.blacksnow1002.realmmod.client.profession.alchemy.screen.AlchemyFurnaceScreen;
import com.blacksnow1002.realmmod.common.registry.ModMenuTypes;
import com.blacksnow1002.realmmod.common.network.ModMessages;
import com.blacksnow1002.realmmod.system.profession.harvest.command.ProfessionHarvestCommand;
import com.blacksnow1002.realmmod.system.spell.SpellRegistry;
import com.blacksnow1002.realmmod.system.cultivation.command.BreakthroughCommand;
import com.blacksnow1002.realmmod.system.cultivation.command.SetRealmCommand;
import com.blacksnow1002.realmmod.system.technique.TechniqueRegistry;
import com.blacksnow1002.realmmod.system.technique.TechniqueSystem;
import com.blacksnow1002.realmmod.system.title.TitleRegistry;
import com.blacksnow1002.realmmod.system.title.TitleSystem;
import com.blacksnow1002.realmmod.system.title.command.TitleCommand;
import com.mojang.logging.LogUtils;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.BuildCreativeModeTabContentsEvent;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.event.server.ServerStartedEvent;
import net.minecraftforge.event.server.ServerStartingEvent;
import net.minecraftforge.event.server.ServerStoppingEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.slf4j.Logger;

import static com.blacksnow1002.realmmod.system.assignment.npc.NPCSpawner.isNPCPresent;

// The value here should match an entry in the META-INF/mods.toml file
@Mod(RealmMod.MOD_ID)
public class RealmMod
{
    // Define mod id in a common place for everything to reference
    public static final String MOD_ID = "realmmod";
    // Directly reference a slf4j logger
    public static final Logger LOGGER = LogUtils.getLogger();
    // Create a Deferred Register to hold Blocks which will all be registered under the "examplemod" namespace

    public RealmMod(FMLJavaModLoadingContext context) {
        IEventBus modEventBus = context.getModEventBus();

        // Register the commonSetup method for modLoading
        modEventBus.addListener(this::commonSetup);

        ModItems.register(modEventBus);
        ModBlocks.register(modEventBus);
        ModBlockEntities.register(modEventBus);
        ModDataComponents.DATA_COMPONENT_TYPES.register(modEventBus);
        ModCreativeModeTabs.register(modEventBus);
        ModEntities.ENTITY_TYPES.register(modEventBus);

        // Register ourselves for server and other game events we are interested in
        MinecraftForge.EVENT_BUS.register(this);

        // Register broadcast system
        MinecraftForge.EVENT_BUS.register(ClientBroadcastHandler.class);

        // Register the item to a creative tab
        modEventBus.addListener(this::addCreative);

        ModMenuTypes.MENUS.register(modEventBus);

        // Register our mod's ForgeConfigSpec so that Forge can create and load the config file for us
        context.registerConfig(ModConfig.Type.COMMON, Config.SPEC);


        DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> {
            modEventBus.addListener(ClientSetup::registerRenderers);
            modEventBus.addListener(ClientSetup::registerLayerDefinitions);
        });
    }

    private void commonSetup(final FMLCommonSetupEvent event) {
        LOGGER.info("神通系統初始化中...");
        event.enqueueWork(() -> {
            ModMessages.register();
        });
        SpellRegistry.registerAll();
        LOGGER.info("神通系統初始化完成，已註冊 {} 個神通", SpellRegistry.getAllSpells().size());

        LOGGER.info("任務系統初始化中...");
        AssignmentRegistry.registerAll();
        LOGGER.info("任務系統初始化完成，已註冊 {} 個任務", AssignmentSystem.getInstance().getAllAssignments().size());

        LOGGER.info("NPC初始化中...");
        NPCRegistry.registerAll();
        LOGGER.info("NPC初始化完成，已註冊 {} 個NPC", NPCRegistry.getInstance().getAllNPCs().size());

        LOGGER.info("稱號系統初始化中...");
        TitleRegistry.registerAll();
        LOGGER.info("稱號系統初始化完成，已註冊 {} 個稱號", TitleSystem.getInstance().getAllTitles().size());

        LOGGER.info("功法系統初始化中...");
        TechniqueSystem.init();
        TechniqueRegistry.registerAll();
        LOGGER.info("功法系統初始化完成，已註冊 {} 個功法", TechniqueSystem.getInstance().getAllTechniques().size());
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
    public void onServerStarted(ServerStartedEvent event) {
        MinecraftServer server = event.getServer();
        ServerLevel level = server.getLevel(Level.OVERWORLD);
        if (level == null) return;

        RealmMod.LOGGER.info("[NPC 系統] 開始生成 NPC...");

        // 從註冊表中獲取所有 NPC
        for (BaseNPC npc : NPCRegistry.getInstance().getAllNPCs()) {

            // 若 NPC 已存在世界中，就跳過
            if (isNPCPresent(level, npc.getNpcId())) {
                RealmMod.LOGGER.info("[NPC 系統] 已存在 NPC：{} ({})", npc.getNpcName(), npc.getNpcId());
                continue;
            }

            // 生成實體
            NPCSpawner.spawnNPC(level, npc.getNpcId(),0,-60, 0);
            RealmMod.LOGGER.info("[NPC 系統] 已生成 NPC：{} ({})", npc.getNpcName(), npc.getNpcId());
        }

        RealmMod.LOGGER.info("[NPC 系統] 所有 NPC 已生成完畢。");
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
        AssignmentCommands.register(event.getDispatcher());
        TitleCommand.register(event.getDispatcher());
        AchievementCommand.register(event.getDispatcher());
        MailCommand.register(event.getDispatcher(), event.getBuildContext());
        MarketCommand.register(event.getDispatcher());
        ProfessionHarvestCommand.register(event.getDispatcher());

    }

    // You can use EventBusSubscriber to automatically register all static methods in the class annotated with @SubscribeEvent
    @Mod.EventBusSubscriber(modid = MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
    public static class ClientModEvents {
        @SubscribeEvent
        public static void onClientSetup(FMLClientSetupEvent event) {
            event.enqueueWork(() -> {
                MenuScreens.register(ModMenuTypes.MAILBOX_MENU.get(), MailboxScreen::new);
                MenuScreens.register(ModMenuTypes.ALCHEMY_FURNACE_MENU.get(), AlchemyFurnaceScreen::new);
                LOGGER.info("[修仙模組] 郵箱 GUI 註冊完成");
            });
        }
    }
}
