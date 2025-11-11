package com.blacksnow1002.realmmod.system.profession.reforge.capability;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class ProfessionReforgeData implements IProfessionReforgeData {
    private int rank = 0; // 0=未入門, 1~9品
    private int exp = 0;
    private final int[] requiredExp = {0, 984100, 328000, 109300, 36400, 12100, 4000, 1300, 400, 100};

    public record ReforgeQualityRate(float mortal, float mystic, float earth, float heaven) {};
    private Map<String, ReforgeQualityRate> qualityRate = new HashMap<>();

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
    public ReforgeQualityRate getQualityRateBonus(String artifactId) {
        return qualityRate.getOrDefault(artifactId, new ReforgeQualityRate(0, 0, 0, 0));
    }

    @Override
    public void setQualityRateBonus(String artifactId, ReforgeQualityRate rate) {
        qualityRate.put(artifactId, rate);
    }

    @Override
    public void addQualityRateBonus(String artifactId, ReforgeQualityRate rate) {
        ReforgeQualityRate successRate = getQualityRateBonus(artifactId);
        setQualityRateBonus(artifactId, new ReforgeQualityRate(
                successRate.mortal + rate.mortal,
                successRate.mystic + rate.mystic,
                successRate.earth + rate.earth,
                successRate.heaven + rate.heaven));
    }

    @Override
    public Set<String> getAllKnownRecipes() {
        return knownRecipes;
    }

    @Override
    public boolean hasKnownRecipe(String artifactId) {
        return knownRecipes.contains(artifactId);
    }

    @Override
    public void setKnownRecipe(String artifactId) {
        knownRecipes.add(artifactId);
    }


    @Override
    public CompoundTag saveNBTData() {
        CompoundTag tag = new CompoundTag();
        tag.putInt("ReforgeRank", rank);
        tag.putInt("ReforgeExp", exp);

        // 存 qualityRate
        ListTag rateList = new ListTag();
        for (Map.Entry<String, ReforgeQualityRate> entry : qualityRate.entrySet()) {
            CompoundTag rateTag = new CompoundTag();
            rateTag.putString("pillId", entry.getKey());
            ReforgeQualityRate rate = entry.getValue();
            rateTag.putFloat("mortal", rate.mortal());
            rateTag.putFloat("mystic", rate.mystic());
            rateTag.putFloat("earth", rate.earth());
            rateTag.putFloat("heaven", rate.heaven());
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
        this.rank = tag.getInt("ReforgeRank");
        this.exp = tag.getInt("ReforgeExp");

        // 讀 qualityRate
        qualityRate.clear();
        ListTag rateList = tag.getList("QualityRateList", Tag.TAG_COMPOUND);
        for (int i = 0; i < rateList.size(); i++) {
            CompoundTag rateTag = rateList.getCompound(i);
            String pillId = rateTag.getString("pillId");
            ReforgeQualityRate rate = new ReforgeQualityRate(
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
