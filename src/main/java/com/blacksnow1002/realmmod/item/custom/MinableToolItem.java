package com.blacksnow1002.realmmod.item.custom;

import com.blacksnow1002.realmmod.item.custom.base.BaseProfessionCollectionToolItem;
import com.blacksnow1002.realmmod.profession.ProfessionType;

public class MinableToolItem extends BaseProfessionCollectionToolItem {

    public MinableToolItem(Properties properties, Grades grades, int rank) {
        super(properties, ProfessionType.MINING,  grades, rank);
    }
}
