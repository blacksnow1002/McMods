package com.blacksnow1002.realmmod.system.battle.attribute.capability.source.equipment;

import net.minecraft.nbt.CompoundTag;

public class EquipmentAttributeData implements IEquipmentAttributeData {
    private int equipmentAttack = 0;
    private int equipmentDefense = 0;
    private int equipmentMaxHealth = 0;
    private float equipmentMoveSpeed = 0;

    private float equipmentDodgeRate = 0.f;
    private float equipmentCritRate = 0.f;
    private float equipmentCritMagnification = 0.f;

    private int equipmentMaxMana = 0;


    @Override
    public int getEquipmentAttack() { return equipmentAttack; }
    @Override
    public void setEquipmentAttack(int attackValue) { this.equipmentAttack = attackValue; }

    @Override
    public int getEquipmentDefense() { return equipmentDefense; }
    @Override
    public void setEquipmentDefense(int defenseValue) { this.equipmentDefense = defenseValue; }

    @Override
    public int getEquipmentMaxHealth() { return equipmentMaxHealth; }
    @Override
    public void setEquipmentMaxHealth(int maxHealthValue) {  this.equipmentMaxHealth = maxHealthValue; }

    @Override
    public float getEquipmentMoveSpeed() { return equipmentMoveSpeed; }
    @Override
    public void setEquipmentMoveSpeed(float speedValue) { this.equipmentMoveSpeed = speedValue; }


    @Override
    public float getEquipmentDodgeRate() { return equipmentDodgeRate; }
    @Override
    public void setEquipmentDodgeRate(float dodgeRate) {  this.equipmentDodgeRate = dodgeRate; }

    @Override
    public float getEquipmentCritRate() { return equipmentCritRate; }
    @Override
    public void setEquipmentCritRate(float CritRate) { this.equipmentCritRate = CritRate; }

    @Override
    public float getEquipmentCritMagnification() { return equipmentCritMagnification; }
    @Override
    public void setEquipmentCritMagnification(float critMagnification) { this.equipmentCritMagnification = critMagnification; }


    @Override
    public int getEquipmentMaxMana() { return equipmentMaxMana; }
    @Override
    public void setEquipmentMaxMana(int maxManaValue) { this.equipmentMaxMana = maxManaValue; }

    @Override
    public CompoundTag saveNBTData() {
        CompoundTag nbt = new CompoundTag();
        nbt.putInt("EquipmentAttack", equipmentAttack);
        nbt.putInt("EquipmentDefense", equipmentDefense);
        nbt.putInt("EquipmentMaxHealth", equipmentMaxHealth);
        nbt.putFloat("EquipmentMoveSpeed", equipmentMoveSpeed);
        nbt.putFloat("EquipmentDodgeRate", equipmentDodgeRate);
        nbt.putFloat("EquipmentCritRate", equipmentCritRate);
        nbt.putFloat("EquipmentCritMagnification", equipmentCritMagnification);
        nbt.putInt("EquipmentMaxMana", equipmentMaxMana);
        return nbt;
    }

    @Override
    public void loadNBTData(CompoundTag nbt) {
        if (nbt.contains("EquipmentAttack")) equipmentAttack = nbt.getInt("EquipmentAttack");
        if (nbt.contains("EquipmentDefense")) equipmentDefense = nbt.getInt("EquipmentDefense");
        if (nbt.contains("EquipmentMaxHealth")) equipmentMaxHealth = nbt.getInt("EquipmentMaxHealth");
        if (nbt.contains("EquipmentMoveSpeed")) equipmentMoveSpeed = nbt.getFloat("EquipmentMoveSpeed");
        if (nbt.contains("EquipmentDodgeRate")) equipmentDodgeRate = nbt.getFloat("EquipmentDodgeRate");
        if (nbt.contains("EquipmentCritRate")) equipmentCritRate = nbt.getFloat("EquipmentCritRate");
        if (nbt.contains("EquipmentCritMagnification")) equipmentCritMagnification = nbt.getFloat("EquipmentCritMagnification");
        if (nbt.contains("EquipmentMaxMana")) equipmentMaxMana = nbt.getInt("EquipmentMaxMana");

    }
}
