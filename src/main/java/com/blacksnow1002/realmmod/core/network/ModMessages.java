package com.blacksnow1002.realmmod.core.network;

import com.blacksnow1002.realmmod.system.broadcast.network.S2C.BroadcastSyncPacket;
import com.blacksnow1002.realmmod.system.mailbox.network.C2S.ClaimMailPacket;
import com.blacksnow1002.realmmod.system.mailbox.network.S2C.MailSyncPacket;
import com.blacksnow1002.realmmod.system.profession.alchemy.network.C2S.StartAlchemyPacket;
import com.blacksnow1002.realmmod.system.cultivation.network.S2C.RealmSyncPacket;
import com.blacksnow1002.realmmod.system.cultivation.network.C2S.StartCultivationStatusPacket;
import com.blacksnow1002.realmmod.system.cultivation.network.C2S.StartMeditationPacket;
import com.blacksnow1002.realmmod.system.spell.network.C2S.*;
import com.blacksnow1002.realmmod.system.spell.network.S2C.LingMuSyncPacket;
import com.blacksnow1002.realmmod.system.title.network.S2C.TitleSyncPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
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

        INSTANCE.messageBuilder(FlySpellPacket.class, nextId(), NetworkDirection.PLAY_TO_SERVER)
                .decoder(FlySpellPacket::new)      // FriendlyByteBuf -> packet
                .encoder(FlySpellPacket::encode)  // packet -> FriendlyByteBuf
                .consumerMainThread(FlySpellPacket::handle) // 處理 (主執行緒)
                .add();

        INSTANCE.messageBuilder(SpiritOutPacket.class, nextId(), NetworkDirection.PLAY_TO_SERVER)
                .decoder(SpiritOutPacket::new)      // FriendlyByteBuf -> packet
                .encoder(SpiritOutPacket::encode)  // packet -> FriendlyByteBuf
                .consumerMainThread(SpiritOutPacket::handle) // 處理 (主執行緒)
                .add();

        INSTANCE.messageBuilder(SetMarkPacket.class, nextId(), NetworkDirection.PLAY_TO_SERVER)
                .decoder(SetMarkPacket::new)      // FriendlyByteBuf -> packet
                .encoder(SetMarkPacket::encode)  // packet -> FriendlyByteBuf
                .consumerMainThread(SetMarkPacket::handle) // 處理 (主執行緒)
                .add();

        INSTANCE.messageBuilder(MarkedTeleportSpellPacket.class, nextId(), NetworkDirection.PLAY_TO_SERVER)
                .decoder(MarkedTeleportSpellPacket::new)      // FriendlyByteBuf -> packet
                .encoder(MarkedTeleportSpellPacket::encode)  // packet -> FriendlyByteBuf
                .consumerMainThread(MarkedTeleportSpellPacket::handle) // 處理 (主執行緒)
                .add();
        INSTANCE.messageBuilder(ShortTeleportSpellPacket.class, nextId(), NetworkDirection.PLAY_TO_SERVER)
                .decoder(ShortTeleportSpellPacket::new)      // FriendlyByteBuf -> packet
                .encoder(ShortTeleportSpellPacket::encode)  // packet -> FriendlyByteBuf
                .consumerMainThread(ShortTeleportSpellPacket::handle) // 處理 (主執行緒)
                .add();
        INSTANCE.messageBuilder(GiantSpellPacket.class, nextId(), NetworkDirection.PLAY_TO_SERVER)
                .decoder(GiantSpellPacket::new)      // FriendlyByteBuf -> packet
                .encoder(GiantSpellPacket::encode)  // packet -> FriendlyByteBuf
                .consumerMainThread(GiantSpellPacket::handle) // 處理 (主執行緒)
                .add();
        INSTANCE.messageBuilder(CloneSpellPacket.class, nextId(), NetworkDirection.PLAY_TO_SERVER)
                .decoder(CloneSpellPacket::new)      // FriendlyByteBuf -> packet
                .encoder(CloneSpellPacket::encode)  // packet -> FriendlyByteBuf
                .consumerMainThread(CloneSpellPacket::handle) // 處理 (主執行緒)
                .add();
        INSTANCE.messageBuilder(TransformSpellPacket.class, nextId(), NetworkDirection.PLAY_TO_SERVER)
                .decoder(TransformSpellPacket::new)      // FriendlyByteBuf -> packet
                .encoder(TransformSpellPacket::encode)  // packet -> FriendlyByteBuf
                .consumerMainThread(TransformSpellPacket::handle) // 處理 (主執行緒)
                .add();
        INSTANCE.messageBuilder(CreateSpellPacket.class, nextId(), NetworkDirection.PLAY_TO_SERVER)
                .decoder(CreateSpellPacket::new)      // FriendlyByteBuf -> packet
                .encoder(CreateSpellPacket::encode)  // packet -> FriendlyByteBuf
                .consumerMainThread(CreateSpellPacket::handle) // 處理 (主執行緒)
                .add();
        INSTANCE.messageBuilder(ClaimMailPacket.class, nextId(), NetworkDirection.PLAY_TO_SERVER)
                .decoder(ClaimMailPacket::new)      // FriendlyByteBuf -> packet
                .encoder(ClaimMailPacket::encode)  // packet -> FriendlyByteBuf
                .consumerMainThread(ClaimMailPacket::handle) // 處理 (主執行緒)
                .add();
        INSTANCE.messageBuilder(StartAlchemyPacket.class, nextId(), NetworkDirection.PLAY_TO_SERVER)
                .decoder(StartAlchemyPacket::new)      // FriendlyByteBuf -> packet
                .encoder(StartAlchemyPacket::encode)  // packet -> FriendlyByteBuf
                .consumerMainThread(StartAlchemyPacket::handle) // 處理 (主執行緒)
                .add();



        INSTANCE.messageBuilder(LingMuSyncPacket.class, nextId(), NetworkDirection.PLAY_TO_CLIENT)
                .decoder(LingMuSyncPacket::new)
                .encoder(LingMuSyncPacket::encode)
                .consumerMainThread(LingMuSyncPacket::handle)
                .add();

        INSTANCE.messageBuilder(RealmSyncPacket.class, nextId(), NetworkDirection.PLAY_TO_CLIENT)
                .decoder(RealmSyncPacket::new)
                .encoder(RealmSyncPacket::encode)
                .consumerMainThread(RealmSyncPacket::handle)
                .add();

        INSTANCE.messageBuilder(TitleSyncPacket.class, nextId(), NetworkDirection.PLAY_TO_CLIENT)
                .decoder(TitleSyncPacket::new)
                .encoder(TitleSyncPacket::encode)
                .consumerMainThread(TitleSyncPacket::handle)
                .add();

        INSTANCE.messageBuilder(BroadcastSyncPacket.class, nextId(), NetworkDirection.PLAY_TO_CLIENT)
                .decoder(BroadcastSyncPacket::new)
                .encoder(BroadcastSyncPacket::encode)
                .consumerMainThread(BroadcastSyncPacket::handle)
                .add();

        INSTANCE.messageBuilder(MailSyncPacket.class, nextId(), NetworkDirection.PLAY_TO_CLIENT)
                .decoder(MailSyncPacket::new)
                .encoder(MailSyncPacket::encode)
                .consumerMainThread(MailSyncPacket::handle)
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

    public static void sendToServer(FlySpellPacket packet) {
        INSTANCE.send(packet, PacketDistributor.SERVER.noArg());
    }

    public static void sendToServer(SpiritOutPacket packet) {
        INSTANCE.send(packet, PacketDistributor.SERVER.noArg());
    }
    public static void sendToServer(SetMarkPacket packet) {
        INSTANCE.send(packet, PacketDistributor.SERVER.noArg());
    }
    public static void sendToServer(MarkedTeleportSpellPacket packet) {
        INSTANCE.send(packet, PacketDistributor.SERVER.noArg());
    }
    public static void sendToServer(ShortTeleportSpellPacket packet) {
        INSTANCE.send(packet, PacketDistributor.SERVER.noArg());
    }
    public static void sendToServer(GiantSpellPacket packet) {
        INSTANCE.send(packet, PacketDistributor.SERVER.noArg());
    }
    public static void sendToServer(CloneSpellPacket packet) {
        INSTANCE.send(packet, PacketDistributor.SERVER.noArg());
    }
    public static void sendToServer(TransformSpellPacket packet) {
        INSTANCE.send(packet, PacketDistributor.SERVER.noArg());
    }
    public static void sendToServer(CreateSpellPacket packet) {
        INSTANCE.send(packet, PacketDistributor.SERVER.noArg());
    }

    public static void sendToServer(ClaimMailPacket packet) {
        INSTANCE.send(packet, PacketDistributor.SERVER.noArg());
    }

    public static void sendToServer(StartAlchemyPacket packet) {
        INSTANCE.send(packet, PacketDistributor.SERVER.noArg());
    }

    // 從伺服器發送到特定玩家
    public static void sendToPlayer(LingMuSyncPacket packet, ServerPlayer player) {
        INSTANCE.send(packet, PacketDistributor.PLAYER.with(player));
    }

    public static void sendToPlayer(RealmSyncPacket packet, ServerPlayer player) {
        INSTANCE.send(packet, PacketDistributor.PLAYER.with(player));
    }

    public static void sendToPlayer(TitleSyncPacket packet, ServerPlayer player) {
        INSTANCE.send(packet, PacketDistributor.PLAYER.with(player));
    }

    public static void sendToPlayer(MailSyncPacket packet, ServerPlayer player) {
        INSTANCE.send(packet, PacketDistributor.PLAYER.with(player));
    }

    // 傳送給所有玩家
    public static void sendToAllPlayers(BroadcastSyncPacket packet) {
        INSTANCE.send(packet, PacketDistributor.ALL.noArg());
    }

    // 傳送給所有視野範圍內的玩家
    public static <MSG> void sendToPlayersTrackingEntityAndSelf(MSG message, Entity entity) {
        INSTANCE.send(message, PacketDistributor.TRACKING_ENTITY_AND_SELF.with(entity));
    }
}
