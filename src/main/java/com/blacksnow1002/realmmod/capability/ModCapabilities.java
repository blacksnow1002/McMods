package com.blacksnow1002.realmmod.capability;

import com.blacksnow1002.realmmod.assignment.IAssignmentDataManager;
import com.blacksnow1002.realmmod.capability.attribute.IPlayerTotalAttributeData;
import com.blacksnow1002.realmmod.capability.attribute.allocate.IAllocateAttributeData;
import com.blacksnow1002.realmmod.capability.attribute.equipment.IEquipmentAttributeData;
import com.blacksnow1002.realmmod.capability.attribute.realm.IRealmAttributeData;
import com.blacksnow1002.realmmod.capability.attribute.technique.ITechniqueAttributeData;
import com.blacksnow1002.realmmod.capability.mana.IManaData;
import com.blacksnow1002.realmmod.capability.realm_breakthrough.IRealmBreakthroughData;
import com.blacksnow1002.realmmod.capability.cultivation.ICultivationData;
import com.blacksnow1002.realmmod.capability.spiritroot.ISpiritRootData;
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
    public static final Capability<IRealmBreakthroughData> BREAKTHROUGH_CAPABILITY_CAP =
            CapabilityManager.get(new CapabilityToken<>(){});

    public static final Capability<ISpiritRootData> SPIRIT_ROOT_CAP =
            CapabilityManager.get(new CapabilityToken<>(){});

    public static final Capability<IManaData> MANA_CAP =
            CapabilityManager.get(new CapabilityToken<>(){});

    public static final Capability<IAssignmentDataManager> ASSIGNMENT_CAP =
            CapabilityManager.get(new CapabilityToken<>(){});

    public static final Capability<IAllocateAttributeData> ALLOCATE_ATTRIBUTE_CAP =
            CapabilityManager.get(new CapabilityToken<>(){});
    public static final Capability<IRealmAttributeData> REALM_ATTRIBUTE_CAP =
            CapabilityManager.get(new CapabilityToken<>(){});
    public static final Capability<IEquipmentAttributeData> EQUIPMENT_ATTRIBUTE_CAP =
            CapabilityManager.get(new CapabilityToken<>(){});
    public static final Capability<ITechniqueAttributeData> TECHNIQUE_ATTRIBUTE_CAP =
            CapabilityManager.get(new CapabilityToken<>(){});
    public static final Capability<IPlayerTotalAttributeData> PLAYER_TOTAL_ATTRIBUTE_CAP =
            CapabilityManager.get(new CapabilityToken<>(){});


    public static final Capability<IWorldData> WORLD_CAP =
            CapabilityManager.get(new CapabilityToken<>(){});
}
