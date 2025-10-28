package com.blacksnow1002.realmmod.profession.common;

import net.minecraft.nbt.CompoundTag;

public interface IProfessionHeartDemonData {
    boolean isHeartDemon();
    void setHeartDemon(boolean state);

    CompoundTag saveNBTData();
    void loadNBTData(CompoundTag tag);
}
