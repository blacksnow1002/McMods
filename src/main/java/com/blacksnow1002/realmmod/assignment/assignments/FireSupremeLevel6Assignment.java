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
 * 火焰至尊功第6層進階任務
 */
public class FireSupremeLevel6Assignment extends BaseAssignment {

    public FireSupremeLevel6Assignment() {
        super(
                AssignmentRegistry.AssignmentIds.FIRE_SUPREME_LEVEL_6,
                "鳳凰涅槃試煉",
                "擊敗凋零並收集地獄之星，領悟涅槃重生之法",
                AssignmentCategory.TECHNIQUE
        );

        addObjective(new AssignmentObjective(
                "collect_nether_star",
                "收集地獄之星",
                AssignmentObjective.AssignmentObjectiveType.COLLECT_ITEM,
                1,
                "minecraft:nether_star"
        ));
    }

    @Override
    public void onAccepted(ServerPlayer player) {
        player.sendSystemMessage(Component.literal("§c召喚並擊敗凋零，從死亡中領悟涅槃之力"));
    }

    @Override
    public void onCompleted(ServerPlayer player) {
        if (player.getInventory().contains(new ItemStack(Items.NETHER_STAR))) player.getInventory().removeItem(new ItemStack(Items.NETHER_STAR));
        player.sendSystemMessage(Component.literal("§6§l你領悟了鳳凰涅槃！功法可以進階至第6層了"));
    }
}
