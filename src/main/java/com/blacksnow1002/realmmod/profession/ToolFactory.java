package com.blacksnow1002.realmmod.profession;

import com.blacksnow1002.realmmod.item.custom.HarvestToolItem;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;

/**
 * 工具工廠類
 * 用於創建已初始化的工具ItemStack
 * 推薦在以下場景使用：
 * 1. 煉器配方輸出
 * 2. NPC交易商品
 * 3. 任務獎勵
 * 4. 管理員命令給予物品
 */
public class ToolFactory {

    /**
     * 創建一個完全初始化的工具
     * @param tool 工具物品
     * @param count 數量
     * @return 已初始化的ItemStack
     */
    public static ItemStack createInitializedTool(HarvestToolItem tool, int count) {
        ItemStack stack = new ItemStack(tool, count);
        initializeTool(stack, tool);
        return stack;
    }

    /**
     * 創建一個完全初始化的工具（數量1）
     */
    public static ItemStack createInitializedTool(HarvestToolItem tool) {
        return createInitializedTool(tool, 1);
    }

    /**
     * 初始化已存在的工具ItemStack
     * 只會初始化一次，不會重複初始化
     */
    public static void initializeTool(ItemStack stack, HarvestToolItem tool) {
        // 添加日誌
        System.out.println("[ToolFactory] 開始初始化工具: " + tool.getGrade());

        // 檢查是否已經初始化過
        if (isInitialized(stack)) {
            System.out.println("[ToolFactory] 工具已經初始化過了，跳過");
            return;
        }

        HarvestToolItem.Grades grade = tool.getGrade();
        System.out.println("[ToolFactory] 工具品階: " + grade);

        // 天階工具賦予隨機詞條
        if (grade == HarvestToolItem.Grades.HEAVEN) {
            System.out.println("[ToolFactory] 這是天階工具，開始添加詞條");
            HarvestToolItem.setRandomBonus(stack);

            // 驗證詞條是否成功設置
            HarvestToolItem.BonusEntry bonus = HarvestToolItem.getBonus(stack);
            if (bonus != null) {
                System.out.println("[ToolFactory] 詞條設置成功: " + bonus.getDisplayName());
            } else {
                System.out.println("[ToolFactory] 警告: 詞條設置失敗!");
            }
        }

        // 標記為已初始化
        CompoundTag tag = getOrCreateTag(stack);
        tag.putBoolean("Initialized", true);
        stack.set(DataComponents.CUSTOM_DATA, CustomData.of(tag));

        System.out.println("[ToolFactory] 工具初始化完成");
    }

    /**
     * 創建一個指定詞條的天階工具
     * 用於測試或特殊獎勵
     */
    public static ItemStack createHeavenToolWithEntry(HarvestToolItem tool,
                                                      HarvestToolItem.BonusEntry entry) {
        if (tool.getGrade() != HarvestToolItem.Grades.HEAVEN) {
            throw new IllegalArgumentException("只有天階工具才能指定詞條!");
        }

        ItemStack stack = new ItemStack(tool, 1);
        // 天階無限耐久（已在構造函數中設置）

        // 設置指定詞條
        CompoundTag tag = new CompoundTag();
        tag.putInt("BonusEntry", entry.ordinal());
        tag.putBoolean("Initialized", true);
        stack.set(DataComponents.CUSTOM_DATA, CustomData.of(tag));

        return stack;
    }

    /**
     * 檢查工具是否已初始化
     */
    public static boolean isInitialized(ItemStack stack) {
        CustomData customData = stack.get(DataComponents.CUSTOM_DATA);
        if (customData != null) {
            CompoundTag tag = customData.copyTag();
            return tag.contains("Initialized") && tag.getBoolean("Initialized");
        }
        return false;
    }

    /**
     * 重置工具耐久度到最大值
     * 可用於修理系統
     */
    public static void repairTool(ItemStack stack, HarvestToolItem tool) {
        HarvestToolItem.Grades grade = tool.getGrade();

        // 天階和地階無限耐久，不需要修理
        if (grade.hasUnbreakable()) {
            return;
        }

        // 使用原版方法重置耐久度
        stack.setDamageValue(0);
    }

    // 輔助方法：獲取或創建NBT
    private static CompoundTag getOrCreateTag(ItemStack stack) {
        CustomData customData = stack.get(DataComponents.CUSTOM_DATA);
        if (customData != null) {
            return customData.copyTag();
        }
        return new CompoundTag();
    }
}