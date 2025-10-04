package com.blacksnow1002.realmmod.capability;

import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;

public class ModCapabilities {
    public static final Capability<ICultivationData> CULTIVATION_CAP =
            CapabilityManager.get(new CapabilityToken<>(){});
}
