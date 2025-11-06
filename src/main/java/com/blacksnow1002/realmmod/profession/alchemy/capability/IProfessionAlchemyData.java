package com.blacksnow1002.realmmod.profession.alchemy.capability;

import net.minecraft.nbt.CompoundTag;

import java.util.Set;

public interface IProfessionAlchemyData {
    int getRank();
    void setRank(int rank);

    int getExp();
    void setExp(int exp);
    void addExp(int exp);
    void subtractExp(int exp);
    int getRequiredExp();

    ProfessionAlchemyData.AlchemyQualityRate getQualityRateBonus(String pillId);
    void setQualityRateBonus(String pillId, ProfessionAlchemyData.AlchemyQualityRate rate);
    void addQualityRateBonus(String pillId, ProfessionAlchemyData.AlchemyQualityRate rate);

    Set<String> getAllKnownRecipes();
    boolean hasKnownRecipe(String pillId);
    void setKnownRecipe(String pillId);

    CompoundTag saveNBTData();
    void loadNBTData(CompoundTag tag);
}