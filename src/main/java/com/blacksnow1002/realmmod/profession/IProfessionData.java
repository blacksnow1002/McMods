package com.blacksnow1002.realmmod.profession;

import net.minecraft.nbt.CompoundTag;

public interface IProfessionData {

    // 採集
    int getHarvestLevel();
    void setHarvestLevel(int level);

    int getHarvestExp();
    void setHarvestExp(int exp);
    void addHarvestExp(int exp);
    void subtractHarvestExp(int exp);

    // 種植
    int getCultivationLevel();
    void setCultivationLevel(int level);

    int getCultivationExp();
    void setCultivationExp(int exp);
    void addCultivationExp(int exp);
    void subtractCultivationExp(int exp);

    // 挖礦
    int getMiningLevel();
    void setMiningLevel(int level);

    int getMiningExp();
    void setMiningExp(int exp);
    void addMiningExp(int exp);
    void subtractMiningExp(int exp);

    // 煉丹
    int getAlchemyLevel();
    void setAlchemyLevel(int level);

    int getAlchemyExp();
    void setAlchemyExp(int exp);
    void addAlchemyExp(int exp);
    void subtractAlchemyExp(int exp);

    // 煉器
    int getReforgingLevel();
    void setReforgingLevel(int level);

    int getReforgingExp();
    void setReforgingExp(int exp);
    void addReforgingExp(int exp);
    void subtractReforgingExp(int exp);

    // 畫符
    int getInscriptionLevel();
    void setInscriptionLevel(int level);

    int getInscriptionExp();
    void setInscriptionExp(int exp);
    void addInscriptionExp(int exp);
    void subtractInscriptionExp(int exp);

    // 陣法製作
    int getArrayCraftingLevel();
    void setArrayCraftingLevel(int level);

    int getArrayCraftingExp();
    void setArrayCraftingExp(int exp);
    void addArrayCraftingExp(int exp);
    void subtractArrayCraftingExp(int exp);


    CompoundTag saveNBTData();
    void loadNBTData(CompoundTag nbt);


}