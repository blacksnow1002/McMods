package com.blacksnow1002.realmmod.system.profession.harvest.capability;

import com.blacksnow1002.realmmod.core.capability.ModCapabilities;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.common.util.LazyOptional;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ProfessionHarvestProvider implements ICapabilitySerializable<CompoundTag> {
    public static final String IDENTIFIER = "profession_harvest";

    private final ProfessionHarvestData backend = new ProfessionHarvestData();
    private final LazyOptional<IProfessionHarvestData> optional = LazyOptional.of(() -> backend);

    @Override
    public <T> @NotNull LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
        return cap == ModCapabilities.PROFESSION_HARVEST_CAP ? optional.cast() : LazyOptional.empty();
    }

    // ✅ 新版 API：必須帶 HolderLookup.Provider
    @Override
    public CompoundTag serializeNBT(HolderLookup.Provider provider) {
        return backend.saveNBTData();
    }

    @Override
    public void deserializeNBT(HolderLookup.Provider provider, CompoundTag nbt) {
        backend.loadNBTData(nbt);
    }
}