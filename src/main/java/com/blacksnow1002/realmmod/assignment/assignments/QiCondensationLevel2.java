package com.blacksnow1002.realmmod.assignment.assignments;

import com.blacksnow1002.realmmod.assignment.AssignmentCategory;
import com.blacksnow1002.realmmod.assignment.AssignmentObjective;
import com.blacksnow1002.realmmod.assignment.AssignmentRegistry;
import com.blacksnow1002.realmmod.assignment.BaseAssignment;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;

public class QiCondensationLevel2 extends BaseAssignment {
    public QiCondensationLevel2() {
        super(
                AssignmentRegistry.AssignmentIds.QI_CONDENSATION_LEVEL_2,
                "引氣訣2階進階任務",
                "在水中連續打坐10秒",
                AssignmentCategory.TECHNIQUE
        );

        addObjective(new AssignmentObjective(
                "meditation_in_water",
                "在水中連續打坐10秒",
                AssignmentObjective.AssignmentObjectiveType.CUSTOM,
                10,
                "water_meditation"
        ));
    }

    @Override
    public void onAccepted(ServerPlayer player) {
        player.sendSystemMessage(Component.literal("接收水中打坐任務"));
    }

    @Override
    public void onCompleted(ServerPlayer player) {
        player.sendSystemMessage(Component.literal("§6§l§n你已完成水中打坐任務"));
    }
}
