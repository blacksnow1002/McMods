package com.blacksnow1002.realmmod.system.broadcast;

import com.blacksnow1002.realmmod.common.network.ModMessages;
import com.blacksnow1002.realmmod.system.broadcast.network.S2C.BroadcastSyncPacket;

public class BroadcastManager {
    private static final int DEFAULT_DURATION = 60;

    public static void broadcast(String message) {
        broadcast(message, DEFAULT_DURATION);
    }

    public static void broadcast(String message, int durationTicks) {
        BroadcastSyncPacket packet = new BroadcastSyncPacket(message, durationTicks);

        // 使用你在 ModMessages 中定義的方法
        ModMessages.sendToAllPlayers(packet);
    }

}