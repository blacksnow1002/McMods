package com.blacksnow1002.realmmod.capability.age;

import net.minecraft.nbt.CompoundTag;

/**
 * 玩家年齡與境界年齡資料介面
 */
public interface IAgeData {

    // -------------------- 當前年齡 --------------------
    int getCurrentAge();           // 取得玩家當前年齡
    void setCurrentAge(int age);   // 設置玩家當前年齡
    void addCurrentAge(int years); // 增加當前年齡

    // -------------------- 境界年齡 --------------------
    int getRealmAge();           // 取得玩家境界年齡
    void setRealmAge(int age);   // 設置玩家境界年齡

    // -------------------- 超凡年齡 --------------------
    int getUltraAge();           // 取得超凡年齡
    void setUltraAge(int age);   // 設置超凡年齡
    void addUltraAge(int years); // 增加超凡年齡

    int getMaxAge();

    CompoundTag saveNBTData();
    void loadNBTData(CompoundTag nbt);
}
