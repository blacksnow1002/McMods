package com.blacksnow1002.realmmod.util;

import net.minecraft.world.entity.decoration.ArmorStand;
import net.minecraft.network.syncher.EntityDataAccessor;

public class ArmorStandUtils {
    private static EntityDataAccessor<Boolean> DATA_MARKER;

    static {
        try {
            var field = ArmorStand.class.getDeclaredField("DATA_MARKER");
            field.setAccessible(true);
            DATA_MARKER = (EntityDataAccessor<Boolean>) field.get(null);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void setMarker(ArmorStand stand, boolean value) {
        if (DATA_MARKER != null) {
            stand.getEntityData().set(DATA_MARKER, value);
        }
    }
}
