package com.blacksnow1002.realmmod.system.mailbox.network.C2S;

import com.blacksnow1002.realmmod.system.mailbox.MailboxStorage;
import com.blacksnow1002.realmmod.system.mailbox.ReceiveMail;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.network.CustomPayloadEvent;

import java.util.UUID;

public record ClaimMailPacket(UUID mailId) implements CustomPacketPayload {

    public static final Type<ClaimMailPacket> TYPE =
            new Type<>(ResourceLocation.fromNamespaceAndPath("realmmod", "claim_mail_packet"));

    public ClaimMailPacket(RegistryFriendlyByteBuf buf) {
        this(buf.readUUID());
    }

    public void encode(FriendlyByteBuf buf) {
        buf.writeUUID(mailId);
    }

    @Override
    public Type<ClaimMailPacket> type() {
        return TYPE;
    }


    public static void handle(ClaimMailPacket packet, CustomPayloadEvent.Context ctx) {
        ctx.enqueueWork(() -> {
            Player player = ctx.getSender();
            if (player != null) {
                //領取信件
                if (ReceiveMail.receiveMail(player, packet.mailId())) {
                    MailboxStorage storage = MailboxStorage.get(player.level());
                    System.out.println("start remove mail");
                    storage.removeMail(player.getUUID(), packet.mailId());
                }
            }
        });
        ctx.setPacketHandled(true);
    }
}