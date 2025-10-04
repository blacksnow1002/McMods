package com.blacksnow1002.realmmod.capability;

public enum CultivationRealm {
    first("凡夫俗子", 1, 1, 1.0f),
    second("練氣", 9, 100, 0.95f),
    third("築基", 9, 200, 0.9f),
    fourth("金丹", 9, 500, 0.8f),
    fifth("元嬰", 9, 1000, 0.7f),
    sixth("化神", 9, 2000, 0.6f),
    seventh("反虛", 9, 500, 0.5f),
    eighth("合體", 9, 10000, 0.25f),
    ninth("渡劫", 9, 20000, 0.1f);

    private final String displayName;
    private final int maxLayer;
    private final int requiredPerLayer;
    private final float BreakthroughSuccessPossibility;

    CultivationRealm(String displayName, int maxLayer, int requiredPerLayer, float BreakthroughSuccessPossibility) {
        this.displayName = displayName;
        this.maxLayer = maxLayer;
        this.requiredPerLayer = requiredPerLayer;
        this.BreakthroughSuccessPossibility = BreakthroughSuccessPossibility;
    }

    public String getDisplayName() {
        return displayName;
    }

    public int getMaxLayer() {
        return maxLayer;
    }

    public int getRequiredPerLayer() {
        return requiredPerLayer;
    }

    public float getBreakthroughSuccessPossibility() {
        return BreakthroughSuccessPossibility;
    }

    public CultivationRealm getNextRealm() {
        int original = this.ordinal();
        return original + 1 < values().length ? values()[original + 1] : this;
    }
}
