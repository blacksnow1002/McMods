package com.blacksnow1002.realmmod.system.technique.item;

import com.blacksnow1002.realmmod.system.technique.TechniqueSystem;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class TechniqueItem extends Item {

    private final String techniqueId;

    public TechniqueItem(String pTechniqueId, Properties pProperties) {
        super(pProperties.stacksTo(1));
        this.techniqueId = pTechniqueId;
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level pLevel, Player pPlayer, InteractionHand pHand) {
        ItemStack itemstack = pPlayer.getItemInHand(pHand);

        if (!pLevel.isClientSide && pPlayer instanceof ServerPlayer serverPlayer) {
            TechniqueSystem system = TechniqueSystem.getInstance();

            System.out.println("[DEBUG] Attempting to unlock technique: " + techniqueId);
            boolean success = system.unlockTechnique(serverPlayer, techniqueId);
            System.out.println("[DEBUG] Unlock result: " + success);

            if (success) {
                System.out.println("[DEBUG] Shrinking item...");
                itemstack.shrink(1);
                return InteractionResultHolder.success(itemstack);
            } else {
                System.out.println("[DEBUG] Unlock failed");
                return InteractionResultHolder.fail(itemstack);
            }
        }

        return InteractionResultHolder.pass(itemstack);
    }
}