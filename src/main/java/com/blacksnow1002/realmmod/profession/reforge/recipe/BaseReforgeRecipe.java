package com.blacksnow1002.realmmod.profession.reforge.recipe;

import com.blacksnow1002.realmmod.item.custom.ReforgeMainAffixItem;
import com.blacksnow1002.realmmod.item.custom.ReforgeMainElementItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.ItemStackHandler;

import java.util.List;

public abstract class BaseReforgeRecipe {

    public static class CoreIngredient {
        private final Item item;
        private final int count;

        public CoreIngredient(Item item, int count) {
            this.item = item;
            this.count = count;
        }

        public Item getItem() { return item; }
        public int getCount() { return count; }

        public boolean matches(ItemStack stack) {
            return stack.getItem() == item && stack.getCount() >= count;
        }
    }

    public static class MainElementIngredient {
        private final List<ReforgeMainElementItem.ReforgeMainElementType> type;
        private final int count;

        public MainElementIngredient(List<ReforgeMainElementItem.ReforgeMainElementType> type, int count) {
            this.type = type;
            this.count = count;
        }

        public List<ReforgeMainElementItem.ReforgeMainElementType> getType() { return type; }
        public int getCount() { return count; }

        public boolean matches(ItemStack stack) {
            if (stack.getItem() instanceof ReforgeMainElementItem ingredient) {
                return type.contains(ingredient.getType()) && stack.getCount() >= count;
            }
            return false;
        }
    }

    public static class MainAffixIngredient {
        private final List<ReforgeMainAffixItem.ReforgeMainAffixType> type;
        private final int count;

        public MainAffixIngredient(List<ReforgeMainAffixItem.ReforgeMainAffixType> type, int count) {
            this.type = type;
            this.count = count;
        }

        public List<ReforgeMainAffixItem.ReforgeMainAffixType> getType() { return type; }
        public int getCount() { return count; }

        public boolean matches(ItemStack stack) {
            if (stack.getItem() instanceof ReforgeMainAffixItem ingredient) {
                return type.contains(ingredient.getType()) && stack.getCount() >= count;
            }
            return false;
        }
    }


    protected final String id;
    protected final int rank; // 產物品級（1-9品）
    protected final CoreIngredient core; // 核心
    protected final MainElementIngredient mainElementMaterial; // 主材1
    protected final MainAffixIngredient mainAffixMaterial; // 主材2

    protected BaseReforgeRecipe(String id, int rank,
                                CoreIngredient core,
                                MainElementIngredient mainElementMaterial,
                                MainAffixIngredient mainAffixMaterial) {
        this.id = id;
        this.rank = rank;
        this.core = core;
        this.mainElementMaterial = mainElementMaterial;
        this.mainAffixMaterial = mainAffixMaterial;
    }

    // Getters
    public String getId() { return id; }
    public int getRank() { return rank; }
    public CoreIngredient getDemonCore() { return core; }
    public MainElementIngredient getMainElementMaterial() { return mainElementMaterial; }
    public MainAffixIngredient getMainAffixMaterial() { return mainAffixMaterial; }

    public List<ReforgeMainElementItem.ReforgeMainElementType> getMainElementAble() {
        return mainElementMaterial.getType();
    }

    public List<ReforgeMainAffixItem.ReforgeMainAffixType> getMainAffixAble() {
        return mainAffixMaterial.getType();
    }

    public boolean matches(ItemStack slot1, ItemStack slot2, ItemStack slot3) {
        return core.matches(slot1) &&
                mainElementMaterial.matches(slot2) &&
                mainAffixMaterial.matches(slot3);
    }

    public void shrinkMaterial(ItemStackHandler itemHandler) {
        itemHandler.extractItem(2, core.count, false);
        itemHandler.extractItem(3, mainElementMaterial.count, false);
        itemHandler.extractItem(4, mainAffixMaterial.count, false);
        itemHandler.extractItem(5, 1, false);
    }

    public void returnMaterial(int slot, ItemStackHandler itemHandler) {
        ItemStack stack = itemHandler.getStackInSlot(slot);
        if (slot == 3) {
            itemHandler.setStackInSlot(slot, new ItemStack(stack.getItem(), stack.getCount() + mainElementMaterial.count));
        } else {
            itemHandler.setStackInSlot(slot, new ItemStack(stack.getItem(), stack.getCount() + mainAffixMaterial.count));
        }
    }

    public abstract Item getOutputItem();

    public String getDisplayName() {
        return "";
    }

    public String getRecipeType() {
        return "";
    };
    public String getDescription() {
        return rank + "品產物";
    }


}
