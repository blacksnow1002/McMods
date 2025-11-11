package com.blacksnow1002.realmmod.system.profession;

import com.blacksnow1002.realmmod.RealmMod;
import com.blacksnow1002.realmmod.system.profession.base.item.BaseProfessionCollectionToolItem;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

/**
 * 工具初始化事件监听器
 * 自动为所有继承自BaseProfessionCollectionToolItem的工具进行初始化
 * 支持:
 * - HarvestToolItem (采集工具)
 * - MinableToolItem (采矿工具)
 * - 以及所有其他职业工具
 */
@Mod.EventBusSubscriber(modid = RealmMod.MOD_ID)
public class ToolInitializationEvent {

    @SubscribeEvent
    public static void onItemCrafted(PlayerEvent.ItemCraftedEvent event) {
        ItemStack stack = event.getCrafting();

        System.out.println("玩家合成物品: " + stack.getItem());

        // 检查是否是职业采集工具
        if (stack.getItem() instanceof BaseProfessionCollectionToolItem tool) {
            System.out.println("是职业工具 - 类型: " + tool.getProfessionType() +
                    ", 品级: " + tool.getGrade().getDisplayName() +
                    ", 等级: " + tool.getRank());

            // 如果未初始化,则初始化
            if (!ToolFactory.isInitialized(stack)) {
                System.out.println("工具未初始化,开始初始化...");
                ToolFactory.initializeTool(stack, tool);
            } else {
                System.out.println("工具已经初始化过了");
            }
        }
    }
}