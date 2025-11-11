package com.blacksnow1002.realmmod.core.registry;

import com.blacksnow1002.realmmod.RealmMod;
import com.mojang.serialization.Codec;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

public class ModDataComponents {

    public static final DeferredRegister<DataComponentType<?>> DATA_COMPONENT_TYPES =
            DeferredRegister.create(net.minecraft.core.registries.Registries.DATA_COMPONENT_TYPE, RealmMod.MOD_ID);

    public static final RegistryObject<DataComponentType<Integer>> PILL_QUALITY =
            DATA_COMPONENT_TYPES.register("pill_quality", () ->
                    DataComponentType.<Integer>builder()
                            .persistent(Codec.INT)
                            .networkSynchronized(ByteBufCodecs.INT)
                            .build()
            );
}