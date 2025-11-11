package com.blacksnow1002.realmmod.system.battle.attribute.capability.source.technique;

import net.minecraft.nbt.CompoundTag;

public class TechniqueAttributeData implements ITechniqueAttributeData {
    private int techniqueAttack = 0;
    private int techniqueDefense = 0;
    private int techniqueMaxHealth = 0;
    private float techniqueMoveSpeed = 0;

    private float techniqueDodgeRate = 0.f;
    private float techniqueCritRate = 0.f;
    private float techniqueCritMagnification = 0.f;

    private int techniqueMaxMana = 0;


    @Override
    public int getTechniqueAttack() { return techniqueAttack; }
    @Override
    public void setTechniqueAttack(int attackValue) { this.techniqueAttack = attackValue; }

    @Override
    public int getTechniqueDefense() { return techniqueDefense; }
    @Override
    public void setTechniqueDefense(int defenseValue) { this.techniqueDefense = defenseValue; }

    @Override
    public int getTechniqueMaxHealth() { return techniqueMaxHealth; }
    @Override
    public void setTechniqueMaxHealth(int maxHealthValue) {  this.techniqueMaxHealth = maxHealthValue; }

    @Override
    public float getTechniqueMoveSpeed() { return techniqueMoveSpeed; }
    @Override
    public void setTechniqueMoveSpeed(float speedValue) { this.techniqueMoveSpeed = speedValue; }


    @Override
    public float getTechniqueDodgeRate() { return techniqueDodgeRate; }
    @Override
    public void setTechniqueDodgeRate(float dodgeRate) {  this.techniqueDodgeRate = dodgeRate; }

    @Override
    public float getTechniqueCritRate() { return techniqueCritRate; }
    @Override
    public void setTechniqueCritRate(float CritRate) { this.techniqueCritRate = CritRate; }

    @Override
    public float getTechniqueCritMagnification() { return techniqueCritMagnification; }
    @Override
    public void setTechniqueCritMagnification(float critMagnification) { this.techniqueCritMagnification = critMagnification; }


    @Override
    public int getTechniqueMaxMana() { return techniqueMaxMana; }
    @Override
    public void setTechniqueMaxMana(int maxManaValue) { this.techniqueMaxMana = maxManaValue; }

    @Override
    public CompoundTag saveNBTData() {
        CompoundTag nbt = new CompoundTag();
        nbt.putInt("TechniqueAttack", techniqueAttack);
        nbt.putInt("TechniqueDefense", techniqueDefense);
        nbt.putInt("TechniqueMaxHealth", techniqueMaxHealth);
        nbt.putFloat("TechniqueMoveSpeed", techniqueMoveSpeed);
        nbt.putFloat("TechniqueDodgeRate", techniqueDodgeRate);
        nbt.putFloat("TechniqueCritRate", techniqueCritRate);
        nbt.putFloat("TechniqueCritMagnification", techniqueCritMagnification);
        nbt.putInt("TechniqueMaxMana", techniqueMaxMana);
        return nbt;
    }

    @Override
    public void loadNBTData(CompoundTag nbt) {
        if (nbt.contains("TechniqueAttack")) techniqueAttack = nbt.getInt("TechniqueAttack");
        if (nbt.contains("TechniqueDefense")) techniqueDefense = nbt.getInt("TechniqueDefense");
        if (nbt.contains("TechniqueMaxHealth")) techniqueMaxHealth = nbt.getInt("TechniqueMaxHealth");
        if (nbt.contains("TechniqueMoveSpeed")) techniqueMoveSpeed = nbt.getFloat("TechniqueMoveSpeed");
        if (nbt.contains("TechniqueDodgeRate")) techniqueDodgeRate = nbt.getFloat("TechniqueDodgeRate");
        if (nbt.contains("TechniqueCritRate")) techniqueCritRate = nbt.getFloat("TechniqueCritRate");
        if (nbt.contains("TechniqueCritMagnification")) techniqueCritMagnification = nbt.getFloat("TechniqueCritMagnification");
        if (nbt.contains("TechniqueMaxMana")) techniqueMaxMana = nbt.getInt("TechniqueMaxMana");

    }
}
