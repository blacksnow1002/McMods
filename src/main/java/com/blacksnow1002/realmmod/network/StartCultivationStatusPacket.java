package com.blacksnow1002.realmmod.network;

import com.blacksnow1002.realmmod.capability.CultivationRealm;
import com.blacksnow1002.realmmod.capability.ModCapabilities;
import com.blacksnow1002.realmmod.client.key.ModKeyBindings;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.event.network.CustomPayloadEvent;

import java.util.Map;

public record StartCultivationStatusPacket(int keyId) implements CustomPacketPayload {

    public static final Type<StartCultivationStatusPacket> TYPE =
            new Type<>(ResourceLocation.fromNamespaceAndPath("realmmod", "cultivation_status"));

    public StartCultivationStatusPacket(FriendlyByteBuf buf) {
        this(buf.readInt());
    }

    public void encode(FriendlyByteBuf buf) {
        buf.writeInt(keyId);
    }

    private static Map<Integer, String> setDisplayLayer =
            Map.of(
                    1, "一層",
                    2, "二層",
                    3, "三層",
                    4, "四層",
                    5, "五層",
                    6, "六層",
                    7, "七層",
                    8, "八層",
                    9, "九層",
                    10, "大圓滿"
            );

    @Override
    public Type<StartCultivationStatusPacket> type() {
        return TYPE;
    }

    public static void handle(StartCultivationStatusPacket packet, CustomPayloadEvent.Context context) {
        context.enqueueWork(() -> {
            var player = context.getSender();
            if (player != null) {

                player.getCapability(ModCapabilities.CULTIVATION_CAP).ifPresent(cap -> {
                    if(cap.getRealm().ordinal() < CultivationRealm.second.ordinal()) {
                        player.sendSystemMessage(Component.translatable(
                                "message.realmmod.send_cultivation_status.low_ordinal"
                        ));
                    }
                    player.sendSystemMessage(Component.translatable(
                            "message.realmmod.send_cultivation_status.realm",
                            cap.getRealm().getDisplayName(),
                            setDisplayLayer.get(cap.getLayer())
                    ));
                    player.sendSystemMessage(Component.translatable(
                            "message.realmmod.send_cultivation_status.cultivation",
                            cap.getCultivation(),
                            cap.getRealm().getRequiredPerLayer()
                    ));
                    player.sendSystemMessage(Component.translatable(
                            "message.realmmod.send_cultivation_status.possibility",
                            (int) (cap.getBreakthroughSuccessPossibility() * 100)
                    ));
                });
            }
        });
        context.setPacketHandled(true);
    }
}