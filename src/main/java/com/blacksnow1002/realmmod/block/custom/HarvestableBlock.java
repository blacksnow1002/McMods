package com.blacksnow1002.realmmod.block.custom;

import com.blacksnow1002.realmmod.block.custom.base.BaseProfessionCollectionBlock;
import com.blacksnow1002.realmmod.profession.ProfessionType;
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