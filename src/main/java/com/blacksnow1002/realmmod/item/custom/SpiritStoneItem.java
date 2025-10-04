package com.blacksnow1002.realmmod.item.custom;

import net.minecraft.world.item.Item;

public class SpiritStoneItem extends Item {
    private final int cultivationValue; // 每顆提供的修為量

    public SpiritStoneItem(Properties properties, int cultivationValue) {
        super(properties);
        this.cultivationValue = cultivationValue;
    }

    public int getCultivationValue() {
        return cultivationValue;
    }
}
