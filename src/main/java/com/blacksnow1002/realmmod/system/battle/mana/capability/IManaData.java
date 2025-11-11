package com.blacksnow1002.realmmod.system.battle.mana.capability;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Player;

public interface IManaData {
    int getMana();
    void setMana(int value);

    void getManaFromPlayerAttribute(Player player);

    void addMana(float value);

    void subtractMana(float value);

    CompoundTag saveNBTData();
    void loadNBTData(CompoundTag nbt);
}
