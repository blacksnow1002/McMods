package com.blacksnow1002.realmmod.datagen;

import com.blacksnow1002.realmmod.RealmMod;
import com.blacksnow1002.realmmod.item.ModItems;
import net.minecraft.data.PackOutput;
import net.minecraftforge.client.model.generators.ItemModelProvider;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.fml.common.Mod;

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

        basicItem(ModItems.MAGIC_DRUG.get());

        basicItem(ModItems.ALEXANDRITE.get());
        basicItem(ModItems.RAW_ALEXANDRITE.get());

        basicItem(ModItems.CHISEL.get());
        basicItem(ModItems.KOHLRABI.get());
        basicItem(ModItems.SPIRIT_FRUIT.get());
        basicItem(ModItems.AURORA_ASHES.get());

    }
}