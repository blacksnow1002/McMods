package com.blacksnow1002.realmmod.system.achievement.datagen;

import com.blacksnow1002.realmmod.system.achievement.CustomAchievements;
import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.common.data.ForgeAdvancementProvider;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

public class ModAdvancementProvider extends ForgeAdvancementProvider {

    public ModAdvancementProvider(PackOutput output,
                                  CompletableFuture<HolderLookup.Provider> registries,
                                  ExistingFileHelper existingFileHelper) {
        super(output, registries, existingFileHelper, List.of(new CustomAdvancementGenerator()));
    }

    private static class CustomAdvancementGenerator implements ForgeAdvancementProvider.AdvancementGenerator {
        @Override
        public void generate(HolderLookup.Provider registries,
                             Consumer<AdvancementHolder> saver,
                             ExistingFileHelper existingFileHelper) {

            Map<CustomAchievements, AdvancementHolder> holders = new HashMap<>();

            // 按順序生成所有成就（先生成父成就）
            for (CustomAchievements achievement : CustomAchievements.values()) {
                achievement.generate( saver, holders);
            }
        }
    }
}
