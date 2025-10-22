package com.blacksnow1002.realmmod.capability.mana;

import com.blacksnow1002.realmmod.capability.ModCapabilities;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Player;

public class ManaData implements IManaData{
    int maxMana = 0;
    int receiveMana = 0;
    int currentMana = 0;

    @Override
    public void getManaFromPlayerAttribute(Player player) {
        player.getCapability(ModCapabilities.PLAYER_TOTAL_ATTRIBUTE_CAP).ifPresent(cap -> {
            maxMana = cap.getPlayerTotalMaxMana();
            receiveMana = cap.getPlayerTotalManaReceive();

            if (currentMana > maxMana) { currentMana =  maxMana; }
        });
    }

    @Override
    public int getMana() { return currentMana; }

    @Override
    public void setMana(int value) { currentMana = value; }

    @Override
    public void addMana(float value) {
        currentMana = (int) Math.min(currentMana + value, maxMana);
    }

    @Override
    public void subtractMana(float value) {
        currentMana = (int) Math.max(currentMana - value, 0);
    }

    @Override
    public CompoundTag saveNBTData() {
        CompoundTag nbt = new CompoundTag();
        nbt.putInt("playerCurrentMana", currentMana);
        return nbt;
    }

    @Override
    public void loadNBTData(CompoundTag nbt) {
        if (nbt.contains("playerCurrentMana")) currentMana = nbt.getInt("playerCurrentMana");
    }
}
