package com.blacksnow1002.realmmod.capability.spiritroot;

import net.minecraft.nbt.CompoundTag;

public class SpiritRootData implements ISpiritRootData{
    private int goldRootLevel = 0;
    private int woodRootLevel = 0;
    private int waterRootLevel = 0;
    private int fireRootLevel = 0;
    private int earthRootLevel = 0;

    @Override
    public int getGoldRootLevel() { return goldRootLevel; }
    @Override
    public void setGoldRootLevel(int goldRootLevel) { this.goldRootLevel = goldRootLevel; }

    @Override
    public int getWoodRootLevel() { return woodRootLevel; }
    @Override
    public void setWoodRootLevel(int woodRootLevel) { this.woodRootLevel = woodRootLevel; }

    @Override
    public int getWaterRootLevel() { return waterRootLevel; }
    @Override
    public void setWaterRootLevel(int waterRootLevel) { this.waterRootLevel = waterRootLevel; }

    @Override
    public int getFireRootLevel() { return fireRootLevel; }
    @Override
    public void setFireRootLevel(int fireRootLevel) { this.fireRootLevel = fireRootLevel; }

    @Override
    public int getEarthRootLevel() { return earthRootLevel; }
    @Override
    public void setEarthRootLevel(int earthRootLevel) { this.earthRootLevel = earthRootLevel; }

    @Override
    public int getSumRootLevel() { return goldRootLevel + woodRootLevel + waterRootLevel + fireRootLevel + earthRootLevel; }

    public void saveNBTData(CompoundTag nbt) {
        nbt.putInt("GoldRootLevel", goldRootLevel);
        nbt.putInt("WoodRootLevel", woodRootLevel);
        nbt.putInt("WaterRootLevel", waterRootLevel);
        nbt.putInt("FireRootLevel", fireRootLevel);
        nbt.putInt("EarthRootLevel", earthRootLevel);

    }

    public void loadNBTData(CompoundTag nbt) {
        if (nbt.contains("GoldRootLevel")) goldRootLevel = nbt.getInt("GoldRootLevel");
        if (nbt.contains("WoodRootLevel")) woodRootLevel = nbt.getInt("WoodRootLevel");
        if (nbt.contains("WaterRootLevel")) waterRootLevel = nbt.getInt("WaterRootLevel");
        if (nbt.contains("FireRootLevel")) fireRootLevel = nbt.getInt("FireRootLevel");
        if (nbt.contains("EarthRootLevel")) earthRootLevel = nbt.getInt("EarthRootLevel");

    }
}
