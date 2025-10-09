package com.blacksnow1002.realmmod.capability;

import com.blacksnow1002.realmmod.capability.cultivation.ICultivationData;
import com.blacksnow1002.realmmod.capability.magic_point.IMagicPointData;
import com.blacksnow1002.realmmod.capability.world.IWorldData;
import com.blacksnow1002.realmmod.capability.age.IAgeData;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;

public class ModCapabilities {
    public static final Capability<ICultivationData> CULTIVATION_CAP =
            CapabilityManager.get(new CapabilityToken<>(){});
    public static final Capability<IAgeData> AGE_CAP =
            CapabilityManager.get(new CapabilityToken<>(){});
    public static final Capability<IMagicPointData> MAGIC_POINT_CAP =
            CapabilityManager.get(new CapabilityToken<>(){});

    public static final Capability<IWorldData> WORLD_CAP =
            CapabilityManager.get(new CapabilityToken<>(){});
}
