package com.blacksnow1002.realmmod.capability;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Player;

public interface ICultivationData {
    CultivationRealm getRealm(); // 境界
    void setRealm(CultivationRealm realm);

    int getLayer();
    void setLayer(int layer);

    float getBreakthroughSuccessPossibility();
    void setBreakthroughSuccessPossibility(float possibility);

    int getCultivation(); // 修為
    void setCultivation(int cultivation);

    void addCultivation(Player player, int  amount);

    void saveNBTData(CompoundTag nbt);
    void loadNBTData(CompoundTag nbt);

    void tryBreakthrough(Player player);
}
