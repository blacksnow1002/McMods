package com.blacksnow1002.realmmod.capability.cultivation;

public enum CultivationRealm {
    first("凡夫俗子", 1, 1, 1.0f, 40, 0, 0),
    second("練氣", 10, 100, 0.95f, 60, 100, 1),
    third("築基", 10, 200, 0.9f, 120, 250, 2),
    fourth("金丹", 10, 500, 0.8f, 300, 600, 4),
    fifth("元嬰", 10, 1000, 0.7f, 1200, 1500, 6),
    sixth("化神", 10, 2000, 0.6f, 5000, 2500, 10),
    seventh("反虛", 10, 5000, 0.5f, 15000, 4000, 15),
    eighth("合體", 10, 10000, 0.35f, 50000, 6000, 20),
    ninth("大乘", 10, 20000, 0.2f, 200000, 9000, 30),
    tenth("渡劫", 10, 20000, 0.1f, 1000000, 15000, 50);

    private final String displayName;
    private final int maxLayer;
    private final int requiredPerLayer;
    private final float BreakthroughSuccessPossibility;
    private final int realmAge;
    private final int realmMagicPoint;
    private final int magicPointReceivePerSecond;

    CultivationRealm(String displayName,
                     int maxLayer,
                     int requiredPerLayer,
                     float BreakthroughSuccessPossibility,
                     int realmAge,
                     int realmMagicPoint,
                     int magicPointReceivePerSecond) {
        this.displayName = displayName;
        this.maxLayer = maxLayer;
        this.requiredPerLayer = requiredPerLayer;
        this.BreakthroughSuccessPossibility = BreakthroughSuccessPossibility;
        this.realmAge = realmAge;
        this.realmMagicPoint = realmMagicPoint;
        this.magicPointReceivePerSecond = magicPointReceivePerSecond;
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
