package com.blacksnow1002.realmmod.capability.attribute.allocate;

import net.minecraft.nbt.CompoundTag;

public interface IAllocateAttributeData {

    //神念 神通上限、真元上限、真元回復速度
    int getSpiritualSense();
    void setSpiritualSense(int spiritualSenseValue);

    //根骨 生命上限、回血速度、防禦
    int getPhysique();
    void setPhysique(int physiqueValue);

    //身法 移動速度、閃避率、攻擊速度
    int getMovementTechnique();
    void setMovementTechnique(int movementTechniqueValue);

    //氣運 爆擊率、採集倍率?、奇遇機率?、掉落品質
    int getFortune();
    void setFortune(int fortuneValue);

    CompoundTag saveNBTData();
    void loadNBTData(CompoundTag nbt);
}
