package com.blacksnow1002.realmmod.system.world.capability.world;

import net.minecraft.nbt.CompoundTag;

public class WorldData implements IWorldData {
    private int year = 0;       // 第幾年
    private int season = 0;    // 0=春,1=夏,2=秋,3=冬
    private long worldTime = 0;

    @Override
    public int getYear() { return year; }
    @Override
    public void setYear(int year) { this.year = year; }

    @Override
    public int getSeason() { return season; }
    @Override
    public void setSeason(int season) { this.season = season; }

    @Override
    public long getWorldTime() { return worldTime; }
    @Override
    public void setWorldTime(long worldTime) { this.worldTime = worldTime; }


    public void saveNBTData(CompoundTag nbt) {
        nbt.putInt("Year", year);
        nbt.putInt("Season", season);
        nbt.putLong("WorldTime", worldTime);
    }

    public void loadNBTData(CompoundTag nbt) {
        if (nbt.contains("Year")) year = nbt.getInt("Year");
        if (nbt.contains("Season")) season = nbt.getInt("Season");
        if (nbt.contains("WorldTime")) worldTime = nbt.getLong("WorldTime");
    }
}
