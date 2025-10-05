package com.blacksnow1002.realmmod.capability;

import net.minecraft.nbt.CompoundTag;

public interface IPlayerSpellData {

    boolean isLingMuActive();

    void setLingMuActive(boolean active);

    // 儲存到NBT
    void saveNBTData(CompoundTag tag);

    // 從NBT讀取
    public void loadNBTData(CompoundTag tag);
}
