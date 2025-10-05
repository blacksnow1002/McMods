package com.blacksnow1002.realmmod.network;

import com.blacksnow1002.realmmod.network.packets.*;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.ChannelBuilder;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.network.SimpleChannel;
import net.minecraft.resources.ResourceLocation;

public class ModMessages {
    private static final int PROTOCOL_VERSION = 1;

    public static final SimpleChannel INSTANCE = ChannelBuilder
            .named(ResourceLocation.fromNamespaceAndPath("realmmod", "main"))
            .networkProtocolVersion(PROTOCOL_VERSION)
            .clientAcceptedVersions((status, version) -> true)  // 改這裡
            .serverAcceptedVersions((status, version) -> true)  // 改這裡
            .simpleChannel();

    private static int id = 0;
    private static int nextId() { return id++; }

    // 呼叫這個方法來註冊封包（在 commonSetup 裡呼叫一次）
    public static void register() {
        // 註冊 StartMeditationPacket（Client -> Server）
        INSTANCE.messageBuilder(StartMeditationPacket.class, nextId(), NetworkDirection.PLAY_TO_SERVER)
                .decoder(StartMeditationPacket::new)      // FriendlyByteBuf -> packet
                .encoder(StartMeditationPacket::encode)  // packet -> FriendlyByteBuf
                .consumerMainThread(StartMeditationPacket::handle) // 處理 (主執行緒)
                .add();

        INSTANCE.messageBuilder(StartCultivationStatusPacket.class, nextId(), NetworkDirection.PLAY_TO_SERVER)
                .decoder(StartCultivationStatusPacket::new)      // FriendlyByteBuf -> packet
                .encoder(StartCultivationStatusPacket::encode)  // packet -> FriendlyByteBuf
                .consumerMainThread(StartCultivationStatusPacket::handle) // 處理 (主執行緒)
                .add();

        INSTANCE.messageBuilder(SpellPacket.class, nextId(), NetworkDirection.PLAY_TO_SERVER)
                .decoder(SpellPacket::new)      // FriendlyByteBuf -> packet
                .encoder(SpellPacket::encode)  // packet -> FriendlyByteBuf
                .consumerMainThread(SpellPacket::handle) // 處理 (主執行緒)
                .add();

        INSTANCE.messageBuilder(LingMuSpellPacket.class, nextId(), NetworkDirection.PLAY_TO_SERVER)
                .decoder(LingMuSpellPacket::new)      // FriendlyByteBuf -> packet
                .encoder(LingMuSpellPacket::encode)  // packet -> FriendlyByteBuf
                .consumerMainThread(LingMuSpellPacket::handle) // 處理 (主執行緒)
                .add();

        INSTANCE.messageBuilder(LingMuSyncPacket.class, nextId(), NetworkDirection.PLAY_TO_CLIENT)
                .decoder(LingMuSyncPacket::new)
                .encoder(LingMuSyncPacket::encode)
                .consumerMainThread(LingMuSyncPacket::handle)
                .add();
    }

    // 從客戶端發送到伺服器
    public static void sendToServer(StartMeditationPacket packet) {
        INSTANCE.send(packet, PacketDistributor.SERVER.noArg());
    }

    public static void sendToServer(StartCultivationStatusPacket packet) {
        INSTANCE.send(packet, PacketDistributor.SERVER.noArg());
    }

    public static void sendToServer(SpellPacket packet) {
        INSTANCE.send(packet, PacketDistributor.SERVER.noArg());
    }

    public static void sendToServer(LingMuSpellPacket packet) {
        INSTANCE.send(packet, PacketDistributor.SERVER.noArg());
    }

    // 從伺服器發送到特定玩家
    public static void sendToPlayer(LingMuSyncPacket packet, ServerPlayer player) {
        INSTANCE.send(packet, PacketDistributor.PLAYER.with(player));
    }
}
