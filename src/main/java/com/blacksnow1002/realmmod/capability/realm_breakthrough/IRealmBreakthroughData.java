package com.blacksnow1002.realmmod.capability.realm_breakthrough;

public interface IRealmBreakthroughData {
    public boolean canBreakthrough(int realmIndex);
    public boolean[] getCanBreakthrough();
    public void setCanBreakthrough(int realmIndex, boolean value);

    public void updateCondition(int realmIndex, int conditionId, boolean completed);

    public String getBreakthroughProgress(int realmIndex);
}
