package com.blacksnow1002.realmmod.block.custom.base;

import com.blacksnow1002.realmmod.profession.ProfessionType;
import net.minecraft.world.level.block.Block;

/**
 * 職業方塊基礎類
 */
public abstract class BaseProfessionCollectionBlock extends Block {

    public enum ResourceType {
        COMMON("普通", 0.8, 5),
        RARE("稀有", 0.5, 10),
        TREASURE("天材地寶", 0.0, 0);

        private final String displayName;
        private final double baseSuccessRate;
        private final int baseExp;

        ResourceType(String displayName, double baseSuccessRate, int baseExp) {
            this.displayName = displayName;
            this.baseSuccessRate = baseSuccessRate;
            this.baseExp = baseExp;
        }

        public String getDisplayName() { return displayName; }
        public double getBaseSuccessRate() { return baseSuccessRate; }
        public int getBaseExp() { return baseExp; }
    }

    protected final ProfessionType professionType;
    protected final int rank;
    protected final ResourceType resourceType;

    public BaseProfessionCollectionBlock(Properties properties, ProfessionType professionType, int rank, ResourceType resourceType) {
        super(properties);
        this.professionType = professionType;
        this.rank = rank;
        this.resourceType = resourceType;
    }

    public ProfessionType getProfessionType() { return professionType; }
    public int getRank() { return rank; }
    public ResourceType getResourceType() { return resourceType; }
}
