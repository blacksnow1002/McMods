package com.blacksnow1002.realmmod.item.custom;

import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

public class ReforgeMainAffixItem extends Item {

    public enum ReforgeMainAffixType {
        ATTACK("攻擊類"),
        DEFENCE("防禦類"),
        SPEED("速度類");

        private final String displayName;

        ReforgeMainAffixType(String displayName) {
            this.displayName = displayName;
        }

        public String getDisplayName() {
            return displayName;
        }
    }

    private final ReforgeMainAffixType type;
    private final int rank;

    public ReforgeMainAffixItem(Properties properties, ReforgeMainAffixType type, int rank) {
        super(properties);
        this.type = type;
        this.rank = rank;
    }

    public ReforgeMainAffixType getType() {
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
