package com.blacksnow1002.realmmod.system.cultivation.item;

import com.blacksnow1002.realmmod.common.capability.ModCapabilities;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class SpiritFruitItem extends Item {
    private final int cultivationGain;

    public SpiritFruitItem(Properties properties,  int cultivationGain) {
        super(properties);
        this.cultivationGain = cultivationGain;
    }

    @Override
    public ItemStack finishUsingItem(ItemStack stack, Level level, LivingEntity entity) {
        if (entity instanceof Player player && !level.isClientSide) {

            player.getCapability(ModCapabilities.CULTIVATION_CAP).ifPresent(data -> {
                data.addCultivation(player,  this.cultivationGain);
                player.displayClientMessage(
                        Component.translatable("message.realmmod.eat.spirit_fruit",
                                this.cultivationGain,
                                data.getCultivation(),
                                data.getRealm().getRequiredPerLayer()
                        ),
                        true
                );
            });
        }
        return stack;
    }
}
