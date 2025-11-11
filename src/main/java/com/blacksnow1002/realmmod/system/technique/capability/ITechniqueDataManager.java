package com.blacksnow1002.realmmod.system.technique.capability;

import net.minecraft.nbt.CompoundTag;

import java.util.Set;

public interface ITechniqueDataManager {

    // === 資料 ===
    Set<String> getUnlockedTechniques();
    boolean isUnlocked(String techniqueId);
    Set<String> getEquippedTechniques();
    boolean isEquipped(String techniqueId);
    int getTechniqueLevel(String techniqueId);

    // === 功能 ===
    void unlockTechnique(String techniqueId);
    boolean equipTechnique(String techniqueId);
    boolean unequipTechnique(String techniqueId);
    void setTechniqueLevel(String techniqueId, int level);

    // === 儲存 ===
    CompoundTag saveNBTData();
    void loadNBTData(CompoundTag tag);
}