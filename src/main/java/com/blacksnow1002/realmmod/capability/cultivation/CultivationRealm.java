package com.blacksnow1002.realmmod.capability.cultivation;

public enum CultivationRealm {
    first,
    second,
    third,
    fourth,
    fifth,
    sixth,
    seventh,
    eighth,
    ninth,
    tenth;

    private RealmAttributeLoader.RealmConfig cachedConfig;

    /**
     * 獲取配置（帶緩存）
     */
    private RealmAttributeLoader.RealmConfig getConfigCached() {
        if (cachedConfig == null) {
            cachedConfig = RealmAttributeLoader.getConfig(this);
        }
        return cachedConfig;
    }

    /**
     * 清除緩存（資源重載時調用）
     */
    public void clearCache() {
        cachedConfig = null;
    }

    public String getDisplayName() {
        return getConfigCached().displayName;
    }

    public int getMaxLayer() {
        return getConfigCached().maxLayer;
    }

    public int getRequiredPerLayer() {
        return getConfigCached().requiredPerLayer;
    }

    public float getBreakthroughSuccessPossibility() {
        return getConfigCached().breakthroughSuccessPossibility;
    }

    public CultivationRealm getNextRealm() {
        int original = this.ordinal();
        return original + 1 < values().length ? values()[original + 1] : this;
    }
}
