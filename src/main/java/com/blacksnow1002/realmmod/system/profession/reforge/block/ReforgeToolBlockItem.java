package com.blacksnow1002.realmmod.system.profession.reforge.block;

import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.level.block.Block;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Random;

public class ReforgeToolBlockItem extends BlockItem {

    private final ReforgeToolBlock.Grades grade;
    private final int rank;

    public ReforgeToolBlockItem(Block block, Properties properties, ReforgeToolBlock.Grades grade, int rank) {
        super(block, properties);
        this.grade = grade;
        this.rank = rank;
    }

    public ReforgeToolBlock.Grades getGrade() { return grade; }
    public int getRank() { return rank; }

    /**
     * 獲取物品的詞條
     */
    @Nullable
    public static ReforgeToolBlock.BonusEntry getBonus(ItemStack stack) {
        CustomData customData = stack.get(DataComponents.CUSTOM_DATA);

        if (customData != null) {
            CompoundTag tag = customData.copyTag();

            if (tag.contains("BonusEntry")) {
                int index = tag.getInt("BonusEntry");

                if (index >= 0 && index < ReforgeToolBlock.BonusEntry.values().length) {
                    return ReforgeToolBlock.BonusEntry.values()[index];
                }
            }
        }

        return null;
    }

    /**
     * 設置隨機詞條（用於合成/給予物品時）
     */
    public static void setRandomBonus(ItemStack stack) {
        Random random = new Random();
        int bonusIndex = random.nextInt(ReforgeToolBlock.BonusEntry.values().length);

        CustomData existingData = stack.get(DataComponents.CUSTOM_DATA);
        CompoundTag tag;
        if (existingData != null) {
            tag = existingData.copyTag();
        } else {
            tag = new CompoundTag();
        }

        tag.putInt("BonusEntry", bonusIndex);
        stack.set(DataComponents.CUSTOM_DATA, CustomData.of(tag));
    }

    /**
     * 設置指定詞條
     */
    public static void setBonus(ItemStack stack, ReforgeToolBlock.BonusEntry entry) {
        CustomData existingData = stack.get(DataComponents.CUSTOM_DATA);
        CompoundTag tag;
        if (existingData != null) {
            tag = existingData.copyTag();
        } else {
            tag = new CompoundTag();
        }

        tag.putInt("BonusEntry", entry.ordinal());
        stack.set(DataComponents.CUSTOM_DATA, CustomData.of(tag));
    }

    /**
     * 檢查是否有詞條
     */
    public static boolean hasBonus(ItemStack stack) {
        CustomData customData = stack.get(DataComponents.CUSTOM_DATA);
        if (customData != null) {
            CompoundTag tag = customData.copyTag();
            return tag.contains("BonusEntry");
        }
        return false;
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable TooltipContext context, List<Component> tooltip, TooltipFlag flag) {
        tooltip.add(Component.literal("§6品級: §f" + grade.getDisplayName() + "階"));
        tooltip.add(Component.literal("§6等級: §f" + rank + "品"));

        if (grade.hasUnbreakable()) {
            tooltip.add(Component.literal("§6耐久: §f無限"));
        } else {
            tooltip.add(Component.literal("§6耐久: §f" + grade.getMaxDurability()));
        }

        if (grade.getExpBonus() > 0) {
            tooltip.add(Component.literal("§b經驗加成: §f+" + (int)(grade.getExpBonus() * 100) + "%"));
        }

        // 顯示詞條
        ReforgeToolBlock.BonusEntry bonus = getBonus(stack);
        if (bonus != null) {
            tooltip.add(Component.literal(""));
            tooltip.add(Component.literal("§d" + bonus.getDisplayName()));
            tooltip.add(Component.literal("§7" + bonus.getDescription()));
        } else {
            // 如果是天階但沒有詞條，顯示警告
            if (grade == ReforgeToolBlock.Grades.HEAVEN) {
                tooltip.add(Component.literal("§c[警告] 天階工具缺少詞條"));
            }
        }

        super.appendHoverText(stack, context, tooltip, flag);
    }
}