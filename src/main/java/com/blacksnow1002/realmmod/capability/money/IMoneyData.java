package com.blacksnow1002.realmmod.capability.money;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Player;

public interface IMoneyData {
    int getMoney();
    void setMoney(int value);

    void addMoney(int value);

    void subtractMoney(int value);

    CompoundTag saveNBTData();
    void loadNBTData(CompoundTag nbt);
}
