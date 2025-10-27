package com.blacksnow1002.realmmod.profession;

import com.blacksnow1002.realmmod.RealmMod;
import com.blacksnow1002.realmmod.item.custom.HarvestToolItem;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = RealmMod.MOD_ID)
public class ToolInitializationEvent {

    @SubscribeEvent
    public static void onItemCrafted(PlayerEvent.ItemCraftedEvent event) {
        ItemStack stack = event.getCrafting();

        System.out.println("玩家合成物品: " + stack.getItem());

        if (stack.getItem() instanceof HarvestToolItem tool) {
            System.out.println("是採集工具，品級: " + tool.getGrade());

            if (!ToolFactory.isInitialized(stack)) {
                System.out.println("工具未初始化，開始初始化...");
                ToolFactory.initializeTool(stack, tool);
            }
        }
    }
}