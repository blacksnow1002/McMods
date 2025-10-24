package com.blacksnow1002.realmmod.network.packets.S2C;

import com.blacksnow1002.realmmod.mailbox.ClientMailCache;
import com.blacksnow1002.realmmod.mailbox.Mail;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraftforge.event.network.CustomPayloadEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class MailSyncPacket {
    private final UUID uuid;
    private final List<Mail> mails;

    public MailSyncPacket(UUID playerUUID, List<Mail> mails) {
        this.uuid = playerUUID;
        this.mails = mails;
    }

    public MailSyncPacket(RegistryFriendlyByteBuf buf) {
        this.uuid = buf.readUUID();

        int size = buf.readInt();
        this.mails = new ArrayList<>();
        for (int i=0; i<size; i++) {
            this.mails.add(Mail.fromBytes(buf));
        }
    }

    public void encode(RegistryFriendlyByteBuf buf) {
        buf.writeUUID(uuid);
        buf.writeInt(mails.size());
        for (Mail mail : mails) {
            mail.toBytes(buf);
        }
    }

    public void handle(CustomPayloadEvent.Context ctx) {
        ctx.enqueueWork(() -> {
            ClientMailCache.setMails(uuid, mails);
            System.out.println("[MailSyncPacket] 收到玩家 " + this.uuid + " 的郵件，共 " + mails.size() + " 封");
        });
        ctx.setPacketHandled(true);
    }
}