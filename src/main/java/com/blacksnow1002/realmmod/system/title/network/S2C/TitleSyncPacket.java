package com.blacksnow1002.realmmod.system.title.network.S2C;

import com.blacksnow1002.realmmod.client.title.cache.ClientTitleCache;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.event.network.CustomPayloadEvent;

import java.util.UUID;

public class TitleSyncPacket {
    private final UUID uuid;
    private final String titleId;

    public TitleSyncPacket(UUID playerUUID, String titleId) {
        this.uuid = playerUUID;
        this.titleId = titleId;
    }

    public TitleSyncPacket(FriendlyByteBuf buf) {
        this.uuid = buf.readUUID();
        this.titleId = buf.readUtf(256);
    }

    public void encode(FriendlyByteBuf buf) {
        buf.writeUUID(uuid);
        buf.writeUtf(titleId);
    }

    public void handle(CustomPayloadEvent.Context ctx) {
        ctx.enqueueWork(() -> {
                ClientTitleCache.setTitleId(this.uuid, this.titleId);
                System.out.println("[ClientTitleCache] 更新玩家" + this.uuid + "的稱號");
        });
        ctx.setPacketHandled(true);
    }
}