package com.blacksnow1002.realmmod.assignment;

import net.minecraft.server.level.ServerPlayer;

import java.util.*;

/**
 * 基礎任務類
 */
public abstract class BaseAssignment {

    private final String id;
    private final String displayName;
    private final String description;
    private final AssignmentCategory category;
    private final List<AssignmentObjective> objectives;
    private final List<String> prerequisites;

    public BaseAssignment(String id, String displayName, String description, AssignmentCategory category) {
        this.id = id;
        this.displayName = displayName;
        this.description = description;
        this.category = category;
        this.objectives = new ArrayList<>();
        this.prerequisites = new ArrayList<>();
    }

    public String getId() {
        return id;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getDescription() {
        return description;
    }

    public AssignmentCategory getCategory() { return category; }

    public List<AssignmentObjective> getObjectives() {
        return objectives;
    }

    public List<String> getPrerequisites() {
        return prerequisites;
    }

    protected void addObjective(AssignmentObjective objective) {
        objectives.add(objective);
    }

    protected void addPrerequisite(String assignmentId) {
        prerequisites.add(assignmentId);
    }

    protected void addPrerequisites(String... assignmentId) {
        prerequisites.addAll(Arrays.asList(assignmentId));
    }

    public boolean checkPrerequisites(ServerPlayer player, IAssignmentDataManager dataManager) {
        if (prerequisites.isEmpty()) { return true; }

        for (String prerequisiteId : prerequisites) {
            if (!dataManager.hasCompleted(prerequisiteId)) {
                return false;
            }
        }
        return true;
    }

    public List<String> getUncompletedPrerequisites(ServerPlayer player, IAssignmentDataManager dataManager) {
        List<String> uncompletedPrerequisites = new ArrayList<>();
        for (String prerequisiteId : prerequisites) {
            if (!dataManager.hasCompleted(prerequisiteId)) {
                uncompletedPrerequisites.add(prerequisiteId);
            }
        }
        return uncompletedPrerequisites;
    }
    /**
     * 檢查任務是否完成
     */
    public boolean isCompleted(ServerPlayer player, IAssignmentDataManager dataManager) {
        for (AssignmentObjective objective : objectives) {
            if (!dataManager.isObjectiveCompleted(id, objective.getId())) {
                return false;
            }
        }
        return true;
    }

    /**
     * 任務完成時的回調
     */
    public void onCompleted(ServerPlayer player) {
        // 子類可覆寫
    }

    /**
     * 任務接取時的回調
     */
    public void onAccepted(ServerPlayer player) {
        // 子類可覆寫
    }
}