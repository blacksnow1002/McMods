package com.blacksnow1002.realmmod.system.assignment.assignments;

import com.blacksnow1002.realmmod.system.assignment.AssignmentCategory;
import com.blacksnow1002.realmmod.system.assignment.AssignmentObjective;
import com.blacksnow1002.realmmod.system.assignment.AssignmentRegistry;
import com.blacksnow1002.realmmod.system.assignment.BaseAssignment;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

public class TestKillEntitySecondAssignment extends BaseAssignment {
    public TestKillEntitySecondAssignment() {
        super(
                AssignmentRegistry.AssignmentIds.TEST_KILL_ENTITY_SECOND_ASSIGNMENT,
                "測試任務-a2",
                "測試測試~",
                AssignmentCategory.MAIN
        );

        addPrerequisite(AssignmentRegistry.AssignmentIds.TEST_KILL_ENTITY_ASSIGNMENT);

        addObjective(new AssignmentObjective(
                "testObjective",
                "擊殺史萊姆一隻-2",
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
