package com.blacksnow1002.realmmod.core.capability;

import com.blacksnow1002.realmmod.system.assignment.capability.IAssignmentDataManager;
import com.blacksnow1002.realmmod.system.battle.attribute.capability.IPlayerTotalAttributeData;
import com.blacksnow1002.realmmod.system.battle.attribute.capability.source.allocate.IAllocateAttributeData;
import com.blacksnow1002.realmmod.system.battle.attribute.capability.source.equipment.IEquipmentAttributeData;
import com.blacksnow1002.realmmod.system.battle.attribute.capability.source.realm.IRealmAttributeData;
import com.blacksnow1002.realmmod.system.battle.attribute.capability.source.technique.ITechniqueAttributeData;
import com.blacksnow1002.realmmod.system.battle.mana.capability.IManaData;
import com.blacksnow1002.realmmod.system.economy.money.capability.IMoneyData;
import com.blacksnow1002.realmmod.system.cultivation.breakthrough.capability.IRealmBreakthroughData;
import com.blacksnow1002.realmmod.system.cultivation.realm.capability.ICultivationData;
import com.blacksnow1002.realmmod.player.spiritroot.ISpiritRootData;
import com.blacksnow1002.realmmod.world.capability.world.IWorldData;
import com.blacksnow1002.realmmod.player.age.capability.IAgeData;
import com.blacksnow1002.realmmod.system.profession.alchemy.capability.IProfessionAlchemyData;
import com.blacksnow1002.realmmod.system.profession.base.capability.IProfessionHeartDemonData;
import com.blacksnow1002.realmmod.system.profession.harvest.capability.IProfessionHarvestData;
import com.blacksnow1002.realmmod.system.profession.mining.capability.IProfessionMiningData;
import com.blacksnow1002.realmmod.system.profession.reforge.capability.IProfessionReforgeData;
import com.blacksnow1002.realmmod.system.technique.capability.ITechniqueDataManager;
import com.blacksnow1002.realmmod.system.title.capability.ITitleDataManager;
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

    public static final Capability<IMoneyData> MONEY_CAP =
            CapabilityManager.get(new CapabilityToken<>(){});

    public static final Capability<IAssignmentDataManager> ASSIGNMENT_CAP =
            CapabilityManager.get(new CapabilityToken<>(){});
    public static final Capability<ITechniqueDataManager> TECHNIQUE_CAP =
            CapabilityManager.get(new CapabilityToken<>(){});
    public static final Capability<ITitleDataManager> TITLE_CAP =
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


    public static final Capability<IProfessionHeartDemonData> PROFESSION_HEART_DEMON_CAP =
            CapabilityManager.get(new CapabilityToken<>(){});

    public static final Capability<IProfessionHarvestData> PROFESSION_HARVEST_CAP =
            CapabilityManager.get(new CapabilityToken<>(){});
    public static final Capability<IProfessionMiningData> PROFESSION_MINING_CAP =
            CapabilityManager.get(new CapabilityToken<>(){});
    public static final Capability<IProfessionAlchemyData> PROFESSION_ALCHEMY_CAP =
            CapabilityManager.get(new CapabilityToken<>(){});
    public static final Capability<IProfessionReforgeData> PROFESSION_REFORGE_CAP =
            CapabilityManager.get(new CapabilityToken<>(){});


    public static final Capability<IWorldData> WORLD_CAP =
            CapabilityManager.get(new CapabilityToken<>(){});
}
