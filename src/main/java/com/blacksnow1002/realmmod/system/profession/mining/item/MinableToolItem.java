package com.blacksnow1002.realmmod.system.profession.mining.item;

import com.blacksnow1002.realmmod.system.profession.base.item.BaseProfessionCollectionToolItem;
import com.blacksnow1002.realmmod.system.profession.ProfessionType;

public class MinableToolItem extends BaseProfessionCollectionToolItem {

    public MinableToolItem(Properties properties, Grades grades, int rank) {
        super(properties, ProfessionType.MINING,  grades, rank);
    }
}
