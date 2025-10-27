package com.blacksnow1002.realmmod.block.custom;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.state.BlockState;

public class HarvestableBlock extends Block {
    public enum HarvestType {
        COMMON("普通", 0.8, 0.5, 5),
        RARE("稀有", 0.5, 0.1, 10),
        TREASURE("天材地寶", 0.0, 0.0, 0);

        private final String displayName;
        private final double baseSameSuccessRate;
        private final double baseHighSuccessRate;
        private final int baseExp;

        HarvestType(String displayName, double baseSameSuccessRate, double baseHighSuccessRate, int baseExp) {
            this.displayName = displayName;
            this.baseSameSuccessRate = baseSameSuccessRate;
            this.baseHighSuccessRate = baseHighSuccessRate;
            this.baseExp = baseExp;
        }

        public String getDisplayName() { return displayName; }
        public double getBaseSameSuccessRate() { return baseSameSuccessRate; }
        public double getBaseHighSuccessRate() { return baseHighSuccessRate; }
        public int getBaseExp() { return baseExp; }

        public static HarvestType fromString(String type) {
            for (HarvestType t : values()) {
                if (t.displayName.equals(type)) return t;
            }
            return COMMON;
        }
    }

    private final int rank; // 1~9品
    private final HarvestType type;

    public HarvestableBlock(Properties properties, int rank, HarvestType type) {
        super(properties);
        this.rank = rank;
        this.type = type;
    }

    public int getRank() { return rank; }
    public HarvestType getType() { return type; }

    @Override
    public RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }
}