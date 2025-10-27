package com.blacksnow1002.realmmod.item.custom;

import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

/**
 * 重鑄石 - 用於刷新天階工具詞條
 */
public class ToolReforgeItem extends Item {

    public ToolReforgeItem(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack reforgeStone = player.getItemInHand(hand);
        ItemStack offHand = player.getItemInHand(hand == InteractionHand.MAIN_HAND ?
                InteractionHand.OFF_HAND : InteractionHand.MAIN_HAND);
        if (!level.isClientSide) {
            if (!(offHand.getItem() instanceof HarvestToolItem tool)) {
                player.sendSystemMessage(Component.literal("§c請將要重鑄的工具放在副手!"));
                return InteractionResultHolder.fail(reforgeStone);
            }
            if (tool.getGrade() != HarvestToolItem.Grades.HEAVEN) {
                player.sendSystemMessage(Component.literal("§c只能重鑄天階工具!"));
                return InteractionResultHolder.fail(reforgeStone);
            }

            // 刷新詞條
            HarvestToolItem.setRandomBonus(offHand);
            HarvestToolItem.BonusEntry newBonus = HarvestToolItem.getBonus(offHand);

            player.sendSystemMessage(Component.literal("§a重鑄成功!"));
            player.sendSystemMessage(Component.literal("§d新詞條: " + newBonus.getDisplayName()));
            player.sendSystemMessage(Component.literal("§7" + newBonus.getDescription()));

            // 消耗重鑄石
            reforgeStone.shrink(1);
        }

        return InteractionResultHolder.success(reforgeStone);
    }
}
