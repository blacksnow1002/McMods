package com.blacksnow1002.realmmod.system.battle.attribute.capability.source.realm;

import net.minecraft.nbt.CompoundTag;

public interface IRealmAttributeData {

    int getRealmAttack();
    void setRealmAttack(int attackValue);

    int getRealmDefense();
    void setRealmDefense(int defenseValue);

    int getRealmMaxHealth();
    void setRealmMaxHealth(int maxHealthValue);

    float getRealmMoveSpeed();
    void setRealmMoveSpeed(float speedValue);


    float getRealmDodgeRate();
    void setRealmDodgeRate(float dodgeRate);

    float getRealmCritRate();
    void setRealmCritRate(float CritRate);

    float getRealmCritMagnification();
    void setRealmCritMagnification(float critMagnification);


    int getRealmMaxMana();
    void setRealmMaxMana(int maxManaValue);

    CompoundTag saveNBTData();
    void loadNBTData(CompoundTag nbt);

}
