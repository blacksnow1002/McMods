package com.blacksnow1002.realmmod.profession;

import net.minecraft.nbt.CompoundTag;

public class ProfessionData implements IProfessionData {
    // 等級
    private int harvestLevel = 0;
    private int cultivationLevel = 0;
    private int miningLevel = 0;
    private int alchemyLevel = 0;
    private int reforgingLevel = 0;
    private int inscriptionLevel = 0;
    private int arrayCraftingLevel = 0;

    // 經驗
    private int harvestExp = 0;
    private int cultivationExp = 0;
    private int miningExp = 0;
    private int alchemyExp = 0;
    private int reforgingExp = 0;
    private int inscriptionExp = 0;
    private int arrayCraftingExp = 0;

    // 採集
    @Override
    public int getHarvestLevel() {
        return harvestLevel;
    }
    @Override
    public void setHarvestLevel(int level) {
        this.harvestLevel = level;
    }

    @Override
    public int getHarvestExp() {
        return harvestExp;
    }
    @Override
    public void setHarvestExp(int exp) {
        this.harvestExp = exp;
    }
    @Override
    public void addHarvestExp(int exp) {
        this.harvestExp += exp;
    }
    @Override
    public void subtractHarvestExp(int exp) {
        this.harvestExp -= exp;
    }


    // 種植
    @Override
    public int getCultivationLevel() {
        return cultivationLevel;
    }
    @Override
    public void setCultivationLevel(int level) {
        this.cultivationLevel = level;
    }

    @Override
    public int getCultivationExp() {
        return cultivationExp;
    }
    @Override
    public void setCultivationExp(int exp) {
        this.cultivationExp = exp;
    }
    @Override
    public void addCultivationExp(int exp) {
        this.cultivationExp += exp;
    }
    @Override
    public void subtractCultivationExp(int exp) {
        this.cultivationExp -= exp;
    }


    // 挖礦
    @Override
    public int getMiningLevel() {
        return miningLevel;
    }
    @Override
    public void setMiningLevel(int level) {
        this.miningLevel = level;
    }

    @Override
    public int getMiningExp() {
        return miningExp;
    }
    @Override
    public void setMiningExp(int exp) {
        this.miningExp = exp;
    }
    @Override
    public void addMiningExp(int exp) {
        this.miningExp += exp;
    }
    @Override
    public void subtractMiningExp(int exp) {
        this.miningExp -= exp;
    }


    //煉丹
    @Override
    public int getAlchemyLevel() {
        return alchemyLevel;
    }
    @Override
    public void setAlchemyLevel(int level) {
        this.alchemyLevel = level;
    }

    @Override
    public int getAlchemyExp() {
        return alchemyExp;
    }
    @Override
    public void setAlchemyExp(int exp) {
        this.alchemyExp = exp;
    }
    @Override
    public void addAlchemyExp(int exp) {
        this.alchemyExp += exp;
    }
    @Override
    public void subtractAlchemyExp(int exp) {
        this.alchemyExp -= exp;
    }


    // 煉器
    @Override
    public int getReforgingLevel() {
        return reforgingLevel;
    }
    @Override
    public void setReforgingLevel(int level) {
        this.reforgingLevel = level;
    }

    @Override
    public int getReforgingExp() {
        return reforgingExp;
    }
    @Override
    public void setReforgingExp(int exp) {
        this.reforgingExp = exp;
    }
    @Override
    public void addReforgingExp(int exp) {
        this.reforgingExp += exp;
    }
    @Override
    public void subtractReforgingExp(int exp) {
        this.reforgingExp -= exp;
    }


    // 畫符
    @Override
    public int getInscriptionLevel() {
        return inscriptionLevel;
    }
    @Override
    public void setInscriptionLevel(int level) {
        this.inscriptionLevel = level;
    }

    @Override
    public int getInscriptionExp() {
        return inscriptionExp;
    }
    @Override
    public void setInscriptionExp(int exp) {
        this.inscriptionExp = exp;
    }
    @Override
    public void addInscriptionExp(int exp) {
        this.inscriptionExp += exp;
    }
    @Override
    public void subtractInscriptionExp(int exp) {
        this.inscriptionExp -= exp;
    }


    // 陣法
    @Override
    public int getArrayCraftingLevel() {
        return arrayCraftingLevel;
    }
    @Override
    public void setArrayCraftingLevel(int level) {
        this.arrayCraftingLevel = level;
    }

    @Override
    public int getArrayCraftingExp() {
        return arrayCraftingExp;
    }
    @Override
    public void setArrayCraftingExp(int exp) {
        this.arrayCraftingExp = exp;
    }
    @Override
    public void addArrayCraftingExp(int exp) {
        this.arrayCraftingExp += exp;
    }
    @Override
    public void subtractArrayCraftingExp(int exp) {
        this.arrayCraftingExp -= exp;
    }

    @Override
    public CompoundTag saveNBTData() {
        CompoundTag nbt = new CompoundTag();
        nbt.putInt("harvestLevel", harvestLevel);
        nbt.putInt("cultivationLevel", cultivationLevel);
        nbt.putInt("miningLevel", miningLevel);
        nbt.putInt("alchemyLevel", alchemyLevel);
        nbt.putInt("reforgingLevel", reforgingLevel);
        nbt.putInt("inscriptionLevel", inscriptionLevel);
        nbt.putInt("arrayCraftingLevel", arrayCraftingLevel);

        nbt.putInt("harvestExp", harvestExp);
        nbt.putInt("cultivationExp", cultivationExp);
        nbt.putInt("miningExp", miningExp);
        nbt.putInt("alchemyExp", alchemyExp);
        nbt.putInt("reforgingExp", reforgingExp);
        nbt.putInt("inscriptionExp", inscriptionExp);
        nbt.putInt("arrayCraftingExp", arrayCraftingExp);
        return nbt;
    }

    @Override
    public void loadNBTData(CompoundTag nbt) {
        if (nbt.contains("harvestLevel")) { harvestLevel = nbt.getInt("harvestLevel"); }
        if (nbt.contains("cultivationLevel")) { cultivationLevel = nbt.getInt("cultivationLevel"); }
        if (nbt.contains("miningLevel")) { miningLevel = nbt.getInt("miningLevel"); }
        if (nbt.contains("alchemyLevel")) { alchemyLevel = nbt.getInt("alchemyLevel"); }
        if (nbt.contains("reforgingLevel")) { reforgingLevel = nbt.getInt("reforgingLevel"); }
        if (nbt.contains("inscriptionLevel")) { inscriptionLevel = nbt.getInt("inscriptionLevel"); }
        if (nbt.contains("arrayCraftingLevel")) { arrayCraftingLevel = nbt.getInt("arrayCraftingLevel"); }

        if (nbt.contains("harvestExp")) { harvestExp = nbt.getInt("harvestExp"); }
        if (nbt.contains("cultivationExp")) { cultivationExp = nbt.getInt("cultivationExp"); }
        if (nbt.contains("miningExp")) { miningExp = nbt.getInt("miningExp"); }
        if (nbt.contains("alchemyExp")) { alchemyExp = nbt.getInt("alchemyExp"); }
        if (nbt.contains("reforgingExp")) { reforgingExp = nbt.getInt("reforgingExp"); }
        if (nbt.contains("inscriptionExp")) { inscriptionExp = nbt.getInt("inscriptionExp"); }
        if (nbt.contains("arrayCraftingExp")) { arrayCraftingExp = nbt.getInt("arrayCraftingExp"); }
    }
}