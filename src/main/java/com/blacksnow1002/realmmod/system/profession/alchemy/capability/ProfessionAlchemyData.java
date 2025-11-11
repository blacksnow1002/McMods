package com.blacksnow1002.realmmod.system.profession.alchemy.capability;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class ProfessionAlchemyData implements IProfessionAlchemyData {
    private int rank = 0; // 0=未入門, 1~9品
    private int exp = 0;
    private final int[] requiredExp = {0, 984100, 328000, 109300, 36400, 12100, 4000, 1300, 400, 100};

    public record AlchemyQualityRate(float floating, float cloud, float spirit, float dao) {};
    private Map<String, AlchemyQualityRate> qualityRate = new HashMap<>();

    private Set<String> knownRecipes = new HashSet<>();

    @Override
    public int getRank() {
        return rank;
    }

    @Override
    public void setRank(int rank) { this.rank = Math.max(0, Math.min(9, rank)); }

    @Override
    public int getExp() {
        return exp;
    }

    @Override
    public void setExp(int exp) {
        this.exp = exp;
    }

    @Override
    public void addExp(int exp) {
        if (this.rank > 0 && this.rank <= 9) {
            this.exp = Math.min(requiredExp[this.rank], this.exp + exp);
        }
    }

    @Override
    public void subtractExp(int exp) {
        this.exp = Math.max(0, this.exp - exp);
    }

    @Override
    public int getRequiredExp() {
        if (rank >= 0 && rank <= 9) {
            return requiredExp[rank];
        }
        return 0;
    }

    @Override
    public AlchemyQualityRate getQualityRateBonus(String pillId) {
        return qualityRate.getOrDefault(pillId, new AlchemyQualityRate(0, 0, 0, 0));
    }

    @Override
    public void setQualityRateBonus(String pillId, AlchemyQualityRate rate) {
        qualityRate.put(pillId, rate);
    }

    @Override
    public void addQualityRateBonus(String pillId, AlchemyQualityRate rate) {
        AlchemyQualityRate successRate = getQualityRateBonus(pillId);
        setQualityRateBonus(pillId, new AlchemyQualityRate(
                successRate.floating + rate.floating,
                successRate.cloud + rate.cloud,
                successRate.spirit + rate.spirit,
                successRate.dao + rate.dao));
    }

    @Override
    public Set<String> getAllKnownRecipes() {
        return knownRecipes;
    }

    @Override
    public boolean hasKnownRecipe(String pillId) {
        return knownRecipes.contains(pillId);
    }

    @Override
    public void setKnownRecipe(String pillId) {
        knownRecipes.add(pillId);
    }


    @Override
    public CompoundTag saveNBTData() {
        CompoundTag tag = new CompoundTag();
        tag.putInt("AlchemyRank", rank);
        tag.putInt("AlchemyExp", exp);

        // 存 qualityRate
        ListTag rateList = new ListTag();
        for (Map.Entry<String, AlchemyQualityRate> entry : qualityRate.entrySet()) {
            CompoundTag rateTag = new CompoundTag();
            rateTag.putString("pillId", entry.getKey());
            AlchemyQualityRate rate = entry.getValue();
            rateTag.putFloat("mortal", rate.floating());
            rateTag.putFloat("mystic", rate.cloud());
            rateTag.putFloat("earth", rate.spirit());
            rateTag.putFloat("heaven", rate.dao());
            rateList.add(rateTag);
        }
        tag.put("QualityRateList", rateList);

        // 存 knownRecipes
        ListTag recipeList = new ListTag();
        for (String recipe : knownRecipes) {
            CompoundTag recipeTag = new CompoundTag();
            recipeTag.putString("id", recipe);
            recipeList.add(recipeTag);
        }
        tag.put("KnownRecipes", recipeList);

        return tag;
    }


    @Override
    public void loadNBTData(CompoundTag tag) {
        this.rank = tag.getInt("AlchemyRank");
        this.exp = tag.getInt("AlchemyExp");

        // 讀 qualityRate
        qualityRate.clear();
        ListTag rateList = tag.getList("QualityRateList", Tag.TAG_COMPOUND);
        for (int i = 0; i < rateList.size(); i++) {
            CompoundTag rateTag = rateList.getCompound(i);
            String pillId = rateTag.getString("pillId");
            AlchemyQualityRate rate = new AlchemyQualityRate(
                    rateTag.getFloat("mortal"),
                    rateTag.getFloat("mystic"),
                    rateTag.getFloat("earth"),
                    rateTag.getFloat("heaven")
            );
            qualityRate.put(pillId, rate);
        }

        // 讀 knownRecipes
        knownRecipes.clear();
        ListTag recipeList = tag.getList("KnownRecipes", Tag.TAG_COMPOUND);
        for (int i = 0; i < recipeList.size(); i++) {
            CompoundTag recipeTag = recipeList.getCompound(i);
            knownRecipes.add(recipeTag.getString("id"));
        }
    }

}
