package com.blacksnow1002.realmmod.system.economy.money.capability;

import net.minecraft.nbt.CompoundTag;

public interface IMoneyData {
    int getMoney();
    void setMoney(int value);

    void addMoney(int value);

    void subtractMoney(int value);

    CompoundTag saveNBTData();
    void loadNBTData(CompoundTag nbt);
}
