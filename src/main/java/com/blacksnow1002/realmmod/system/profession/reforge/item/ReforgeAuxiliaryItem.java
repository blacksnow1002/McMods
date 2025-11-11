package com.blacksnow1002.realmmod.system.profession.reforge.item;

import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

public class ReforgeAuxiliaryItem extends Item {

    private final ReforgeAuxiliaryType type;
    private final int rank;

    public ReforgeAuxiliaryItem(Item.Properties properties, ReforgeAuxiliaryType type, int rank) {
        super(properties);
        this.type = type;
        this.rank = rank;
    }

    public ReforgeAuxiliaryType getType() {
        return type;
    }

    public int getRank() {
        return rank;
    }

    @Override
    public Component getName(ItemStack itemstack) {
        return Component.literal(rank + "品");
    }

    public enum ReforgeAuxiliaryType {
        ENHANCEMENT,  // 增強類
        STABILIZE,    // 穩定類
        PURIFY        // 淨化類
    }
}
