package com.blacksnow1002.realmmod.assignment.npc.npcs;

import com.blacksnow1002.realmmod.assignment.AssignmentRegistry;
import com.blacksnow1002.realmmod.assignment.npc.BaseNPC;
import com.blacksnow1002.realmmod.assignment.npc.NPCRegistry;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;

public class TestNPC extends BaseNPC {

    public TestNPC() {
        super(NPCRegistry.NPCIds.TEST_NPC, "任務測試員");

        this.addAssignment(AssignmentRegistry.AssignmentIds.TEST_KILL_ENTITY_ASSIGNMENT);
        this.addAssignment(AssignmentRegistry.AssignmentIds.TEST_KILL_ENTITY_SECOND_ASSIGNMENT);
        this.addAssignment(AssignmentRegistry.AssignmentIds.TEST_ANOTHER_ASSIGNMENT);
        this.addAssignment(AssignmentRegistry.AssignmentIds.TEST_ANOTHER_SECOND_ASSIGNMENT);
    }

    @Override
    protected String getDialogueText() {
        return "你好冒險家，我有一些任務需要幫忙";
    }

    @Override
    protected void onAssignmentAccepted(ServerPlayer player, String assignmentId) {
        player.sendSystemMessage(Component.literal(
                "§e[任務測試員] 祝你好運！"
        ));
    }

    @Override
    protected void onAssignmentCompleted(ServerPlayer player, String assignmentId) {
        player.sendSystemMessage(Component.literal(
                "§e[任務測試員] 做得好！這是你應得的獎勵！"
        ));
    }
}
