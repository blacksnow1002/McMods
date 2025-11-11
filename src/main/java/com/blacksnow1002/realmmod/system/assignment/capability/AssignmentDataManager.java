package com.blacksnow1002.realmmod.system.assignment.capability;

import com.blacksnow1002.realmmod.system.assignment.AssignmentObjective;
import com.blacksnow1002.realmmod.system.assignment.AssignmentSystem;
import com.blacksnow1002.realmmod.system.assignment.BaseAssignment;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;

import java.util.*;

/**
 * 任務數據管理器
 */
public class AssignmentDataManager implements IAssignmentDataManager {

    private final Set<String> acceptedAssignments = new HashSet<>();
    private final Set<String> completedAssignments = new HashSet<>();
    private final Map<String, Map<String, Integer>> objectiveProgress = new HashMap<>();

    // ==================== 任務狀態 ====================

    @Override
    public boolean hasAccepted(String assignmentId) {
        return acceptedAssignments.contains(assignmentId);
    }

    @Override
    public void setAccepted(String assignmentId, boolean accepted) {
        if (accepted) {
            acceptedAssignments.add(assignmentId);
        } else {
            acceptedAssignments.remove(assignmentId);
        }
    }

    @Override
    public boolean hasCompleted(String assignmentId) {
        return completedAssignments.contains(assignmentId);
    }

    @Override
    public void setCompleted(String assignmentId, boolean completed) {
        if (completed) {
            completedAssignments.add(assignmentId);
        } else {
            completedAssignments.remove(assignmentId);
        }
    }

    @Override
    public Set<String> getAcceptedAssignments() {
        return new HashSet<>(acceptedAssignments);
    }

    @Override
    public Set<String> getCompletedAssignments() {
        return new HashSet<>(completedAssignments);
    }

    // ==================== 目標進度 ====================

    @Override
    public int getObjectiveProgress(String assignmentId, String objectiveId) {
        Map<String, Integer> assignmentProgress = objectiveProgress.get(assignmentId);
        if (assignmentProgress == null) return 0;
        return assignmentProgress.getOrDefault(objectiveId, 0);
    }

    @Override
    public void setObjectiveProgress(String assignmentId, String objectiveId, int progress) {
        objectiveProgress
                .computeIfAbsent(assignmentId, k -> new HashMap<>())
                .put(objectiveId, progress);
    }

    @Override
    public void incrementObjectiveProgress(String assignmentId, String objectiveId) {
        int current = getObjectiveProgress(assignmentId, objectiveId);
        System.out.println("更新目標進度： " + assignmentId + " - " + objectiveId);
        setObjectiveProgress(assignmentId, objectiveId, current + 1);
    }

    @Override
    public boolean isObjectiveCompleted(String assignmentId, String objectiveId) {
        AssignmentSystem system =
                AssignmentSystem.getInstance();
        BaseAssignment assignment =
                system.getAssignment(assignmentId);

        if (assignment == null) return false;

        AssignmentObjective objective =
                assignment.getObjectives().stream()
                        .filter(obj -> obj.getId().equals(objectiveId))
                        .findFirst()
                        .orElse(null);
        if (objective == null) return false;

        int currentProgress = getObjectiveProgress(assignmentId, objectiveId);
        int requiredCount = objective.getRequiredCount();
        return currentProgress == requiredCount;
    }

    // ==================== NBT 序列化 ====================

    @Override
    public CompoundTag saveNBTData() {
        CompoundTag nbt = new CompoundTag();

        // 已接取任務
        ListTag acceptedList = new ListTag();
        for (String id : acceptedAssignments) {
            CompoundTag tag = new CompoundTag();
            tag.putString("id", id);
            acceptedList.add(tag);
        }
        nbt.put("accepted", acceptedList);

        // 已完成任務
        ListTag completedList = new ListTag();
        for (String id : completedAssignments) {
            CompoundTag tag = new CompoundTag();
            tag.putString("id", id);
            completedList.add(tag);
        }
        nbt.put("completed", completedList);

        // 任務目標進度
        CompoundTag progressTag = new CompoundTag();
        for (Map.Entry<String, Map<String, Integer>> entry : objectiveProgress.entrySet()) {
            CompoundTag assignmentTag = new CompoundTag();
            for (Map.Entry<String, Integer> objEntry : entry.getValue().entrySet()) {
                if (objEntry.getValue() > 0) {
                    assignmentTag.putInt(objEntry.getKey(), objEntry.getValue());
                }
            }
            if (!assignmentTag.getAllKeys().isEmpty()) {
                progressTag.put(entry.getKey(), assignmentTag);
            }
        }
        nbt.put("progress", progressTag);

        return nbt;
    }

    @Override
    public void loadNBTData(CompoundTag nbt) {
        acceptedAssignments.clear();
        completedAssignments.clear();
        objectiveProgress.clear();

        // 已接取任務
        if (nbt.contains("accepted", Tag.TAG_LIST)) {
            ListTag acceptedList = nbt.getList("accepted", Tag.TAG_COMPOUND);
            for (int i = 0; i < acceptedList.size(); i++) {
                acceptedAssignments.add(acceptedList.getCompound(i).getString("id"));
            }
        }

        // 已完成任務
        if (nbt.contains("completed", Tag.TAG_LIST)) {
            ListTag completedList = nbt.getList("completed", Tag.TAG_COMPOUND);
            for (int i = 0; i < completedList.size(); i++) {
                completedAssignments.add(completedList.getCompound(i).getString("id"));
            }
        }

        // 任務目標進度
        if (nbt.contains("progress", Tag.TAG_COMPOUND)) {
            CompoundTag progressTag = nbt.getCompound("progress");
            for (String assignmentId : progressTag.getAllKeys()) {
                CompoundTag assignmentTag = progressTag.getCompound(assignmentId);
                Map<String, Integer> map = new HashMap<>();
                for (String objId : assignmentTag.getAllKeys()) {
                    map.put(objId, assignmentTag.getInt(objId));
                }
                objectiveProgress.put(assignmentId, map);
            }
        }
    }
}