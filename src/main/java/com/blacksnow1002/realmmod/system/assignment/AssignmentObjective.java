package com.blacksnow1002.realmmod.system.assignment;

/**
 * 任務目標
 */
public class AssignmentObjective {

    private final String id;
    private final String description;
    private final AssignmentObjectiveType type;
    private final int requiredCount;
    private final String targetId; // 目標ID（怪物、物品等）

    public AssignmentObjective(String id, String description, AssignmentObjectiveType type,
                          int requiredCount, String targetId) {
        this.id = id;
        this.description = description;
        this.type = type;
        this.requiredCount = requiredCount;
        this.targetId = targetId;
    }

    public String getId() {
        return id;
    }

    public String getDescription() {
        return description;
    }

    public AssignmentObjectiveType getType() {
        return type;
    }

    public int getRequiredCount() {
        return requiredCount;
    }

    public String getTargetId() {
        return targetId;
    }

    /**
     * 任務目標類型
     */
    public enum AssignmentObjectiveType {
        KILL_ENTITY,        // 擊殺實體
        COLLECT_ITEM,       // 收集物品
        REACH_REALM,        // 達到境界
        REACH_LOCATION,     // 到達地點
        INTERACT_BLOCK,     // 互動方塊
        INTERACT_ENTITY,    // 互動實體
        CRAFT_ITEM,         // 合成物品
        USE_ITEM,           // 使用物品
        CUSTOM              // 自定義
    }
}