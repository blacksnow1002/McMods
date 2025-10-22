package com.blacksnow1002.realmmod.network.packets.S2C;

import com.blacksnow1002.realmmod.broadcast.ClientBroadcastHandler;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.event.network.CustomPayloadEvent;

public class BroadcastSyncPacket {
    private final String message;
    private final int duration;

    public BroadcastSyncPacket(String message, int duration) {
        this.message = message;
        this.duration = duration;
    }

    public BroadcastSyncPacket(FriendlyByteBuf buf) {
        this.message = buf.readUtf(256);
        this.duration = buf.readInt();
    }

    public void encode(FriendlyByteBuf buf) {
        buf.writeUtf(message);
        buf.writeInt(duration);
    }

    public void handle(CustomPayloadEvent.Context ctx) {
        ctx.enqueueWork(() -> {
                ClientBroadcastHandler.addMessage(message, duration);
        });
        ctx.setPacketHandled(true);
    }
}