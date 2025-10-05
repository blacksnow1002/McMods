package com.blacksnow1002.realmmod.util;

import com.blacksnow1002.realmmod.RealmMod;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;

public class ModTags {
    public static class Blocks {
        public static final TagKey<Block> SPIRITUAL_VISIBLE = createTag("spiritual_invisible");

        private static TagKey<Block>  createTag(String name) {
            return BlockTags.create(ResourceLocation.fromNamespaceAndPath(RealmMod.MOD_ID, name));
        }
    }

    public static class Items {
        public static final TagKey<Item> TRANSFORMABLE_ITEMS = createTag("transformable_items");

        private static TagKey<Item>  createTag(String name) {
            return ItemTags.create(ResourceLocation.fromNamespaceAndPath(RealmMod.MOD_ID, name));
        }
    }
}
