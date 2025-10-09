package com.blacksnow1002.realmmod.capability.magic_point;

import com.blacksnow1002.realmmod.capability.world.IWorldData;
import net.minecraft.nbt.CompoundTag;

public class MagicPointData implements IMagicPointData {
    private int magicPointNow = 0;
    private int magicPointMax = 0;

    @Override
    public int getMagicPointNow() { return magicPointNow; }
    @Override
    public void setMagicPointNow(int magicPointNow) { this.magicPointNow = magicPointNow; }

    @Override
    public int getMagicPointMax() { return magicPointMax; }
    @Override
    public void setMagicPointMax(int magicPointMax) { this.magicPointMax = magicPointMax; }

    @Override
    public int addMagicPoint(int addPoint) { return Math.min(magicPointNow + addPoint, magicPointMax); }
    @Override
    public int subtractMagicPoint(int subtractPoint) { return Math.max(magicPointNow - subtractPoint, 0); }


    public void saveNBTData(CompoundTag nbt) {
        nbt.putInt("MagicPointNow", magicPointNow);
        nbt.putInt("MagicPointMax", magicPointMax);
    }

    public void loadNBTData(CompoundTag nbt) {
        if (nbt.contains("MagicPointNow")) magicPointNow = nbt.getInt("MagicPointNow");
        if (nbt.contains("MagicPointMax")) magicPointMax = nbt.getInt("MagicPointMax");
    }
}
