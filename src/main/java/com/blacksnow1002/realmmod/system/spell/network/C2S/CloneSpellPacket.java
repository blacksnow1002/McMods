package com.blacksnow1002.realmmod.system.spell.network.C2S;

import com.blacksnow1002.realmmod.system.spell.BaseSpell;
import com.blacksnow1002.realmmod.system.spell.SpellRegistry;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.event.network.CustomPayloadEvent;

public record CloneSpellPacket(int keyId) implements CustomPacketPayload {

    public static final Type<CloneSpellPacket> TYPE =
            new Type<>(ResourceLocation.fromNamespaceAndPath("realmmod", "clone_spell_packet"));

    public CloneSpellPacket(FriendlyByteBuf buf) {
        this(buf.readInt());
    }

    public void encode(FriendlyByteBuf buf) {
        buf.writeInt(keyId);
    }

    @Override
    public Type<CloneSpellPacket> type() {
        return TYPE;
    }


    public static void handle(CloneSpellPacket packet, CustomPayloadEvent.Context context) {
        context.enqueueWork(() -> {
            var player = context.getSender();
            if (player != null) {
                BaseSpell spell = SpellRegistry.get("身外化身");
                if (spell != null) {
                    spell.tryCast(player);
                }
            }
        });
        context.setPacketHandled(true);
    }
}