package com.blacksnow1002.realmmod.profession.alchemy.recipe;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.ItemStackHandler;

public abstract class BaseAlchemyRecipe {

    // 材料需求（物品 + 數量）
    public static class Ingredient {
        private final Item item;
        private final int count;

        public Ingredient(Item item, int count) {
            this.item = item;
            this.count = count;
        }

        public Item getItem() { return item; }
        public int getCount() { return count; }

        public boolean matches(ItemStack stack) {
            return stack.getItem() == item && stack.getCount() >= count;
        }
    }

    protected final String id;
    protected final int rank; // 丹藥品級（1-9品）
    protected final Ingredient demonCore; // 妖丹
    protected final Ingredient mainMaterial1; // 主材1
    protected final Ingredient mainMaterial2; // 主材2

    protected BaseAlchemyRecipe(String id, int rank,
                                Ingredient demonCore,
                                Ingredient mainMaterial1,
                                Ingredient mainMaterial2) {
        this.id = id;
        this.rank = rank;
        this.demonCore = demonCore;
        this.mainMaterial1 = mainMaterial1;
        this.mainMaterial2 = mainMaterial2;
    }

    // Getters
    public String getId() { return id; }
    public int getRank() { return rank; }
    public Ingredient getDemonCore() { return demonCore; }
    public Ingredient getMainMaterial1() { return mainMaterial1; }
    public Ingredient getMainMaterial2() { return mainMaterial2; }

    public boolean matches(ItemStack slot1, ItemStack slot2, ItemStack slot3) {
        return demonCore.matches(slot1) &&
                mainMaterial1.matches(slot2) &&
                mainMaterial2.matches(slot3);
    }

    public void shrinkMaterial(ItemStackHandler itemHandler) {
        itemHandler.extractItem(2, demonCore.count, false);
        itemHandler.extractItem(3, mainMaterial1.count, false);
        itemHandler.extractItem(4, mainMaterial2.count, false);
        itemHandler.extractItem(5, 1, false);
    }

    public void returnMaterial(int slot ,ItemStackHandler itemHandler) {
        ItemStack stack = itemHandler.getStackInSlot(slot);
        if (slot == 3) {
            itemHandler.setStackInSlot(slot, new ItemStack(stack.getItem(), stack.getCount() + mainMaterial1.count));
        } else {
            itemHandler.setStackInSlot(slot, new ItemStack(stack.getItem(), stack.getCount() + mainMaterial2.count));
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
        return rank + "品丹藥";
    }


}
