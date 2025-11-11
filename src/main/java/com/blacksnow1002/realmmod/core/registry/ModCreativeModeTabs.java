package com.blacksnow1002.realmmod.core.registry;

import com.blacksnow1002.realmmod.RealmMod;
import com.blacksnow1002.realmmod.core.registry.block.ModBlocks;
import com.blacksnow1002.realmmod.system.profession.harvest.item.HarvestToolItem;
import com.blacksnow1002.realmmod.system.profession.ToolFactory;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

public class ModCreativeModeTabs {
    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS =
            DeferredRegister.create(Registries.CREATIVE_MODE_TAB, RealmMod.MOD_ID);

    public static final RegistryObject<CreativeModeTab> EXAMPLE_ITEM_TAB = CREATIVE_MODE_TABS.register("example_item_tab",
            () -> CreativeModeTab.builder().icon(() -> new ItemStack(ModItems.SPIRIT_STONE_LOW.get()))
                    .title(Component.translatable("creativetab.realmmod.example_items"))
                    .displayItems((itemDisplayParameters, output) -> {
                        output.accept(ModItems.SPIRIT_STONE_LOW.get());
                        output.accept(ModItems.SPIRIT_STONE_MIDDLE.get());
                        output.accept(ModItems.SPIRIT_STONE_HIGH.get());
                        output.accept(ModItems.MAGIC_DRUG.get());
                        output.accept(ModItems.ALEXANDRITE.get());
                        output.accept(ModItems.RAW_ALEXANDRITE.get());
                        output.accept(ModItems.CHISEL.get());
                        output.accept(ModItems.KOHLRABI.get());
                        output.accept(ModItems.SPIRIT_FRUIT.get());
                        output.accept(ModItems.AURORA_ASHES.get());

                        output.accept(ModItems.TECHNIQUE.get());

                    }).build());

    public static final RegistryObject<CreativeModeTab> EXAMPLE_BLOCK_TAB = CREATIVE_MODE_TABS.register("example_block_tab",
            () -> CreativeModeTab.builder().icon(() -> new ItemStack(ModBlocks.SPIRIT_STONE_BLOCK.get()))
                    .withTabsBefore(EXAMPLE_ITEM_TAB.getId())
                    .title(Component.translatable("creativetab.realmmod.example_blocks"))
                    .displayItems((itemDisplayParameters, output) -> {

                        output.accept(ModBlocks.SPIRIT_ORE.get());
                        output.accept(ModBlocks.SPIRIT_STONE_BLOCK.get());
                        output.accept(ModBlocks.ALEXANDRITE_BLOCK.get());
                        output.accept(ModBlocks.ALEXANDRITE_ORE.get());
                        output.accept(ModBlocks.ALEXANDRITE_DEEPSLATE_ORE.get());
                        output.accept(ModBlocks.RAW_ALEXANDRITE_BLOCK.get());

                    }).build());

    public static final RegistryObject<CreativeModeTab> ELIXIR_TAB = CREATIVE_MODE_TABS.register("elixir_tab",
            () -> CreativeModeTab.builder().icon(() -> new ItemStack(ModItems.FOUNDATION_BUILD_ELIXIR.get()))
                    .withTabsBefore(EXAMPLE_BLOCK_TAB.getId())
                    .title(Component.translatable("creativetab.realmmod.elixir"))
                    .displayItems((itemDisplayParameters, output) -> {

                        output.accept(ModItems.FOUNDATION_BUILD_ELIXIR.get());
                        output.accept(ModItems.HEALING_PILL_9.get());
                        output.accept(ModItems.ALCHEMY_WASTE_ITEM.get());
                    }).build());

    public static final RegistryObject<CreativeModeTab> HARVEST_TOOlS_TAB = CREATIVE_MODE_TABS.register("harvest_tool_tab",
            () -> CreativeModeTab.builder().icon(() -> new ItemStack(ModItems.HARVEST_TOOL_9_MORTAL.get()))
                    .withTabsBefore(ELIXIR_TAB.getId())
                    .title(Component.translatable("creativetab.realmmod.harvest_tools"))
                    .displayItems((itemDisplayParameters, output) -> {

                        output.accept(ToolFactory.createInitializedTool((HarvestToolItem) ModItems.HARVEST_TOOL_9_HEAVEN.get()));
                        output.accept(ModItems.HARVEST_TOOL_9_EARTH.get());
                        output.accept(ModItems.HARVEST_TOOL_9_MYSTIC.get());
                        output.accept(ModItems.HARVEST_TOOL_9_MORTAL.get());

                        output.accept(ModBlocks.MORTAL_ALCHEMY_9_TOOL.get());
                        output.accept(ModBlocks.MYSTIC_ALCHEMY_9_TOOL.get());
                        output.accept(ModBlocks.EARTH_ALCHEMY_9_TOOL.get());
                        output.accept(ModBlocks.HEAVEN_ALCHEMY_9_TOOL.get());
                    }).build());

    public static final RegistryObject<CreativeModeTab> HARVESTABLE_BLOCKS_TAB = CREATIVE_MODE_TABS.register("harvestable_blocks_tab",
            () -> CreativeModeTab.builder().icon(() -> new ItemStack(ModBlocks.HARVESTABLE_BLOCK_9_COMMON_1.get()))
                    .withTabsBefore(HARVEST_TOOlS_TAB.getId())
                    .title(Component.translatable("creativetab.realmmod.harvestable_blocks"))
                    .displayItems((itemDisplayParameters, output) -> {

                        output.accept(ModBlocks.HARVESTABLE_BLOCK_9_COMMON_1.get());
                        output.accept(ModBlocks.HARVESTABLE_BLOCK_9_COMMON_2.get());
                        output.accept(ModBlocks.HARVESTABLE_BLOCK_9_COMMON_3.get());
                        output.accept(ModBlocks.HARVESTABLE_BLOCK_9_RARE_1.get());
                        output.accept(ModBlocks.HARVESTABLE_BLOCK_9_RARE_2.get());
                        output.accept(ModBlocks.HARVESTABLE_BLOCK_9_TREASURE_1.get());

                        output.accept(ModItems.TOOL_REFORGE_STONE.get());

                        output.accept(ModItems.ALCHEMY_RECIPE_HEALING_9.get());
                    }).build());

    public static void register(IEventBus eventBus) {
        CREATIVE_MODE_TABS.register(eventBus);
    }
}
