package com.blacksnow1002.realmmod.system.profession.harvest.block;

import com.blacksnow1002.realmmod.system.profession.base.block.BaseProfessionCollectionBlock;
import com.blacksnow1002.realmmod.system.profession.ProfessionType;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.state.BlockState;

public class HarvestableBlock extends BaseProfessionCollectionBlock {
    public HarvestableBlock(Properties properties, int rank, ResourceType resourceType) {
        super(properties, ProfessionType.HARVEST, rank, resourceType);
    }

    @Override
    public RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }
}