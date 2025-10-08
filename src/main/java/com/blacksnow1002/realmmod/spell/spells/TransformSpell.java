package com.blacksnow1002.realmmod.spell.spells;

import com.blacksnow1002.realmmod.capability.CultivationRealm;
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
import net.minecraft.world.item.Items;

import java.util.Map;

public class TransformSpell extends BaseSpell {

    private static final Map<Item, Item> TRANSFORMABLE = Map.of(
            ModItems.ALEXANDRITE.get(), ModItems.SPIRIT_STONE_LOW.get(),
            ModItems.RAW_ALEXANDRITE.get(), ModItems.SPIRIT_STONE_MIDDLE.get()
    );

    @Override
    public String getName() {
        return "造化之力";
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
        if (handItem.isEmpty() || handItem.getItem() == Items.AIR) {
            player.sendSystemMessage(Component.literal("§7你手中沒有可轉化的物品。"));
            return false;
        }

        Item current = handItem.getItem();
        if (TRANSFORMABLE.containsKey(current)) {
            Item resultItem = TRANSFORMABLE.get(current);
            int amount = handItem.getCount();

            // 替換玩家手中物品
            player.setItemInHand(InteractionHand.MAIN_HAND, new ItemStack(resultItem, amount));

            // 顯示提示訊息
            player.sendSystemMessage(Component.literal(
                    "§d你施展造化之力，將 §b" + handItem.getDisplayName().getString() +
                            " §d轉化為 §a" + new ItemStack(resultItem).getDisplayName().getString() + "§r。"
            ));

            // 播放音效與粒子特效（可選）
            level.playSound(null, player.blockPosition(), SoundEvents.ENCHANTMENT_TABLE_USE, player.getSoundSource(), 1.0F, 1.0F);
            level.sendParticles(ParticleTypes.HAPPY_VILLAGER, player.getX(), player.getY() + 1.0, player.getZ(), 10, 0.3, 0.5, 0.3, 0.01);

            return true;
        }

        player.sendSystemMessage(Component.literal("§7造化之力無法轉化 §c" + handItem.getDisplayName().getString() + "§r。"));
        return false;
    }
}
