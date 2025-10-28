package com.blacksnow1002.realmmod.profession;

public enum ProfessionType {
    HARVEST("採集", "harvest"),
    MINING("挖礦", "mining");

    private final String displayName;
    private final String id;

    ProfessionType(String displayName, String id) {
        this.displayName = displayName;
        this.id = id;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getId() {
        return id;
    }

    public static ProfessionType fromId(String id) {
        for (ProfessionType type : values()) {
            if (type.id.equals(id)) {
                return type;
            }
        }
        return HARVEST; // 預設回傳採集
    }
}
