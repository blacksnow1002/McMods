package com.blacksnow1002.realmmod.capability.money;

import com.blacksnow1002.realmmod.capability.ModCapabilities;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Player;

public class MoneyData implements IMoneyData {
    int currentMoney = 0;

    @Override
    public int getMoney() { return currentMoney; }

    @Override
    public void setMoney(int value) { currentMoney = value; }

    @Override
    public void addMoney(int value) {
        currentMoney += value;
    }

    @Override
    public void subtractMoney(int value) {
        currentMoney -= value;
    }

    @Override
    public CompoundTag saveNBTData() {
        CompoundTag nbt = new CompoundTag();
        nbt.putInt("playerCurrentMoney", currentMoney);
        return nbt;
    }

    public void loadNBTData(CompoundTag nbt) {
        if (nbt.contains("playerCurrentMoney")) currentMoney = nbt.getInt("playerCurrentMoney");
    }
}
