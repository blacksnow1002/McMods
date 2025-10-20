package com.blacksnow1002.realmmod.assignment.assignments;

import com.blacksnow1002.realmmod.assignment.AssignmentCategory;
import com.blacksnow1002.realmmod.assignment.AssignmentObjective;
import com.blacksnow1002.realmmod.assignment.AssignmentRegistry;
import com.blacksnow1002.realmmod.assignment.BaseAssignment;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

public class TestAnotherSecondAssignment extends BaseAssignment {
    public TestAnotherSecondAssignment() {
        super(
                AssignmentRegistry.AssignmentIds.TEST_ANOTHER_SECOND_ASSIGNMENT,
                "測試任務-b2",
                "測試測試~",
                AssignmentCategory.MAIN
        );

        addPrerequisite(AssignmentRegistry.AssignmentIds.TEST_ANOTHER_ASSIGNMENT);

        addObjective(new AssignmentObjective(
                "testObjective",
                "擊殺殭屍兩隻-2",
                AssignmentObjective.AssignmentObjectiveType.KILL_ENTITY,
                2,
                "entity.minecraft.zombie"
        ));
    }

    @Override
    public void onAccepted(ServerPlayer player) {
        player.sendSystemMessage(Component.literal("接收測試任務"));
    }

    @Override
    public void onCompleted(ServerPlayer player) {
        player.sendSystemMessage(Component.literal("§6§l§n完成測試任務"));
        player.getInventory().add(new ItemStack(Items.BEEF, 10));
    }
}
