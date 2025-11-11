package com.blacksnow1002.realmmod.system.battle.attribute.capability.source.technique;

import net.minecraft.nbt.CompoundTag;

public interface ITechniqueAttributeData {

    int getTechniqueAttack();
    void setTechniqueAttack(int attackValue);

    int getTechniqueDefense();
    void setTechniqueDefense(int defenseValue);

    int getTechniqueMaxHealth();
    void setTechniqueMaxHealth(int maxHealthValue);

    float getTechniqueMoveSpeed();
    void setTechniqueMoveSpeed(float speedValue);


    float getTechniqueDodgeRate();
    void setTechniqueDodgeRate(float dodgeRate);

    float getTechniqueCritRate();
    void setTechniqueCritRate(float CritRate);

    float getTechniqueCritMagnification();
    void setTechniqueCritMagnification(float critMagnification);

    int getTechniqueMaxMana();
    void setTechniqueMaxMana(int maxManaValue);

    CompoundTag saveNBTData();
    void loadNBTData(CompoundTag nbt);

}
