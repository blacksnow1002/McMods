package com.blacksnow1002.realmmod.broadcast;

import com.blacksnow1002.realmmod.network.ModMessages;
import com.blacksnow1002.realmmod.network.packets.S2C.BroadcastSyncPacket;

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