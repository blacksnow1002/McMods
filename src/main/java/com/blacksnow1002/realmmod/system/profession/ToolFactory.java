package com.blacksnow1002.realmmod.system.profession;

import com.blacksnow1002.realmmod.system.profession.base.item.BaseProfessionCollectionToolItem;
import com.blacksnow1002.realmmod.system.profession.base.item.BaseProfessionCollectionToolItem.Grades;
import com.blacksnow1002.realmmod.system.profession.base.item.BaseProfessionCollectionToolItem.BonusEntry;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;

/**
 * 工具工厂类
 * 用于创建已初始化的工具ItemStack
 * 支持所有继承自BaseProfessionCollectionToolItem的工具
 * 推荐在以下场景使用:
 * 1. 炼器配方输出
 * 2. NPC交易商品
 * 3. 任务奖励
 * 4. 管理员命令给予物品
 */
public class ToolFactory {

    /**
     * 创建一个完全初始化的工具
     * @param tool 工具物品(必须继承自BaseProfessionCollectionToolItem)
     * @param count 数量
     * @return 已初始化的ItemStack
     */
    public static ItemStack createInitializedTool(BaseProfessionCollectionToolItem tool, int count) {
        ItemStack stack = new ItemStack(tool, count);
        initializeTool(stack, tool);
        return stack;
    }

    /**
     * 创建一个完全初始化的工具(数量1)
     */
    public static ItemStack createInitializedTool(BaseProfessionCollectionToolItem tool) {
        return createInitializedTool(tool, 1);
    }

    /**
     * 初始化已存在的工具ItemStack
     * 只会初始化一次,不会重复初始化
     */
    public static void initializeTool(ItemStack stack, BaseProfessionCollectionToolItem tool) {
        // 添加日志
        System.out.println("[ToolFactory] 开始初始化工具: " + tool.getGrade() + " - " + tool.getProfessionType());

        // 检查是否已经初始化过
        if (isInitialized(stack)) {
            System.out.println("[ToolFactory] 工具已经初始化过了,跳过");
            return;
        }

        Grades grade = tool.getGrade();
        System.out.println("[ToolFactory] 工具品阶: " + grade.getDisplayName());

        // 天阶工具赋予随机词条
        if (grade == Grades.HEAVEN) {
            System.out.println("[ToolFactory] 这是天阶工具,开始添加词条");
            BaseProfessionCollectionToolItem.setRandomBonus(stack);

            // 验证词条是否成功设置
            BonusEntry bonus = BaseProfessionCollectionToolItem.getBonus(stack);
            if (bonus != null) {
                System.out.println("[ToolFactory] 词条设置成功: " + bonus.getDisplayName());
            } else {
                System.out.println("[ToolFactory] 警告: 词条设置失败!");
            }
        }

        // 标记为已初始化
        CompoundTag tag = getOrCreateTag(stack);
        tag.putBoolean("Initialized", true);
        stack.set(DataComponents.CUSTOM_DATA, CustomData.of(tag));

        System.out.println("[ToolFactory] 工具初始化完成");
    }

    /**
     * 创建一个指定词条的天阶工具
     * 用于测试或特殊奖励
     */
    public static ItemStack createHeavenToolWithEntry(BaseProfessionCollectionToolItem tool,
                                                      BonusEntry entry) {
        if (tool.getGrade() != Grades.HEAVEN) {
            throw new IllegalArgumentException("只有天阶工具才能指定词条!");
        }

        ItemStack stack = new ItemStack(tool, 1);

        // 设置指定词条
        CompoundTag tag = new CompoundTag();
        tag.putInt("BonusEntry", entry.ordinal());
        tag.putBoolean("Initialized", true);
        stack.set(DataComponents.CUSTOM_DATA, CustomData.of(tag));

        return stack;
    }

    /**
     * 检查工具是否已初始化
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
     * 可用于修理系统
     */
    public static void repairTool(ItemStack stack, BaseProfessionCollectionToolItem tool) {
        Grades grade = tool.getGrade();

        // 天阶和地阶无限耐久,不需要修理
        if (grade.hasUnbreakable()) {
            return;
        }

        // 使用原版方法重置耐久度
        stack.setDamageValue(0);
    }

    // 辅助方法:获取或创建NBT
    private static CompoundTag getOrCreateTag(ItemStack stack) {
        CustomData customData = stack.get(DataComponents.CUSTOM_DATA);
        if (customData != null) {
            return customData.copyTag();
        }
        return new CompoundTag();
    }
}