package com.blacksnow1002.realmmod.item.custom.pillItem;

import com.blacksnow1002.realmmod.item.custom.base.BasePillItem;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.player.Player;

public class HealingPillItem extends BasePillItem {
    public HealingPillItem(Properties properties) {
        super(properties);
    }

    @Override
    protected String getPillName() {
        return "回血丹";
    }

    @Override
    protected String getPillDescription(int q) {
        return switch(q) {
            case 1 -> "恢復少量生命";
            case 2 -> "恢復中量生命";
            case 3 -> "恢復大量生命";
            case 4 -> "恢復超大量生命";
            default -> "";
        };
    }

    @Override
    protected void applyEffect(Player player, int q) {
        player.addEffect(new MobEffectInstance(MobEffects.REGENERATION, 200 * (q+1), q));
    }
}
