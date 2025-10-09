package com.blacksnow1002.realmmod.spell.spells;

import com.blacksnow1002.realmmod.capability.cultivation.CultivationRealm;
import com.blacksnow1002.realmmod.item.ModItems;
import com.blacksnow1002.realmmod.spell.BaseSpell;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

public class CreateSpell extends BaseSpell {

    @Override
    public String getName() {
        return "虛空造物";
    }

    @Override
    public CultivationRealm getRequiredRealm() {
        return CultivationRealm.ninth;
    }

    @Override
    public int getCooldownTicks() {
        return 20 * 45; // 45秒冷卻
    }

    @Override
    public boolean cast(ServerPlayer player, ServerLevel level) {
        ItemStack handItem = player.getMainHandItem();
        if (!handItem.isEmpty()) {
            player.sendSystemMessage(Component.literal("使用虛空之物時請保持雙手淨空。"));
            return false;
        }

        Item resultItem = ModItems.SPIRIT_STONE_HIGH.get();
        // 替換玩家手中物品
        player.setItemInHand(InteractionHand.MAIN_HAND, new ItemStack(resultItem, 2));

        // 顯示提示訊息
        player.sendSystemMessage(Component.literal(
                "§d你施展虛空造物，凝鍊出" +  new ItemStack(resultItem).getDisplayName().getString() + "§r。"
        ));

        // 播放音效與粒子特效（可選）
        level.playSound(null, player.blockPosition(), SoundEvents.ENCHANTMENT_TABLE_USE, player.getSoundSource(), 1.0F, 1.0F);
        level.sendParticles(ParticleTypes.HAPPY_VILLAGER, player.getX(), player.getY() + 1.0, player.getZ(), 10, 0.3, 0.5, 0.3, 0.01);

        return true;
    }
}
