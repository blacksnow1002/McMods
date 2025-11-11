package com.blacksnow1002.realmmod.system.assignment.assignments;

import com.blacksnow1002.realmmod.system.assignment.AssignmentCategory;
import com.blacksnow1002.realmmod.system.assignment.AssignmentObjective;
import com.blacksnow1002.realmmod.system.assignment.AssignmentRegistry;
import com.blacksnow1002.realmmod.system.assignment.BaseAssignment;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;

/**
 * 火焰至尊功第3層進階任務
 */
public class FireSupremeLevel3Assignment extends BaseAssignment {

    public FireSupremeLevel3Assignment() {
        super(
                AssignmentRegistry.AssignmentIds.FIRE_SUPREME_LEVEL_3,
                "火焰護體試煉",
                "在岩漿中冥想，領悟火焰護體之法",
                AssignmentCategory.TECHNIQUE
        );

        addObjective(new AssignmentObjective(
                "meditate_in_lava",
                "在岩漿中停留20秒",
                AssignmentObjective.AssignmentObjectiveType.CUSTOM,
                60,
                "lava_meditation"
        ));
    }

    @Override
    public void onAccepted(ServerPlayer player) {
        player.sendSystemMessage(Component.literal("§c尋找岩漿，在其中冥想以領悟火焰護體"));
    }

    @Override
    public void onCompleted(ServerPlayer player) {
        player.sendSystemMessage(Component.literal("§6§l你領悟了火焰護體！功法可以進階至第3層了"));
    }
}