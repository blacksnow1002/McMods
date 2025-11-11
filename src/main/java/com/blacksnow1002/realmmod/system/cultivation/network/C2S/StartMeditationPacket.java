package com.blacksnow1002.realmmod.system.cultivation.network.C2S;

import com.blacksnow1002.realmmod.core.capability.ModCapabilities;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.event.network.CustomPayloadEvent;

public record StartMeditationPacket(int keyId) implements CustomPacketPayload {

    public static final CustomPacketPayload.Type<StartMeditationPacket> TYPE =
            new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath("realmmod", "start_meditation"));

    public StartMeditationPacket(FriendlyByteBuf buf) {
        this(buf.readInt());
    }

    public void encode(FriendlyByteBuf buf) {
        buf.writeInt(keyId);
    }

    @Override
    public CustomPacketPayload.Type<StartMeditationPacket> type() {
        return TYPE;
    }

    public static void handle(StartMeditationPacket packet, CustomPayloadEvent.Context context) {
        context.enqueueWork(() -> {
            var player = context.getSender();
            if (player != null) {
                CompoundTag data = player.getPersistentData();
                boolean isMeditating = data.getBoolean("realmmod.Meditation.IsMeditating");

                System.out.println("伺服器：收到封包，當前狀態 = " + isMeditating); // 除錯

                if (!isMeditating) {
                    data.putBoolean("realmmod.Meditation.IsMeditating", true);
                    data.putInt("realmmod.Meditation.StillTicks", 0);
                    player.sendSystemMessage(Component.translatable("message.realmmod.meditation.start"));
                    System.out.println("伺服器：" + player + "設定為開始冥想"); // 除錯

                    player.getCapability(ModCapabilities.BREAKTHROUGH_CAPABILITY_CAP).ifPresent(cap -> {
                        if (!cap.getRealmConditionFinished(1, 0)) {
                            cap.updateCondition(1, 0, true);
                            player.sendSystemMessage(Component.literal("首次打坐，感受到靈氣"));
                            player.sendSystemMessage(Component.literal("完成築基任務-獲取氣感"));
                        }
                    });
                } else {
                    player.sendSystemMessage(Component.translatable("message.realmmod.meditation.start_again"));
                    System.out.println("伺服器" + player + "已在冥想中"); // 除錯
                }
            }
        });
        context.setPacketHandled(true);
    }
}