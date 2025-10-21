package com.blacksnow1002.realmmod.network.packets.C2S;

import com.blacksnow1002.realmmod.spell.BaseSpell;
import com.blacksnow1002.realmmod.spell.SpellRegistry;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.event.network.CustomPayloadEvent;

public record FlySpellPacket(int keyId) implements CustomPacketPayload {

    public static final Type<FlySpellPacket> TYPE =
            new Type<>(ResourceLocation.fromNamespaceAndPath("realmmod", "fly_spell_packet"));

    public FlySpellPacket(FriendlyByteBuf buf) {
        this(buf.readInt());
    }

    public void encode(FriendlyByteBuf buf) {
        buf.writeInt(keyId);
    }

    @Override
    public Type<FlySpellPacket> type() {
        return TYPE;
    }


    public static void handle(FlySpellPacket packet, CustomPayloadEvent.Context context) {
        context.enqueueWork(() -> {
            var player = context.getSender();
            if (player != null) {
                BaseSpell spell = SpellRegistry.get("御劍飛行");
                if (spell != null) {
                    spell.tryCast(player);
                }
            }
        });
        context.setPacketHandled(true);
    }
}