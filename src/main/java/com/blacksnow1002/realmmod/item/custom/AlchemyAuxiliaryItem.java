package com.blacksnow1002.realmmod.item.custom;

import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

public class AlchemyAuxiliaryItem extends Item {

    private final AuxiliaryType type;
    private final int rank;

    public AlchemyAuxiliaryItem(Properties properties, AuxiliaryType type, int rank) {
        super(properties);
        this.type = type;
        this.rank = rank;
    }

    public AuxiliaryType getType() {
        return type;
    }

    public int getRank() {
        return rank;
    }

    @Override
    public Component getName(ItemStack itemstack) {
        return Component.literal(rank + "品");
    }

    public enum AuxiliaryType {
        ENHANCEMENT,  // 增強類
        STABILIZE,    // 穩定類
        PURIFY        // 淨化類
    }
}
