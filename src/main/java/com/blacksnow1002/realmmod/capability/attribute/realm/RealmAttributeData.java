package com.blacksnow1002.realmmod.capability.attribute.realm;

import net.minecraft.nbt.CompoundTag;

public class RealmAttributeData implements IRealmAttributeData {
    private int realmAttack = 0;
    private int realmDefense = 0;
    private int realmMaxHealth = 0;
    private float realmMoveSpeed = 0;

    private float realmDodgeRate = 0.f;
    private float realmCritRate = 0.f;
    private float realmCritMagnification = 0.f;

    private int realmMaxMana = 0;


    @Override
    public int getRealmAttack() { return realmAttack; }
    @Override
    public void setRealmAttack(int attackValue) { this.realmAttack = attackValue; }

    @Override
    public int getRealmDefense() { return realmDefense; }
    @Override
    public void setRealmDefense(int defenseValue) { this.realmDefense = defenseValue; }

    @Override
    public int getRealmMaxHealth() { return realmMaxHealth; }
    @Override
    public void setRealmMaxHealth(int maxHealthValue) {  this.realmMaxHealth = maxHealthValue; }

    @Override
    public float getRealmMoveSpeed() { return realmMoveSpeed; }
    @Override
    public void setRealmMoveSpeed(float speedValue) { this.realmMoveSpeed = speedValue; }



    @Override
    public float getRealmDodgeRate() { return realmDodgeRate; }
    @Override
    public void setRealmDodgeRate(float dodgeRate) {  this.realmDodgeRate = dodgeRate; }

    @Override
    public float getRealmCritRate() { return realmCritRate; }
    @Override
    public void setRealmCritRate(float CritRate) { this.realmCritRate = CritRate; }

    @Override
    public float getRealmCritMagnification() { return realmCritMagnification; }
    @Override
    public void setRealmCritMagnification(float critMagnification) { this.realmCritMagnification = critMagnification; }




    @Override
    public int getRealmMaxMana() { return realmMaxMana; }
    @Override
    public void setRealmMaxMana(int maxManaValue) { this.realmMaxMana = maxManaValue; }

    @Override
    public CompoundTag saveNBTData() {
        CompoundTag nbt = new CompoundTag();
        nbt.putInt("RealmAttack", realmAttack);
        nbt.putInt("RealmDefense", realmDefense);
        nbt.putInt("RealmMaxHealth", realmMaxHealth);
        nbt.putFloat("RealmMoveSpeed", realmMoveSpeed);
        nbt.putFloat("RealmDodgeRate", realmDodgeRate);
        nbt.putFloat("RealmCritRate", realmCritRate);
        nbt.putFloat("RealmCritMagnification", realmCritMagnification);
        nbt.putInt("RealmMaxMana", realmMaxMana);
        return nbt;
    }

    @Override
    public void loadNBTData(CompoundTag nbt) {
        if (nbt.contains("RealmAttack")) realmAttack = nbt.getInt("RealmAttack");
        if (nbt.contains("RealmDefense")) realmDefense = nbt.getInt("RealmDefense");
        if (nbt.contains("RealmMaxHealth")) realmMaxHealth = nbt.getInt("RealmMaxHealth");
        if (nbt.contains("RealmMoveSpeed")) realmMoveSpeed = nbt.getFloat("RealmMoveSpeed");
        if (nbt.contains("RealmDodgeRate")) realmDodgeRate = nbt.getFloat("RealmDodgeRate");
        if (nbt.contains("RealmCritRate")) realmCritRate = nbt.getFloat("RealmCritRate");
        if (nbt.contains("RealmCritMagnification")) realmCritMagnification = nbt.getFloat("RealmCritMagnification");
        if (nbt.contains("RealmMaxMana")) realmMaxMana = nbt.getInt("RealmMaxMana");

    }
}
