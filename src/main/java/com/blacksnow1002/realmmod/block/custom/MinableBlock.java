package com.blacksnow1002.realmmod.block.custom;

import com.blacksnow1002.realmmod.block.custom.base.BaseProfessionCollectionBlock;
import com.blacksnow1002.realmmod.profession.ProfessionType;

public class MinableBlock extends BaseProfessionCollectionBlock {
    public MinableBlock(Properties properties, int rank, ResourceType resourceType) {
        super(properties, ProfessionType.MINING, rank, resourceType);
    }
}
