package com.blacksnow1002.realmmod.profession.reforge.recipe.types;

import com.blacksnow1002.realmmod.item.ModItems;
import com.blacksnow1002.realmmod.item.custom.ReforgeMainElementItem;
import com.blacksnow1002.realmmod.profession.alchemy.recipe.BaseAlchemyRecipe;
import com.blacksnow1002.realmmod.profession.reforge.recipe.BaseReforgeRecipe;
import com.blacksnow1002.realmmod.item.custom.ReforgeMainAffixItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;

import java.util.List;

/**
 * 療傷丹配方
 * 用於恢復生命值的丹藥
 */
public class ASwordRecipe extends BaseReforgeRecipe {

    public ASwordRecipe() {
        super("a_sword_recipe", 9,
                new CoreIngredient(Items.DIAMOND, 1),
                new MainElementIngredient(List.of(
                        ReforgeMainElementItem.ReforgeMainElementType.GOLD,
                        ReforgeMainElementItem.ReforgeMainElementType.WOOD,
                        ReforgeMainElementItem.ReforgeMainElementType.WATER,
                        ReforgeMainElementItem.ReforgeMainElementType.FIRE,
                        ReforgeMainElementItem.ReforgeMainElementType.EARTH), 2),
                new MainAffixIngredient(List.of(
                        ReforgeMainAffixItem.ReforgeMainAffixType.ATTACK,
                        ReforgeMainAffixItem.ReforgeMainAffixType.DEFENCE,
                        ReforgeMainAffixItem.ReforgeMainAffixType.SPEED
                ), 3));
    }

    @Override
    public String getRecipeType() {
        return "桃木劍";
    }

    @Override
    public String getDisplayName() {
        return "abc";
    }

    @Override
    public String getDescription() {
        return "煉製 " + rank + "品桃木劍 - test";
    }

    @Override
    public Item getOutputItem() {
        // TODO: 改成物品
        return ModItems.HEALING_PILL_9.get();
    }
}