package com.blacksnow1002.realmmod.assignment.assignments;

import com.blacksnow1002.realmmod.assignment.AssignmentCategory;
import com.blacksnow1002.realmmod.assignment.AssignmentObjective;
import com.blacksnow1002.realmmod.assignment.AssignmentRegistry;
import com.blacksnow1002.realmmod.assignment.BaseAssignment;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;

public class QiCondensationLevel3 extends BaseAssignment {
    public QiCondensationLevel3() {
        super(
                AssignmentRegistry.AssignmentIds.QI_CONDENSATION_LEVEL_3,
                "引氣訣3階進階任務",
                "在岩漿中連續打坐5秒",
                AssignmentCategory.TECHNIQUE
        );

        addObjective(new AssignmentObjective(
                "meditation_in_lava",
                "在岩漿中連續打坐5秒",
                AssignmentObjective.AssignmentObjectiveType.CUSTOM,
                5,
                "lava_meditation"
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
