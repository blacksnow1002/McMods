package com.blacksnow1002.realmmod.capability.spiritroot;

import com.blacksnow1002.realmmod.capability.ModCapabilities;
import com.blacksnow1002.realmmod.capability.attribute.IPlayerTotalAttributeData;
import com.blacksnow1002.realmmod.capability.attribute.PlayerTotalAttributeData;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.common.util.LazyOptional;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class SpiritRootProvider implements ICapabilitySerializable<CompoundTag> {
    public static final String IDENTIFIER = "spirit_root";

    private final SpiritRootData backend = new SpiritRootData();
    private final LazyOptional<ISpiritRootData> optional = LazyOptional.of(() -> backend);

    @Override
    public <T> @NotNull LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
        return cap == ModCapabilities.SPIRIT_ROOT_CAP ? optional.cast() : LazyOptional.empty();
    }

    @Override
    public CompoundTag serializeNBT(HolderLookup.Provider provider) {
        CompoundTag tag = new CompoundTag();
        backend.saveNBTData(tag);
        return tag;
    }

    @Override
    public void deserializeNBT(HolderLookup.Provider provider, CompoundTag nbt) {
        backend.loadNBTData(nbt);
    }

}
