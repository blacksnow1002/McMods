package com.blacksnow1002.realmmod.item.custom;

import com.blacksnow1002.realmmod.profession.reforge.recipe.BaseReforgeRecipe;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import org.jetbrains.annotations.Nullable;

import java.awt.*;
import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class ReforgeRecipeItem extends Item {

    private final Supplier<BaseReforgeRecipe> recipeSupplier;
    private BaseReforgeRecipe cachedRecipe;

    public ReforgeRecipeItem(Properties properties, Supplier<BaseReforgeRecipe> recipeSupplier) {
        super(properties.stacksTo(1)); // 配方物品不可堆疊
        this.recipeSupplier = recipeSupplier;
    }

    /**
     * 獲取對應的配方
     */
    public BaseReforgeRecipe getRecipe() {
        if (cachedRecipe == null) {
            cachedRecipe = recipeSupplier.get();
        }
        return cachedRecipe;
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable TooltipContext context, List<Component> tooltip, TooltipFlag flag) {
        BaseReforgeRecipe recipe = getRecipe();

        tooltip.add(Component.literal("§6配方類型: §f" + recipe.getRecipeType()));
        tooltip.add(Component.literal("§6產物品級: §f" + recipe.getRank() + "品"));
        tooltip.add(Component.literal(""));
        tooltip.add(Component.literal("§7" + recipe.getDescription()));
        tooltip.add(Component.literal(""));
        tooltip.add(Component.literal("§e所需材料:"));

        // 顯示妖丹
        tooltip.add(Component.literal("  §f核心: §7" +
                recipe.getDemonCore().getItem().getDescription().getString() +
                " x" + recipe.getDemonCore().getCount()));

        // 顯示主材1
        List<ReforgeMainElementItem.ReforgeMainElementType> t1 = recipe.getMainElementAble();
        String j1 = t1.stream()
                .map(ReforgeMainElementItem.ReforgeMainElementType::getDisplayName)
                .collect(Collectors.joining(" / "));

        tooltip.add(Component.literal("§f主材一（擇一）：§7[" + j1 + "] x " +recipe.getMainElementMaterial().getCount()));

        // 顯示主材2
        List<ReforgeMainAffixItem.ReforgeMainAffixType> t2 = recipe.getMainAffixAble();
        String j2 = t2.stream()
                .map(ReforgeMainAffixItem.ReforgeMainAffixType::getDisplayName)
                .collect(Collectors.joining(" / "));

        tooltip.add(Component.literal("§f主材二（擇一）：§7[" + j2 + "] x " +recipe.getMainAffixMaterial().getCount()));


        super.appendHoverText(stack, context, tooltip, flag);
    }
}