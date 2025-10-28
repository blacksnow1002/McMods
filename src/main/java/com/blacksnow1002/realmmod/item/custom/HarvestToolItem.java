package com.blacksnow1002.realmmod.item.custom;

import com.blacksnow1002.realmmod.item.custom.base.BaseProfessionCollectionToolItem;
import com.blacksnow1002.realmmod.profession.ProfessionType;

public class HarvestToolItem extends BaseProfessionCollectionToolItem {
    public HarvestToolItem(Properties properties, Grades grade, int rank) {
        super(properties, ProfessionType.HARVEST, grade, rank);
    }
}