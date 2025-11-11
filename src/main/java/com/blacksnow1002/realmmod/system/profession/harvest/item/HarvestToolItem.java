package com.blacksnow1002.realmmod.system.profession.harvest.item;

import com.blacksnow1002.realmmod.system.profession.base.item.BaseProfessionCollectionToolItem;
import com.blacksnow1002.realmmod.system.profession.ProfessionType;

public class HarvestToolItem extends BaseProfessionCollectionToolItem {
    public HarvestToolItem(Properties properties, Grades grade, int rank) {
        super(properties, ProfessionType.HARVEST, grade, rank);
    }
}