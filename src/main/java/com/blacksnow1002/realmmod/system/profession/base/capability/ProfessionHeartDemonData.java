package com.blacksnow1002.realmmod.system.profession.base.capability;

import net.minecraft.nbt.CompoundTag;

public class ProfessionHeartDemonData implements IProfessionHeartDemonData {
    private boolean heartDemon = false;

    @Override
    public boolean isHeartDemon() {
        return heartDemon;
    }

    @Override
    public void setHeartDemon(boolean state) {
        heartDemon = state;
    }

    @Override
    public CompoundTag saveNBTData() {
        CompoundTag tag = new CompoundTag();
        tag.putBoolean("ProfessionHeartDemon", heartDemon);
        return tag;
    }

    @Override
    public void loadNBTData(CompoundTag tag) {
        if (tag.contains("ProfessionHeartDemon")) heartDemon = tag.getBoolean("ProfessionHeartDemon");
    }
}
