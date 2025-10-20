package com.blacksnow1002.realmmod.capability.attribute.equipment;

public interface IEquipmentAttributeData {

    int getEquipmentAttack();
    void setEquipmentAttack(int attackValue);

    int getEquipmentDefense();
    void setEquipmentDefense(int defenseValue);

    int getEquipmentMaxHealth();
    void setEquipmentMaxHealth(int maxHealthValue);

    float getEquipmentMoveSpeed();
    void setEquipmentMoveSpeed(float speedValue);


    float getEquipmentDodgeRate();
    void setEquipmentDodgeRate(float dodgeRate);

    float getEquipmentCritRate();
    void setEquipmentCritRate(float CritRate);

    float getEquipmentCritMagnification();
    void setEquipmentCritMagnification(float critMagnification);


    int getEquipmentMaxMana();
    void setEquipmentMaxMana(int maxManaValue);

}
