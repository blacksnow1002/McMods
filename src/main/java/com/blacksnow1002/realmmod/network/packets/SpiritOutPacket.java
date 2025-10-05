package com.blacksnow1002.realmmod.network.packets;

import com.blacksnow1002.realmmod.spell.BaseSpell;
import com.blacksnow1002.realmmod.spell.SpellRegistry;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.event.network.CustomPayloadEvent;

public record SpiritOutPacket(int keyId) implements CustomPacketPayload {

    public static final Type<SpiritOutPacket> TYPE =
            new Type<>(ResourceLocation.fromNamespaceAndPath("realmmod", "spirit_out_packet"));

    public SpiritOutPacket(FriendlyByteBuf buf) {
        this(buf.readInt());
    }

    public void encode(FriendlyByteBuf buf) {
        buf.writeInt(keyId);
    }

    @Override
    public Type<SpiritOutPacket> type() {
        return TYPE;
    }


    public static void handle(SpiritOutPacket packet, CustomPayloadEvent.Context context) {
        context.enqueueWork(() -> {
            var player = context.getSender();
            if (player != null) {
                BaseSpell spell = SpellRegistry.get("元嬰出竅");
                if (spell != null) {
                    spell.tryCast(player);
                }
            }
        });
        context.setPacketHandled(true);
    }
}