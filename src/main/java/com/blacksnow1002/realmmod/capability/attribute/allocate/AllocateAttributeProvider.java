package com.blacksnow1002.realmmod.capability.attribute.allocate;

import com.blacksnow1002.realmmod.capability.ModCapabilities;
import com.blacksnow1002.realmmod.capability.attribute.equipment.EquipmentAttributeData;
import com.blacksnow1002.realmmod.capability.attribute.equipment.IEquipmentAttributeData;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.common.util.LazyOptional;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class AllocateAttributeProvider implements ICapabilitySerializable<CompoundTag> {
    public static final String IDENTIFIER = "allocate_attribute";

    private final AllocateAttributeData backend = new AllocateAttributeData();
    private final LazyOptional<IAllocateAttributeData> optional = LazyOptional.of(() -> backend);

    @Override
    public <T> @NotNull LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
        return cap == ModCapabilities.ALLOCATE_ATTRIBUTE_CAP ? optional.cast() : LazyOptional.empty();
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
