package com.blacksnow1002.realmmod.system.profession.alchemy;

public enum PillQuality {
    WASTE("廢丹", 0, 0.0),      // 廢丹，直接消失
    FLOATING("浮紋", 1, 1.0),   // 一階
    CLOUD("雲紋", 2, 1.5),      // 二階
    SPIRIT("靈紋", 3, 2.0),     // 三階
    DAO("道紋", 4, 3.0);        // 四階

    private final String displayName;
    private final int tier;
    private final double valueMultiplier; // 效果倍率

    PillQuality(String displayName, int tier, double valueMultiplier) {
        this.displayName = displayName;
        this.tier = tier;
        this.valueMultiplier = valueMultiplier;
    }

    public String getDisplayName() { return displayName; }
    public int getTier() { return tier; }
    public double getValueMultiplier() { return valueMultiplier; }

    // 提升一階品質
    public PillQuality upgrade() {
        return switch (this) {
            case WASTE -> FLOATING;
            case FLOATING -> CLOUD;
            case CLOUD -> SPIRIT;
            case SPIRIT, DAO -> DAO;
        };
    }

    //是否為廢丹
    public boolean isWaste() {
        return this == WASTE;
    }
}
