package com.blacksnow1002.realmmod.system.profession.mining.block;

import com.blacksnow1002.realmmod.system.profession.base.block.BaseProfessionCollectionBlock;
import com.blacksnow1002.realmmod.system.profession.ProfessionType;

public class MinableBlock extends BaseProfessionCollectionBlock {
    public MinableBlock(Properties properties, int rank, ResourceType resourceType) {
        super(properties, ProfessionType.MINING, rank, resourceType);
    }
}
