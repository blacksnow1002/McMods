package com.blacksnow1002.realmmod.system.cultivation.breakthrough.capability;

import com.blacksnow1002.realmmod.common.capability.ModCapabilities;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.common.util.LazyOptional;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class RealmBreakthroughProvider implements ICapabilitySerializable<CompoundTag> {
    public static final String IDENTIFIER = "breakthrough_capability";

    private final RealmBreakthroughData backend = new RealmBreakthroughData();
    private final LazyOptional<IRealmBreakthroughData> optional = LazyOptional.of(() -> backend);

    @Override
    public <T> @NotNull LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
        return cap == ModCapabilities.BREAKTHROUGH_CAPABILITY_CAP ? optional.cast() : LazyOptional.empty();
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
