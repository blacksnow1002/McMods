package com.blacksnow1002.realmmod.profession.common;

import com.blacksnow1002.realmmod.capability.ModCapabilities;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.common.util.LazyOptional;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ProfessionHeartDemonProvider implements ICapabilitySerializable<CompoundTag> {
    public static final String IDENTIFIER = "profession_heart_demon";

    private final ProfessionHeartDemonData backend = new ProfessionHeartDemonData();
    private final LazyOptional<IProfessionHeartDemonData> optional = LazyOptional.of(() -> backend);

    @Override
    public <T> @NotNull LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
        return cap == ModCapabilities.PROFESSION_HEART_DEMON_CAP ? optional.cast() : LazyOptional.empty();
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