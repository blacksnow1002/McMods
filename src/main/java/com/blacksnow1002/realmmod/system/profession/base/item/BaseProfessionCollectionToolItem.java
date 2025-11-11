package com.blacksnow1002.realmmod.system.profession.base.item;

import com.blacksnow1002.realmmod.system.profession.ProfessionType;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.nbt.CompoundTag;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Random;

public class BaseProfessionCollectionToolItem extends Item {

    public enum Grades {
        HEAVEN("天", 1.0, 0.2, 0),      // 無限耐久
        EARTH("地", 0.6, 0.1, 0),       // 無限耐久
        MYSTIC("玄", 0.2, 0.05, 100),   // 100耐久
        MORTAL("黃", 0.0, 0.0, 50);     // 50耐久

        private final String displayName;
        private final double expBonus;
        private final double successBonus;
        private final int maxDurability;

        Grades(String displayName, double expBonus, double successBonus, int maxDurability) {
            this.displayName = displayName;
            this.expBonus = expBonus;
            this.successBonus = successBonus;
            this.maxDurability = maxDurability;
        }

        public String getDisplayName() { return displayName; }
        public double getExpBonus() { return expBonus; }
        public double getSuccessBonus() { return successBonus; }
        public int getMaxDurability() { return maxDurability; }

        public boolean hasUnbreakable() { return maxDurability == 0; }

        public static Grades fromName(String name) {
            for (Grades grade : values()) {
                if (grade.displayName.equals(name)) return grade;
            }
            return MORTAL;
        }
    }

    public enum BonusEntry {
        ENTRY_1("詞條一", "採集高一階產物成功率+10% 並不觸發心魔"),
        ENTRY_2("詞條二", "採集同階稀有產物獲得天材地寶機率上升至5%"),
        ENTRY_3("詞條三", "採集同階或高一階產物成功額外給予100%職業經驗加成"),
        ENTRY_4("詞條四", "採集高一階普通產物必定成功"),
        ENTRY_5("詞條五", "採集同階普通或稀有產物有30%機率給予2個物品"),
        ENTRY_6("詞條六", "採集高一階產物套用基礎成功率，但無成功率加成"),
        ENTRY_7("詞條七", "採集成功時，回復氣血與真元");

        private final String displayName;
        private final String description;

        BonusEntry(String displayName, String description) {
            this.displayName = displayName;
            this.description = description;
        }

        public String getDisplayName() { return displayName; }
        public String getDescription() { return description; }
    }

    private final ProfessionType professionType;
    private final Grades grade;
    private final int rank;

    public BaseProfessionCollectionToolItem(Properties properties, ProfessionType professionType, Grades grade, int rank) {
        super(properties.durability(grade.getMaxDurability()));  // 使用原版耐久度系統
        this.professionType = professionType;
        this.grade = grade;
        this.rank = rank;
    }

    public ProfessionType getProfessionType() { return this.professionType; }
    public Grades getGrade() { return grade; }
    public int getRank() { return rank; }

    public static BonusEntry getBonus(ItemStack stack) {
        CustomData customData = stack.get(DataComponents.CUSTOM_DATA);

        if (customData != null) {
            CompoundTag tag = customData.copyTag();

            if (tag.contains("BonusEntry")) {
                int index = tag.getInt("BonusEntry");

                if (index >= 0 && index < BonusEntry.values().length) {
                    BonusEntry entry = BonusEntry.values()[index];
                    return entry;
                }
            }
        }

        return null;
    }

    // TODO: 測試製作的物品有沒有給詞條
    public static void setRandomBonus(ItemStack stack) {
        Random random = new Random();
        int bonusIndex = random.nextInt(BonusEntry.values().length);

        // 獲取或創建 tag
        CustomData existingData = stack.get(DataComponents.CUSTOM_DATA);
        CompoundTag tag;
        if (existingData != null) {
            tag = existingData.copyTag();
        } else {
            tag = new CompoundTag();
        }

        // 設置詞條
        tag.putInt("BonusEntry", bonusIndex);

        // 保存
        stack.set(DataComponents.CUSTOM_DATA, CustomData.of(tag));
    }

    /**
     * 消耗耐久度
     * 使用原版耐久度系統
     * @return true 如果工具還能使用，false 如果工具已損壞
     */
    public static boolean consumeDurability(ItemStack stack, Grades grade) {
        // 天階和地階無限耐久
        if (grade.hasUnbreakable()) return true;

        stack.setDamageValue(stack.getDamageValue() + 1);

        if (stack.getDamageValue() >= stack.getMaxDamage()) {
            stack.shrink(1);
            return false;
        }

        return true;
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable TooltipContext context, List<Component> tooltip, TooltipFlag flag) {
        tooltip.add(Component.literal("§6品級: §f" + grade.getDisplayName() + "階"));
        tooltip.add(Component.literal("§6等級: §f" + rank + "品"));

        if (grade.hasUnbreakable()) {
            tooltip.add(Component.literal("§6耐久: §f無限"));
        }

        if (grade.getExpBonus() > 0) {
            tooltip.add(Component.literal("§b經驗加成: §f+" + (int)(grade.getExpBonus() * 100) + "%"));
        }

        if (grade.getSuccessBonus() > 0) {
            tooltip.add(Component.literal("§a成功率加成: §f+" + (int)(grade.getSuccessBonus() * 100) + "%"));
        }

        BonusEntry bonus = getBonus(stack);
        if (bonus != null) {
            tooltip.add(Component.literal(""));
            tooltip.add(Component.literal("§d" + bonus.getDisplayName()));
            tooltip.add(Component.literal("§7" + bonus.getDescription()));
        } else {
            // 如果是天階但沒有詞條，顯示警告
            if (grade == Grades.HEAVEN) {
                tooltip.add(Component.literal("§c[警告] 天階工具缺少詞條"));
            }
        }

        super.appendHoverText(stack, context, tooltip, flag);
    }

    @Override
    public boolean isBarVisible(ItemStack stack) {
        // 天階和地階不顯示耐久度條
        return !grade.hasUnbreakable() && super.isBarVisible(stack);
    }
}