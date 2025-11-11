package com.blacksnow1002.realmmod.system.battle.attribute.capability.source.allocate;

import net.minecraft.nbt.CompoundTag;

public class AllocateAttributeData implements IAllocateAttributeData {
    private int spiritualSense = 0;
    private int physique = 0;
    private int movementTechnique = 0;
    private int fortune = 0;

    @Override
    public int getSpiritualSense() { return spiritualSense; }
    @Override
    public void setSpiritualSense(int spiritualSenseValue) { spiritualSense = spiritualSenseValue; }

    @Override
    public int getPhysique() { return physique; }
    @Override
    public void setPhysique(int physiqueValue) {  physique = physiqueValue; }

    @Override
    public int getMovementTechnique() { return movementTechnique; }
    @Override
    public void setMovementTechnique(int movementTechniqueValue) { movementTechnique = movementTechniqueValue; }

    @Override
    public int getFortune() { return fortune; }
    @Override
    public void setFortune(int fortuneValue) { fortune = fortuneValue; }

    @Override
    public CompoundTag saveNBTData() {
        CompoundTag nbt = new CompoundTag();
        nbt.putInt("SpiritualSense", spiritualSense);
        nbt.putInt("Physique", physique);
        nbt.putInt("MovementTechnique", movementTechnique);
        nbt.putInt("Fortune", fortune);
        return nbt;
    }

    @Override
    public void loadNBTData(CompoundTag nbt) {
        if (nbt.contains("SpiritualSense")) spiritualSense = nbt.getInt("SpiritualSense");
        if (nbt.contains("Physique")) physique = nbt.getInt("Physique");
        if (nbt.contains("MovementTechnique")) movementTechnique = nbt.getInt("MovementTechnique");
        if (nbt.contains("Fortune")) fortune = nbt.getInt("Fortune");
    }
}
