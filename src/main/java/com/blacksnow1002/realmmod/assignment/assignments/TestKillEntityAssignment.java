package com.blacksnow1002.realmmod.assignment.assignments;

import com.blacksnow1002.realmmod.assignment.AssignmentCategory;
import com.blacksnow1002.realmmod.assignment.AssignmentObjective;
import com.blacksnow1002.realmmod.assignment.AssignmentRegistry;
import com.blacksnow1002.realmmod.assignment.BaseAssignment;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

public class TestKillEntityAssignment extends BaseAssignment {
    public TestKillEntityAssignment() {
        super(
                AssignmentRegistry.AssignmentIds.TEST_KILL_ENTITY_ASSIGNMENT,
                "測試任務-a1",
                "測試測試~",
                AssignmentCategory.MAIN
        );

        addObjective(new AssignmentObjective(
                "testObjective",
                "擊殺史萊姆一隻-1",
                AssignmentObjective.AssignmentObjectiveType.KILL_ENTITY,
                5,
                "entity.minecraft.slime"
        ));
    }

    @Override
    public void onAccepted(ServerPlayer player) {
        player.sendSystemMessage(Component.literal("接收測試任務"));
    }

    @Override
    public void onCompleted(ServerPlayer player) {
        player.sendSystemMessage(Component.literal("§6§l§n完成測試任務"));
        player.getInventory().add(new ItemStack(Items.SLIME_BALL, 10));
    }
}
