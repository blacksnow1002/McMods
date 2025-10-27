package com.blacksnow1002.realmmod.profession.harvest;

import net.minecraft.nbt.CompoundTag;

public interface IProfessionHarvestData {
    int getRank();
    void setRank(int rank);

    int getExp();
    void setExp(int exp);
    void addExp(int exp);
    void subtractExp(int exp);
    int getRequiredExp();

    double getSuccessRateBonus(String blockId);
    void setSuccessRateBonus(String blockId, double rate);
    void addSuccessRateBonus(String blockId, double rate);

    boolean hasFirstSuccess(int rank);
    void setFirstSuccess(int rank);

    boolean isHeartDemon();
    void setHeartDemon(boolean state);

    int getDailyTreasureCount(int rank);
    void incrementDailyTreasure(int rank);
    void clearDailyTreasure();

    CompoundTag saveNBTData();
    void loadNBTData(CompoundTag tag);
}