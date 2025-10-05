package com.blacksnow1002.realmmod.block.custom;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public class SpiritVisualBlock extends Block {
    public SpiritVisualBlock(Properties properties) {
        super(properties
                .strength(1.5f)
                .noOcclusion() // 允許透明,但保持碰撞
        );
    }

    // 確保有碰撞形狀
    @Override
    public VoxelShape getCollisionShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return Shapes.block(); // 完整方塊碰撞箱
    }

    // 確保不影響視覺形狀
    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return Shapes.block();
    }
}
