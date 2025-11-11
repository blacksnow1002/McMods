package com.blacksnow1002.realmmod.system.assignment.handler;

import com.blacksnow1002.realmmod.system.assignment.AssignmentObjective;
import com.blacksnow1002.realmmod.system.assignment.AssignmentSystem;
import com.blacksnow1002.realmmod.system.assignment.BaseAssignment;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.Set;

/**
 * 任務事件處理器 - 監聽遊戲事件並更新任務進度
 */
@Mod.EventBusSubscriber(modid = "realmmod")
public class AssignmentEventHandler {


    /**
     * 監聽實體死亡事件（擊殺目標）
     */
    @SubscribeEvent
    public static void onEntityKilled(LivingDeathEvent event) {
        if (!(event.getSource().getEntity() instanceof ServerPlayer player)) {
            return;
        }

        LivingEntity killed = event.getEntity();
        String entityId = killed.getType().toString();

        System.out.println("Killed Entity: " + killed.getType());
        System.out.println("Killed ID: " + entityId);


        AssignmentSystem assignmentSystem = AssignmentSystem.getInstance();
        Set<String> acceptedAssignments = assignmentSystem.getAcceptedAssignments(player);

        // 遍歷玩家已接取的任務
        for (String assignmentId : acceptedAssignments) {
            BaseAssignment assignment = assignmentSystem.getAssignment(assignmentId);
            if (assignment == null) continue;

            // 檢查任務目標
            for (AssignmentObjective objective : assignment.getObjectives()) {
                if (objective.getType() == AssignmentObjective.AssignmentObjectiveType.KILL_ENTITY) {
                    // 檢查是否是目標實體
                    if (entityId.equals(objective.getTargetId())) {
                        int currentProgress = assignmentSystem.getObjectiveProgress(player, assignmentId, objective.getId());
                        int requiredCount = objective.getRequiredCount();

                        if (currentProgress < requiredCount) {
                            assignmentSystem.incrementObjectiveProgress(player, assignmentId, objective.getId());
                        }
                    }
                }
            }
        }
    }

    /**
     * 監聽物品拾取事件（收集物品）
     */
    @SubscribeEvent
    public static void onItemPickup(PlayerEvent.ItemPickupEvent event) {
        if (!(event.getEntity() instanceof ServerPlayer player)) {
            return;
        }

        ItemStack pickedItem = event.getStack();
        String itemId = pickedItem.getItem().toString();

        AssignmentSystem assignmentSystem = AssignmentSystem.getInstance();
        Set<String> acceptedAssignments = assignmentSystem.getAcceptedAssignments(player);

        // 遍歷玩家已接取的任務
        for (String assignmentId : acceptedAssignments) {
            BaseAssignment assignment = assignmentSystem.getAssignment(assignmentId);
            if (assignment == null) continue;

            // 檢查任務目標
            for (AssignmentObjective objective : assignment.getObjectives()) {
                if (objective.getType() == AssignmentObjective.AssignmentObjectiveType.COLLECT_ITEM) {
                    // 檢查是否是目標物品
                    if (itemId.contains(objective.getTargetId())) {
                        // 計算玩家背包中該物品的數量
                        int totalCount = countItemInInventory(player, objective.getTargetId());
                        assignmentSystem.updateObjectiveProgress(player, assignmentId, objective.getId(), totalCount);
                    }
                }
            }
        }
    }

    /**
     * 監聽物品合成事件
     */
    @SubscribeEvent
    public static void onItemCrafted(PlayerEvent.ItemCraftedEvent event) {
        if (!(event.getEntity() instanceof ServerPlayer player)) {
            return;
        }

        ItemStack craftedItem = event.getCrafting();
        String itemId = craftedItem.getItem().toString();

        AssignmentSystem assignmentSystem = AssignmentSystem.getInstance();
        Set<String> acceptedAssignments = assignmentSystem.getAcceptedAssignments(player);

        for (String assignmentId : acceptedAssignments) {
            BaseAssignment assignment = assignmentSystem.getAssignment(assignmentId);
            if (assignment == null) continue;

            for (AssignmentObjective objective : assignment.getObjectives()) {
                if (objective.getType() == AssignmentObjective.AssignmentObjectiveType.CRAFT_ITEM) {
                    if (itemId.contains(objective.getTargetId())) {
                        assignmentSystem.incrementObjectiveProgress(player, assignmentId, objective.getId());
                    }
                }
            }
        }
    }

    /**
     * 計算玩家背包中特定物品的數量
     */
    private static int countItemInInventory(Player player, String itemId) {
        int count = 0;
        for (ItemStack stack : player.getInventory().items) {
            if (stack.getItem().toString().contains(itemId)) {
                count += stack.getCount();
            }
        }
        return count;
    }
}