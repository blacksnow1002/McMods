package com.blacksnow1002.realmmod.profession.reforge.capability;

import net.minecraft.nbt.CompoundTag;

import java.util.Set;

public interface IProfessionReforgeData {
    int getRank();
    void setRank(int rank);

    int getExp();
    void setExp(int exp);
    void addExp(int exp);
    void subtractExp(int exp);
    int getRequiredExp();

    ProfessionReforgeData.ReforgeQualityRate getQualityRateBonus(String artifactId);
    void setQualityRateBonus(String artifactId, ProfessionReforgeData.ReforgeQualityRate rate);
    void addQualityRateBonus(String artifactId, ProfessionReforgeData.ReforgeQualityRate rate);

    Set<String> getAllKnownRecipes();
    boolean hasKnownRecipe(String artifactId);
    void setKnownRecipe(String artifactId);

    CompoundTag saveNBTData();
    void loadNBTData(CompoundTag tag);
}