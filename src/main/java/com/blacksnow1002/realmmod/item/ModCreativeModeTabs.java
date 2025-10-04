package com.blacksnow1002.realmmod.item;

import com.blacksnow1002.realmmod.RealmMod;
import com.blacksnow1002.realmmod.block.ModBlocks;
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

                    }).build());

    public static final RegistryObject<CreativeModeTab> EXAMPLE_BLOCK_TAB = CREATIVE_MODE_TABS.register("example_block_tab",
            () -> CreativeModeTab.builder().icon(() -> new ItemStack(ModBlocks.SPIRIT_STONE_BLOCK.get()))
                    .withTabsBefore(EXAMPLE_ITEM_TAB.getId())
                    .title(Component.translatable("creativetab.realmmod.example_blocks"))
                    .displayItems((itemDisplayParameters, output) -> {

                        output.accept(ModBlocks.SPIRIT_STONE_BLOCK.get());
                        output.accept(ModBlocks.ALEXANDRITE_BLOCK.get());
                        output.accept(ModBlocks.ALEXANDRITE_ORE.get());
                        output.accept(ModBlocks.ALEXANDRITE_DEEPSLATE_ORE.get());
                        output.accept(ModBlocks.RAW_ALEXANDRITE_BLOCK.get());
                        output.accept(ModBlocks.MAGIC_BLOCK.get());

                    }).build());

    public static void register(IEventBus eventBus) {
        CREATIVE_MODE_TABS.register(eventBus);
    }
}
