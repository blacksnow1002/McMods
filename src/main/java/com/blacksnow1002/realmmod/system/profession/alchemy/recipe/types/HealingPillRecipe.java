package com.blacksnow1002.realmmod.system.profession.alchemy.recipe.types;

import com.blacksnow1002.realmmod.core.registry.ModItems;
import com.blacksnow1002.realmmod.system.profession.alchemy.recipe.BaseAlchemyRecipe;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;

/**
 * 療傷丹配方
 * 用於恢復生命值的丹藥
 */
public class HealingPillRecipe extends BaseAlchemyRecipe {

    public HealingPillRecipe() {
        super("healing_pill_recipe", 9,
                new Ingredient(Items.DIAMOND, 1),
                new Ingredient(Items.DARK_OAK_WOOD, 2),
                new Ingredient(Items.APPLE, 3));
    }

    @Override
    public String getRecipeType() {
        return "療傷丹";
    }

    @Override
    public String getDisplayName() {
        return "abc";
    }

    @Override
    public String getDescription() {
        return "煉製 " + rank + "品療傷丹 - 恢復生命值";
    }

    @Override
    public Item getOutputItem() {
        return ModItems.HEALING_PILL_9.get();
    }
}