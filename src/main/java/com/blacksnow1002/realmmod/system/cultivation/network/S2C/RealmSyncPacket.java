package com.blacksnow1002.realmmod.system.cultivation.network.S2C;

import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.event.network.CustomPayloadEvent;

public class RealmSyncPacket {
    private final int realmOrdinal;
    private final int layer;

    public RealmSyncPacket(int realmOrdinal, int layer) {
        this.realmOrdinal = realmOrdinal;
        this.layer = layer;
    }

    public RealmSyncPacket(FriendlyByteBuf buf) {
        this.realmOrdinal = buf.readInt();
        this.layer = buf.readInt();
    }

    public void encode(FriendlyByteBuf buf) {
        buf.writeInt(this.realmOrdinal);
        buf.writeInt(this.layer);
    }

    public void handle(CustomPayloadEvent.Context ctx) {
        ctx.enqueueWork(() -> {
            Minecraft mc = Minecraft.getInstance();

            if (mc.player != null && mc.level != null) {
                mc.player.getPersistentData().putInt("RealmOrdinal", this.realmOrdinal);
                mc.player.getPersistentData().putInt("Layer", this.layer);
            }
        });
        ctx.setPacketHandled(true);
    }
}