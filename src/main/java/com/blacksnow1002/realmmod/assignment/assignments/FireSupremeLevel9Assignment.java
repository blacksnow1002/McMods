package com.blacksnow1002.realmmod.assignment.assignments;

import com.blacksnow1002.realmmod.assignment.AssignmentCategory;
import com.blacksnow1002.realmmod.assignment.AssignmentObjective;
import com.blacksnow1002.realmmod.assignment.AssignmentRegistry;
import com.blacksnow1002.realmmod.assignment.BaseAssignment;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

/**
 * 火焰至尊功第9層進階任務
 */
public class FireSupremeLevel9Assignment extends BaseAssignment {

    public FireSupremeLevel9Assignment() {
        super(
                AssignmentRegistry.AssignmentIds.FIRE_SUPREME_LEVEL_9,
                "焚天滅地試煉",
                "擊敗末影龍，以最強之焰證明你的力量",
                AssignmentCategory.TECHNIQUE
        );

        addObjective(new AssignmentObjective(
                "collect_dragon_egg",
                "獲得龍蛋",
                AssignmentObjective.AssignmentObjectiveType.COLLECT_ITEM,
                1,
                "minecraft:dragon_egg"
        ));
    }

    @Override
    public void onAccepted(ServerPlayer player) {
        player.sendSystemMessage(Component.literal("§c§l前往末地，擊敗末影龍，以最強之焰證明你的力量！"));
    }

    @Override
    public void onCompleted(ServerPlayer player) {
        player.sendSystemMessage(Component.literal("§6§l§n你已達火焰至尊功最高境界！焚天滅地之力已屬於你！"));

        // 給予特殊獎勵
        player.addItem(new ItemStack(Items.TOTEM_OF_UNDYING));
        player.sendSystemMessage(Component.literal("§a獲得獎勵: 不死圖騰"));
    }
}
