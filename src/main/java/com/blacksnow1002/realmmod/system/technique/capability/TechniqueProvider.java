package com.blacksnow1002.realmmod.system.technique.capability;

import com.blacksnow1002.realmmod.core.capability.ModCapabilities;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.common.util.LazyOptional;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class TechniqueProvider implements ICapabilitySerializable<CompoundTag> {
    public static final String IDENTIFIER = "technique_provider";

    private final TechniqueDataManager dataManager = new TechniqueDataManager();
    private final LazyOptional<ITechniqueDataManager> optionalManager = LazyOptional.of(() -> dataManager);
    @Override
    public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
        return cap == ModCapabilities.TECHNIQUE_CAP ? optionalManager.cast() : LazyOptional.empty();
    }

    @Override
    public CompoundTag serializeNBT(HolderLookup.Provider registryAccess) {
        return dataManager.saveNBTData();
    }

    @Override
    public void deserializeNBT(HolderLookup.Provider registryAccess, CompoundTag nbt) {
        dataManager.loadNBTData(nbt);
    }
}
