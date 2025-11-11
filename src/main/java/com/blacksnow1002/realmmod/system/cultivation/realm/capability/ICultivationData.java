package com.blacksnow1002.realmmod.system.cultivation.realm.capability;

import com.blacksnow1002.realmmod.system.cultivation.CultivationRealm;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.common.capabilities.AutoRegisterCapability;

@AutoRegisterCapability
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

    void tryBreakthrough(Player player);

    CompoundTag saveNBTData();
    void loadNBTData(CompoundTag nbt);
}
