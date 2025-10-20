package com.blacksnow1002.realmmod.capability.realm_breakthrough;

public interface IRealmBreakthroughData {
    boolean canBreakthrough(int realmIndex);
    boolean[] getCanBreakthrough();
    void setCanBreakthrough(int realmIndex, boolean value);

    void updateCondition(int realmIndex, int conditionId, boolean completed);

    String getBreakthroughProgress(int realmIndex);

    boolean getRealmConditionFinished(int realmIndex, int conditionId);
}
