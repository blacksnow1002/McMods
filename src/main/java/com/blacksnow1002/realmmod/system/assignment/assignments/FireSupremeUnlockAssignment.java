package com.blacksnow1002.realmmod.system.assignment.assignments;

import com.blacksnow1002.realmmod.system.assignment.AssignmentCategory;
import com.blacksnow1002.realmmod.system.assignment.AssignmentRegistry;
import com.blacksnow1002.realmmod.system.assignment.BaseAssignment;
import com.blacksnow1002.realmmod.system.assignment.AssignmentObjective;
import com.blacksnow1002.realmmod.system.assignment.AssignmentObjective.AssignmentObjectiveType;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.ItemStack;

/**
 * 火焰至尊功解鎖任務
 */
public class FireSupremeUnlockAssignment extends BaseAssignment {

    public FireSupremeUnlockAssignment() {
        super(
                AssignmentRegistry.AssignmentIds.FIRE_SUPREME_UNLOCK,
                "火焰試煉",
                "收集火焰精華並擊敗烈焰人，證明你有資格修煉火焰至尊功",
                AssignmentCategory.TECHNIQUE
        );

        // 添加任務目標
        addObjective(new AssignmentObjective(
                "collect_blaze_powder",
                "收集烈焰粉",
                AssignmentObjectiveType.COLLECT_ITEM,
                10,
                "minecraft:blaze_powder"
        ));

        addObjective(new AssignmentObjective(
                "kill_blaze",
                "擊殺烈焰人",
                AssignmentObjectiveType.KILL_ENTITY,
                5,
                "minecraft:blaze"
        ));
    }

    @Override
    public void onAccepted(ServerPlayer player) {
        player.sendSystemMessage(Component.literal("§c火焰試煉開始！收集10個烈焰粉並擊殺5個烈焰人"));
    }

    @Override
    public void onCompleted(ServerPlayer player) {
        player.sendSystemMessage(Component.literal("§6§l你通過了火焰試煉！現在可以解鎖火焰至尊功了"));

        // 給予獎勵
        player.addItem(new ItemStack(Items.FIRE_CHARGE, 5));
        player.sendSystemMessage(Component.literal("§a獲得獎勵: 5x 火焰彈"));
    }
}


