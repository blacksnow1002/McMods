package com.blacksnow1002.realmmod.capability.money;

import com.blacksnow1002.realmmod.capability.ModCapabilities;
import com.blacksnow1002.realmmod.capability.mana.IManaData;
import com.blacksnow1002.realmmod.capability.mana.ManaData;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.common.util.LazyOptional;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class MoneyProvider implements ICapabilitySerializable<CompoundTag> {
    public static final String IDENTIFIER = "money";

    private final MoneyData backend = new MoneyData();
    private final LazyOptional<IMoneyData> optional = LazyOptional.of(() -> backend);

    @Override
    public <T> @NotNull LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
        return cap == ModCapabilities.MONEY_CAP ? optional.cast() : LazyOptional.empty();
    }

    @Override
    public CompoundTag serializeNBT(HolderLookup.Provider provider) {
        return backend.saveNBTData();
    }

    @Override
    public void deserializeNBT(HolderLookup.Provider provider, CompoundTag nbt) {
        backend.loadNBTData(nbt);
    }
}
