package com.blacksnow1002.realmmod.system.assignment.capability;

import net.minecraft.nbt.CompoundTag;

import java.util.Set;

public interface IAssignmentDataManager {
    boolean hasAccepted(String assignmentId);

    void setAccepted(String assignmentId, boolean accepted);

    boolean hasCompleted(String assignmentId);

    void setCompleted(String assignmentId, boolean completed);

    Set<String> getAcceptedAssignments();

    Set<String> getCompletedAssignments();

    int getObjectiveProgress(String assignmentId, String objectiveId);

    void setObjectiveProgress(String assignmentId, String objectiveId, int progress);

    void incrementObjectiveProgress(String assignmentId, String objectiveId);

    boolean isObjectiveCompleted(String assignmentId, String objectiveId);

    CompoundTag saveNBTData();
    void loadNBTData(CompoundTag nbt);
}
