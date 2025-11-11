package com.blacksnow1002.realmmod.core.datagen;

import com.blacksnow1002.realmmod.RealmMod;
import com.blacksnow1002.realmmod.core.registry.block.ModBlocks;
import com.blacksnow1002.realmmod.core.registry.ModItems;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.client.model.generators.ItemModelProvider;
import net.minecraftforge.client.model.generators.ModelFile;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.registries.ForgeRegistries;

public class ModItemModelProvider extends ItemModelProvider {
    public ModItemModelProvider(PackOutput output, ExistingFileHelper existingFileHelper) {
        super(output, RealmMod.MOD_ID, existingFileHelper);
    }

    @Override
    protected void registerModels() {
        basicItem(ModItems.SPIRIT_STONE_LOW.get());
        basicItem(ModItems.SPIRIT_STONE_MIDDLE.get());
        basicItem(ModItems.SPIRIT_STONE_HIGH.get());

        basicItem(ModItems.FOUNDATION_BUILD_ELIXIR.get());

        basicItem(ModItems.TECHNIQUE.get());

        basicItem(ModItems.SPIRIT_FRUIT.get());

        basicItem(ModItems.TOOL_REFORGE_STONE.get());

        basicItem(ModItems.ALCHEMY_WASTE_ITEM.get());

        basicItemWithVanillaParent(ModItems.HARVEST_TOOL_9_MORTAL.get(), "iron_hoe");
        basicItemWithVanillaParent(ModItems.HARVEST_TOOL_9_MYSTIC.get(), "iron_pickaxe");
        basicItemWithVanillaParent(ModItems.HARVEST_TOOL_9_EARTH.get(), "diamond_hoe");
        basicItemWithVanillaParent(ModItems.HARVEST_TOOL_9_HEAVEN.get(), "netherite_hoe");

        blockItemWithGenerated(ModBlocks.HARVESTABLE_BLOCK_9_COMMON_1.get());
        blockItemWithGenerated(ModBlocks.HARVESTABLE_BLOCK_9_COMMON_2.get());
        blockItemWithGenerated(ModBlocks.HARVESTABLE_BLOCK_9_COMMON_3.get());
        blockItemWithGenerated(ModBlocks.HARVESTABLE_BLOCK_9_RARE_1.get());
        blockItemWithGenerated(ModBlocks.HARVESTABLE_BLOCK_9_RARE_2.get());
        blockItemWithGenerated(ModBlocks.HARVESTABLE_BLOCK_9_TREASURE_1.get());

        blockItemWithExistingModel(ModBlocks.MORTAL_ALCHEMY_9_TOOL.get(), "furnace");
        blockItemWithExistingModel(ModBlocks.MYSTIC_ALCHEMY_9_TOOL.get(), "furnace");
        blockItemWithExistingModel(ModBlocks.EARTH_ALCHEMY_9_TOOL.get(), "furnace");
        blockItemWithExistingModel(ModBlocks.HEAVEN_ALCHEMY_9_TOOL.get(), "furnace");

        getBuilder(ModItems.ALCHEMY_RECIPE_HEALING_9.getId().getPath())
                .parent(new ModelFile.UncheckedModelFile("item/generated"))
                .texture("layer0", modLoc("item/alchemy_recipe"));

        getBuilder(ModItems.HEALING_PILL_9.getId().getPath())
                .parent(new ModelFile.UncheckedModelFile("item/generated"))
                .texture("layer0", modLoc("item/alchemy_pill"));

    }

    private void basicItemWithVanillaParent(Item item, String vanillaItemName) {
        ResourceLocation itemId = ForgeRegistries.ITEMS.getKey(item);
        getBuilder(itemId.toString())
                .parent(new ModelFile.UncheckedModelFile(
                        ResourceLocation.withDefaultNamespace("item/" + vanillaItemName)));
    }

    private void blockItemWithGenerated(Block block) {
        ResourceLocation blockId = ForgeRegistries.BLOCKS.getKey(block);
        String path = blockId.getPath();

        getBuilder(RealmMod.MOD_ID + ":" + path)
                .parent(new ModelFile.UncheckedModelFile("item/generated"))
                .texture("layer0", modLoc("block/" + path))
                .renderType("cutout");
    }

    private void blockItemWithExistingModel(Block block, String vanillaBlockModel) {
        ResourceLocation blockId = ForgeRegistries.BLOCKS.getKey(block);
        getBuilder(blockId.getPath())
                .parent(new ModelFile.UncheckedModelFile(mcLoc("block/" + vanillaBlockModel)));
    }
}