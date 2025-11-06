package com.blacksnow1002.realmmod.item.custom;

import com.blacksnow1002.realmmod.profession.alchemy.recipe.BaseAlchemyRecipe;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.function.Supplier;

public class AlchemyRecipeItem extends Item {

    private final Supplier<BaseAlchemyRecipe> recipeSupplier;
    private BaseAlchemyRecipe cachedRecipe;

    public AlchemyRecipeItem(Properties properties, Supplier<BaseAlchemyRecipe> recipeSupplier) {
        super(properties.stacksTo(1)); // 配方物品不可堆疊
        this.recipeSupplier = recipeSupplier;
    }

    /**
     * 獲取對應的配方
     */
    public BaseAlchemyRecipe getRecipe() {
        if (cachedRecipe == null) {
            cachedRecipe = recipeSupplier.get();
        }
        return cachedRecipe;
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable TooltipContext context, List<Component> tooltip, TooltipFlag flag) {
        BaseAlchemyRecipe recipe = getRecipe();

        tooltip.add(Component.literal("§6配方類型: §f" + recipe.getRecipeType()));
        tooltip.add(Component.literal("§6丹藥品級: §f" + recipe.getRank() + "品"));
        tooltip.add(Component.literal(""));
        tooltip.add(Component.literal("§7" + recipe.getDescription()));
        tooltip.add(Component.literal(""));
        tooltip.add(Component.literal("§e所需材料:"));

        // 顯示妖丹
        tooltip.add(Component.literal("  §f妖丹: §7" +
                recipe.getDemonCore().getItem().getDescription().getString() +
                " x" + recipe.getDemonCore().getCount()));

        // 顯示主材1
        tooltip.add(Component.literal("  §f主材一: §7" +
                recipe.getMainMaterial1().getItem().getDescription().getString() +
                " x" + recipe.getMainMaterial1().getCount()));

        // 顯示主材2
        tooltip.add(Component.literal("  §f主材二: §7" +
                recipe.getMainMaterial2().getItem().getDescription().getString() +
                " x" + recipe.getMainMaterial2().getCount()));


        super.appendHoverText(stack, context, tooltip, flag);
    }
}