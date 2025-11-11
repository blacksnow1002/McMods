package com.blacksnow1002.realmmod.player.age.capability;

import net.minecraft.nbt.CompoundTag;

public class AgeData implements IAgeData {
    private int currentAge = 0;       // 玩家年齡
    private int realmAge = 0;
    private int ultraAge = 0;

    @Override
    public int getCurrentAge() { return currentAge; }
    @Override
    public void setCurrentAge(int age) { this.currentAge = age; }
    @Override
    public void addCurrentAge(int year) { this.currentAge += year; }

    @Override
    public int getRealmAge() { return realmAge; }
    @Override
    public void setRealmAge(int age) { this.realmAge = age; }

    @Override
    public int getUltraAge() { return ultraAge; }
    @Override
    public void setUltraAge(int age) { this.ultraAge = age; }
    @Override
    public void addUltraAge(int year) { this.ultraAge += year; }

    @Override
    public int getMaxAge() { return realmAge + ultraAge; }

    @Override
    public CompoundTag saveNBTData() {
        CompoundTag nbt = new CompoundTag();
        nbt.putInt("CurrentAge", currentAge);
        nbt.putInt("RealmAge", realmAge);
        nbt.putInt("UltraAge", ultraAge);
        return nbt;
    }

    @Override
    public void loadNBTData(CompoundTag nbt) {
        if (nbt.contains("CurrentAge")) currentAge = nbt.getInt("CurrentAge");
        if (nbt.contains("RealmAge")) realmAge = nbt.getInt("RealmAge");
        if (nbt.contains("UltraAge")) ultraAge = nbt.getInt("UltraAge");
    }
}
