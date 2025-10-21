package com.blacksnow1002.realmmod.title;

import net.minecraft.nbt.CompoundTag;

import java.util.Set;

public interface ITitleDataManager {
    Set<String> getHaveTitles();
    boolean hasTitle(String title);
    String getEquipTitle();


    void unlockTitle(String titleId);
    void equipTitle(String titleId);
    void unequipTitle();

    CompoundTag saveNBTData();
    void loadNBTData(CompoundTag tag);
}
