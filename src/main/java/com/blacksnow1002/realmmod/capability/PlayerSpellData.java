package com.blacksnow1002.realmmod.capability;

import net.minecraft.nbt.CompoundTag;

public class PlayerSpellData implements IPlayerSpellData{
    private boolean lingMuActive = false;

    public boolean isLingMuActive() {
        return lingMuActive;
    }

    public void setLingMuActive(boolean active) {
        this.lingMuActive = active;
    }

    // 儲存到NBT
    public void saveNBTData(CompoundTag tag) {
        tag.putBoolean("LingMuActive", lingMuActive);
    }

    // 從NBT讀取
    public void loadNBTData(CompoundTag tag) {
        lingMuActive = tag.getBoolean("LingMuActive");
    }
}
