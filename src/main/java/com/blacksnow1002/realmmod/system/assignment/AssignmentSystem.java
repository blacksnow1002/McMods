package com.blacksnow1002.realmmod.system.assignment;

import com.blacksnow1002.realmmod.core.capability.ModCapabilities;
import com.blacksnow1002.realmmod.system.assignment.capability.IAssignmentDataManager;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.network.chat.Component;
import java.util.*;

/**
 * 任務系統 - 管理所有任務相關邏輯
 */
public class AssignmentSystem {

    private static AssignmentSystem INSTANCE;

    private final Map<String, BaseAssignment> assignments = new HashMap<>();

    private final Map<String, List<String>> prerequisiteIndex = new HashMap<>();

    private AssignmentSystem() {

    }

    public static AssignmentSystem getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new AssignmentSystem();
        }
        return INSTANCE;
    }

    public IAssignmentDataManager getPlayerData(ServerPlayer player) {
        return player.getCapability(ModCapabilities.ASSIGNMENT_CAP)
                .orElseThrow(() -> new IllegalStateException("Assignment data manager has not been initialized!"));
    }

    // ==================== 任務註冊 ====================

    public void registerAssignment(BaseAssignment assignment) {
        assignments.put(assignment.getId(), assignment);

        for (String prerequisiteId : assignment.getPrerequisites()) {
            prerequisiteIndex
                    .computeIfAbsent(prerequisiteId, k -> new ArrayList<>())
                    .add(assignment.getId());
        }
    }

    public BaseAssignment getAssignment(String assignmentId) {
        return assignments.get(assignmentId);
    }

    public Collection<BaseAssignment> getAllAssignments() {
        return assignments.values();
    }

    // ==================== 任務接取 ====================

    /**
     * 檢查是否可以接取任務
     */
    public boolean canAccept(ServerPlayer player, String assignmentId) {
        BaseAssignment assignment = assignments.get(assignmentId);
        if (assignment == null) return false;

        IAssignmentDataManager dataManager = getPlayerData(player);

        // 已經接取或完成
        if (dataManager.hasAccepted(assignmentId) || dataManager.hasCompleted(assignmentId)) {
            return false;
        }

        if (!assignment.checkPrerequisites(player, dataManager)) return false;

        return true;
    }

    /**
     * 接取任務
     */
    public boolean acceptAssignment(ServerPlayer player, String assignmentId) {
        BaseAssignment assignment = assignments.get(assignmentId);
        if (assignment == null) {
            player.sendSystemMessage(Component.literal("§c任務不存在！"));
            return false;
        }

        IAssignmentDataManager dataManager = getPlayerData(player);

        // 已經接取
        if (dataManager.hasAccepted(assignmentId)) {
            player.sendSystemMessage(Component.literal("§c你已經接取了這個任務！"));
            return false;
        }

        // 已經完成
        if (dataManager.hasCompleted(assignmentId)) {
            player.sendSystemMessage(Component.literal("§c你已經完成了這個任務！"));
            return false;
        }

        // 檢查前置任務
        if (!assignment.checkPrerequisites(player, dataManager)) {
            List<String> uncompletedPrereqs = assignment.getUncompletedPrerequisites(player, dataManager);
            if (!uncompletedPrereqs.isEmpty()) {
                player.sendSystemMessage(Component.literal("§c需要先完成以下前置任務："));
                for (String prereqId : uncompletedPrereqs) {
                    BaseAssignment prereq = assignments.get(prereqId);
                    if (prereq != null) {
                        player.sendSystemMessage(Component.literal("§e  - " + prereq.getDisplayName()));
                    }
                }
            }
            return false;
        }

        dataManager.setAccepted(assignmentId, true);
        assignment.onAccepted(player);

        player.sendSystemMessage(Component.literal(assignment.getCategory().getColorCode() + "[" + assignment.getCategory().getCategoryName() + "] §a接取任務: " + assignment.getDisplayName()));
        return true;
    }

    // ==================== 任務完成 ====================

    /**
     * 檢查任務是否可以完成
     */
    public boolean canComplete(ServerPlayer player, String assignmentId) {
        BaseAssignment assignment = assignments.get(assignmentId);
        if (assignment == null) return false;

        IAssignmentDataManager dataManager = getPlayerData(player);

        // 未接取
        if (!dataManager.hasAccepted(assignmentId)) {
            return false;
        }

        // 已完成
        if (dataManager.hasCompleted(assignmentId)) {
            return false;
        }

        // 檢查所有目標是否完成
        return assignment.isCompleted(player, dataManager);
    }

    /**
     * 完成任務
     */
    public boolean completeAssignment(ServerPlayer player, String assignmentId) {
        if (!canComplete(player, assignmentId)) {
            return false;
        }

        IAssignmentDataManager dataManager = getPlayerData(player);
        dataManager.setCompleted(assignmentId, true);
        dataManager.setAccepted(assignmentId, false);

        clearAssignmentProgress(dataManager, assignmentId);

        BaseAssignment assignment = assignments.get(assignmentId);
        assignment.onCompleted(player);

        player.sendSystemMessage(Component.literal(assignment.getCategory().getColorCode() + "[" + assignment.getCategory().getCategoryName() + "] §6§l任務完成: " + assignment.getDisplayName()));

        checkUnlockedAssignments(player, dataManager, assignmentId);
        return true;
    }

    /**
     * 檢查是否有新解鎖的任務（優化版）
     */
    private void checkUnlockedAssignments(ServerPlayer player, IAssignmentDataManager dataManager, String completedAssignmentId) {
        // 只檢查與剛完成的任務相關的任務
        List<String> potentialUnlocks = prerequisiteIndex.get(completedAssignmentId);
        if (potentialUnlocks == null || potentialUnlocks.isEmpty()) {
            return; // 沒有任務依賴這個任務
        }

        List<BaseAssignment> newlyUnlocked = new ArrayList<>();

        for (String assignmentId : potentialUnlocks) {
            BaseAssignment assignment = assignments.get(assignmentId);
            if (assignment == null) continue;

            // 跳過已接取或已完成的任務
            if (dataManager.hasAccepted(assignmentId) ||
                    dataManager.hasCompleted(assignmentId)) {
                continue;
            }

            // 檢查所有前置任務是否都完成了
            if (assignment.checkPrerequisites(player, dataManager)) {
                newlyUnlocked.add(assignment);
            }
        }

        if (!newlyUnlocked.isEmpty()) {
            player.sendSystemMessage(Component.literal("§6§l✦ 新任務已解鎖 ✦"));
            for (BaseAssignment assignment : newlyUnlocked) {
                player.sendSystemMessage(Component.literal(
                        assignment.getCategory().getColorCode() + "  [" +
                                assignment.getCategory().getCategoryName() + "] §f" +
                                assignment.getDisplayName()
                ));
            }
        }
    }

    // ==================== 任務進度更新 ====================

    /**
     * 更新任務目標進度
     */
    public void updateObjectiveProgress(ServerPlayer player, String assignmentId, String objectiveId, int progress) {
        IAssignmentDataManager dataManager = getPlayerData(player);

        if (!dataManager.hasAccepted(assignmentId)) {
            return;
        }

        dataManager.setObjectiveProgress(assignmentId, objectiveId, progress);

        // 檢查是否所有目標都完成了
        BaseAssignment assignment = assignments.get(assignmentId);
        if (assignment != null && assignment.isCompleted(player, dataManager)) {
            player.sendSystemMessage(Component.literal("§a任務目標已完成！請返回交付任務"));
        }
    }

    /**
     * 增加任務目標進度
     */
    public void incrementObjectiveProgress(ServerPlayer player, String assignmentId, String objectiveId) {
        IAssignmentDataManager dataManager = getPlayerData(player);

        if (!dataManager.hasAccepted(assignmentId)) {
            return;
        }

        dataManager.incrementObjectiveProgress(assignmentId, objectiveId);

        BaseAssignment assignment = assignments.get(assignmentId);
        if (assignment == null) return;

        // 獲取目標
        AssignmentObjective objective = assignment.getObjectives().stream()
                .filter(obj -> obj.getId().equals(objectiveId))
                .findFirst()
                .orElse(null);

        if (objective != null) {
            int current = dataManager.getObjectiveProgress(assignmentId, objectiveId);
            int required = objective.getRequiredCount();

            // 檢查目標是否完成
            if (current < required) {
                player.sendSystemMessage(Component.literal(
                        String.format("§e任務進度: %s (%d/%d)",
                                objective.getDescription(), current, required)
                ));
            } else {
                player.sendSystemMessage(Component.literal("§a目標完成: " + objective.getDescription()));
            }
        }

        // 檢查是否所有目標都完成了
        if (assignment.isCompleted(player, dataManager)) {
            player.sendSystemMessage(Component.literal("§a§l任務目標全部完成！請返回交付任務"));
        }
    }

    // ==================== 任務檢查方法 ====================

    /**
     * 檢查任務是否已完成（用於功法系統檢查）
     */
    public boolean isAssignmentCompleted(ServerPlayer player, String assignmentId) {
        if (assignmentId == null) return true;
        return getPlayerData(player).hasCompleted(assignmentId);
    }

    public boolean isAssignmentAccepted(ServerPlayer player, String assignmentId) {

        return getPlayerData(player).hasAccepted(assignmentId);
    }

    public int getObjectiveProgress(ServerPlayer player, String assignmentId, String objectiveId) {
        return getPlayerData(player).getObjectiveProgress(assignmentId, objectiveId);
    }

    public Set<String> getAcceptedAssignments(ServerPlayer player) {
        return getPlayerData(player).getAcceptedAssignments();
    }

    public Set<String> getCompletedAssignments(ServerPlayer player) {
        return getPlayerData(player).getCompletedAssignments();
    }

    /**
     * 獲取可接取的任務列表（前置任務已完成）
     */
    public List<BaseAssignment> getAvailableAssignments(ServerPlayer player) {
        IAssignmentDataManager dataManager = getPlayerData(player);
        List<BaseAssignment> available = new ArrayList<>();

        for (BaseAssignment assignment : assignments.values()) {
            if (canAccept(player, assignment.getId())) {
                available.add(assignment);
            }
        }

        return available;
    }

    // ==================== 數據清理 ====================
    private void clearAssignmentProgress(IAssignmentDataManager dataManager, String assignmentId) {
        BaseAssignment assignment = assignments.get(assignmentId);
        if (assignment != null) {
            for (AssignmentObjective objective : assignment.getObjectives()) {
                dataManager.setObjectiveProgress(assignmentId, objective.getId(), 0);
            }
        }
    }

    // ==================== 數據管理 ====================

}