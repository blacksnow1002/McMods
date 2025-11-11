package com.blacksnow1002.realmmod.system.profession.reforge;

public enum ArtifactQuality {
    WASTE("廢鐵", 0, 0),
    MORTAL("黃階", 1, 0),
    MYSTIC("玄階", 2, 1),
    EARTH("地階", 3, 2),
    HEAVEN("天階", 4, 3);

    private final String displayName;
    private final int tier;
    private final int affixAmount; // 詞條數量

    ArtifactQuality(String displayName, int tier, int artifactAmount) {
        this.displayName = displayName;
        this.tier = tier;
        this.affixAmount = artifactAmount;
    }

    public String getDisplayName() { return displayName; }
    public int getTier() { return tier; }
    public double getAffixAmount() { return affixAmount; }

    // 提升一階品質
    public ArtifactQuality upgrade() {
        return switch (this) {
            case WASTE -> MORTAL;
            case MORTAL -> MYSTIC;
            case MYSTIC -> EARTH;
            case EARTH, HEAVEN -> HEAVEN;
        };
    }

    //是否為廢丹
    public boolean isWaste() {
        return this == WASTE;
    }
}
