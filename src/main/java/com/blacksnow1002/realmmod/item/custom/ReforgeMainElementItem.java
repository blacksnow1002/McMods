package com.blacksnow1002.realmmod.item.custom;

import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

public class ReforgeMainElementItem extends Item {

    public enum ReforgeMainElementType {
        GOLD("金"),
        WOOD("木"),
        WATER("水"),
        FIRE("火"),
        EARTH("土");

        private final String displayName;

        ReforgeMainElementType(String displayName) {
            this.displayName = displayName;
        }

        public String getDisplayName() {
            return displayName;
        }
    }

    private final ReforgeMainElementType type;
    private final int rank;

    public ReforgeMainElementItem(Properties properties, ReforgeMainElementType type, int rank) {
        super(properties);
        this.type = type;
        this.rank = rank;
    }

    public ReforgeMainElementType getType() {
        return type;
    }

    public int getRank() {
        return rank;
    }

    @Override
    public Component getName(ItemStack itemstack) {
        return Component.literal(rank + "品");
    }


}
