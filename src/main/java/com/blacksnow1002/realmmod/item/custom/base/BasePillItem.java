package com.blacksnow1002.realmmod.item.custom.base;

import com.blacksnow1002.realmmod.item.ModDataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;

import java.util.List;

public abstract class BasePillItem extends Item {

    public BasePillItem(Properties properties) {
        super(properties);
    }

    @Override
    public Component getName(ItemStack stack) {
        int q = stack.getOrDefault(ModDataComponents.PILL_QUALITY.get(), 0);
        return Component.literal(QUALITY_NAMES[q] + getPillName());
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltip, TooltipFlag flag) {
        int q = stack.getOrDefault(ModDataComponents.PILL_QUALITY.get(), 0);
        tooltip.add(Component.literal(getPillDescription(q)));
    }

    @Override
    public ItemStack finishUsingItem(ItemStack stack, Level level, LivingEntity entity) {
        if (!level.isClientSide && entity instanceof Player p) {
            int q = stack.getOrDefault(ModDataComponents.PILL_QUALITY.get(), 0);
            applyEffect(p, q);
            stack.shrink(1);
        }
        return super.finishUsingItem(stack, level, entity);
    }

    // 共通邏輯
    private static final String[] QUALITY_NAMES = {"廢丹", "浮紋", "雲紋", "靈紋", "道紋"};

    // 子類實現
    protected abstract String getPillName();
    protected abstract String getPillDescription(int quality);
    protected abstract void applyEffect(Player player, int quality);
}