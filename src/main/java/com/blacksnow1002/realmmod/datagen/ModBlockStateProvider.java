package com.blacksnow1002.realmmod.datagen;

import com.blacksnow1002.realmmod.RealmMod;
import com.blacksnow1002.realmmod.block.ModBlocks;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.client.model.generators.BlockStateProvider;
import net.minecraftforge.client.model.generators.ConfiguredModel;
import net.minecraftforge.client.model.generators.ModelFile;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModBlockStateProvider extends BlockStateProvider {
    public ModBlockStateProvider(PackOutput output, ExistingFileHelper exFileHelper) {
        super(output, RealmMod.MOD_ID, exFileHelper);
    }

    @Override
    protected void registerStatesAndModels() {
        blockWithItem(ModBlocks.SPIRIT_ORE);
        blockWithItem(ModBlocks.ALEXANDRITE_BLOCK);
        blockWithItem(ModBlocks.RAW_ALEXANDRITE_BLOCK);

        blockWithItem(ModBlocks.ALEXANDRITE_ORE);
        blockWithItem(ModBlocks.ALEXANDRITE_DEEPSLATE_ORE);

        blockWithItem(ModBlocks.MAGIC_BLOCK);

        blockWithItem(ModBlocks.SPIRIT_STONE_BLOCK);

        crossBlock(ModBlocks.HARVESTABLE_BLOCK_9_COMMON_1.get());
        crossBlock(ModBlocks.HARVESTABLE_BLOCK_9_COMMON_2.get());
        largeMushroomBlock(ModBlocks.HARVESTABLE_BLOCK_9_COMMON_3.get());
        crossBlock(ModBlocks.HARVESTABLE_BLOCK_9_RARE_1.get());
        crossBlock(ModBlocks.HARVESTABLE_BLOCK_9_RARE_2.get());
        largeMushroomBlock(ModBlocks.HARVESTABLE_BLOCK_9_TREASURE_1.get());
    }

    private void blockWithItem(RegistryObject<Block> blockRegistryObject) {
        simpleBlockWithItem(blockRegistryObject.get(), cubeAll(blockRegistryObject.get()));
    }
    private void crossBlock(Block block) {
        ResourceLocation blockId = ForgeRegistries.BLOCKS.getKey(block);
        String path = blockId.getPath();

        ModelFile model = models().cross(path,
                modLoc("block/" + path)).renderType("cutout");

        getVariantBuilder(block).partialState()
                .setModels(new ConfiguredModel(model));
    }

    private void largeMushroomBlock(Block block) {
        ResourceLocation blockId = ForgeRegistries.BLOCKS.getKey(block);
        String path = blockId.getPath();

        ModelFile model = models().getBuilder(path)
                .parent(new ModelFile.UncheckedModelFile("minecraft:block/block"))
                .renderType("minecraft:cutout")
                .texture("particle", modLoc("block/" + path))
                .texture("texture", modLoc("block/" + path))
                // 蘑菇柄 - 更粗更高
                .element()
                .from(3, 0, 3)      // 比原版 (5.6, 0, 5.6) 更粗
                .to(13, 8, 13)      // 比原版 (10.4, 6, 10.4) 更高
                .allFaces((direction, faceBuilder) ->
                        faceBuilder.texture("#texture").cullface(null))
                .end()
                // 蘑菇傘 - 更大
                .element()
                .from(0, 8, 0)      // 比原版 (1, 6, 1) 更大
                .to(16, 12, 16)     // 比原版 (15, 9, 15) 更厚
                .allFaces((direction, faceBuilder) ->
                        faceBuilder.texture("#texture").cullface(null))
                .end();

        getVariantBuilder(block).partialState()
                .setModels(new ConfiguredModel(model));
    }
}