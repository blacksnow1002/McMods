package com.blacksnow1002.realmmod.world.capability.world;

public interface IWorldData {
    int getSeason();
    void setSeason(int season);

    int getYear();
    void setYear(int year);

    long getWorldTime();
    void setWorldTime(long worldTime);
}
