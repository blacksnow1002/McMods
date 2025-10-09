package com.blacksnow1002.realmmod.capability.magic_point;

public interface IMagicPointData {
    int getMagicPointNow();
    void setMagicPointNow(int magicPointNow);

    int getMagicPointMax();
    void setMagicPointMax(int magicPointMax);

    int addMagicPoint(int addPoint);
    int subtractMagicPoint(int subtractPoint);
}
