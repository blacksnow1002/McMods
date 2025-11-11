package com.blacksnow1002.realmmod.system.battle.mana.capability;

import com.blacksnow1002.realmmod.common.capability.ModCapabilities;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.common.util.LazyOptional;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ManaProvider implements ICapabilitySerializable<CompoundTag> {
    public static final String IDENTIFIER = "mana";

    private final ManaData backend = new ManaData();
    private final LazyOptional<IManaData> optional = LazyOptional.of(() -> backend);

    @Override
    public <T> @NotNull LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
        return cap == ModCapabilities.MANA_CAP ? optional.cast() : LazyOptional.empty();
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
