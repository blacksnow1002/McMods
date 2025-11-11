package com.blacksnow1002.realmmod.system.spell.handlers;

import net.minecraft.core.BlockPos;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class TeleportHandler {
    private static final Map<UUID, MarkedLocation> markedLocations = new HashMap<>();

    public static void setMark(UUID playerId, String dimension, double x, double y, double z, BlockPos blockPos) {
        markedLocations.put(playerId, new MarkedLocation(dimension, x, y, z, blockPos));
    }

    public static MarkedLocation getMark(UUID playerId) {
        return markedLocations.get(playerId);
    }

    public static boolean hasMark(UUID playerId) {
        return markedLocations.containsKey(playerId);
    }

    public static void removeMark(UUID playerId) {
        markedLocations.remove(playerId);
    }


    public static class MarkedLocation {
        public final String dimension;
        public final double x, y, z;
        public final BlockPos blockPos;

        public MarkedLocation(String dimension, double x, double y, double z, BlockPos blockPos) {
            this.dimension = dimension;
            this.x = x;
            this.y = y;
            this.z = z;
            this.blockPos = blockPos;
        }
    }
}
