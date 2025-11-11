package com.blacksnow1002.realmmod.system.spell.network.C2S;

import com.blacksnow1002.realmmod.system.spell.BaseSpell;
import com.blacksnow1002.realmmod.system.spell.SpellRegistry;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.event.network.CustomPayloadEvent;

public record ShortTeleportSpellPacket(int keyId) implements CustomPacketPayload {

    public static final Type<ShortTeleportSpellPacket> TYPE =
            new Type<>(ResourceLocation.fromNamespaceAndPath("realmmod", "short_teleport_spell_packet"));

    public ShortTeleportSpellPacket(FriendlyByteBuf buf) {
        this(buf.readInt());
    }

    public void encode(FriendlyByteBuf buf) {
        buf.writeInt(keyId);
    }

    @Override
    public Type<ShortTeleportSpellPacket> type() {
        return TYPE;
    }


    public static void handle(ShortTeleportSpellPacket packet, CustomPayloadEvent.Context context) {
        context.enqueueWork(() -> {
            var player = context.getSender();
            if (player != null) {
                BaseSpell spell = SpellRegistry.get("撕裂空間");
                if (spell != null) {
                    spell.tryCast(player);
                }
            }
        });
        context.setPacketHandled(true);
    }
}