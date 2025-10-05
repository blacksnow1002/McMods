package com.blacksnow1002.realmmod.network.packets;

import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.event.network.CustomPayloadEvent;

public class LingMuSyncPacket {
    private final boolean isActive;

    public LingMuSyncPacket(boolean isActive) {
        this.isActive = isActive;
    }

    public LingMuSyncPacket(FriendlyByteBuf buf) {
        this.isActive = buf.readBoolean();
    }

    public void encode(FriendlyByteBuf buf) {
        buf.writeBoolean(this.isActive);
    }

    public void handle(CustomPayloadEvent.Context ctx) {
        ctx.enqueueWork(() -> {
            Minecraft mc = Minecraft.getInstance();
            if (mc.player != null && mc.level != null) {
                System.out.println("客戶端收到 LingMuSyncPacket: isActive = " + this.isActive);
                mc.player.getPersistentData().putBoolean("LingMuActive", this.isActive);
                System.out.println("客戶端 NBT 已更新: LingMuActive = " + mc.player.getPersistentData().getBoolean("LingMuActive"));

                // 強制重新渲染周圍的區塊
                BlockPos playerPos = mc.player.blockPosition();
                int radius = 32; // 渲染半徑(區塊)

                for (int x = -radius; x <= radius; x++) {
                    for (int y = -radius; y <= radius; y++) {
                        for (int z = -radius; z <= radius; z++) {
                            BlockPos pos = playerPos.offset(x, y, z);
                            mc.level.sendBlockUpdated(pos, mc.level.getBlockState(pos), mc.level.getBlockState(pos), 3);
                        }
                    }
                }

                // 或者使用更簡單的方法:強制重新渲染所有區塊
                if (mc.levelRenderer != null) {
                    mc.levelRenderer.allChanged();
                }
            }
        });
        ctx.setPacketHandled(true);
    }
}