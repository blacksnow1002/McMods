package com.blacksnow1002.realmmod.broadcast;

import com.blacksnow1002.realmmod.broadcast.util.ColoredText;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.LinkedList;
import java.util.Queue;

@Mod.EventBusSubscriber(value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class ClientBroadcastHandler {
    private static final Queue<BroadcastData> messageQueue = new LinkedList<>();
    private static BroadcastData currentMessage = null;
    private static int currentTicks = 0;

    // 位置和樣式參數
    private static final float Y_POSITION = 10;
    private static final float SCALE = 1.0f;
    private static final boolean SHOW_SHADOW = true;
    private static final boolean CENTER_ALIGN = true;

    public static void addMessage(String message, int duration) {
        messageQueue.offer(new BroadcastData(message, duration));
    }

    @SubscribeEvent
    public static void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase != TickEvent.Phase.END) return;

        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null) return;

        // 更新當前訊息狀態
        if (currentMessage != null) {
            currentTicks++;

            if (currentTicks >= currentMessage.getDuration()) {
                currentMessage = null;
                currentTicks = 0;
            }
        } else if (!messageQueue.isEmpty()) {
            currentMessage = messageQueue.poll();
            currentTicks = 0;
        }
    }

    // 這個方法會在你的客戶端主類或渲染事件中被調用
    public static void render(GuiGraphics guiGraphics) {
        if (currentMessage == null) return;

        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null) return;

        renderBroadcast(guiGraphics, mc, currentMessage);
    }

    private static void renderBroadcast(GuiGraphics guiGraphics, Minecraft mc, BroadcastData data) {
        Font font = mc.font;
        PoseStack poseStack = guiGraphics.pose();

        int screenWidth = mc.getWindow().getGuiScaledWidth();

        // 計算總寬度
        int totalWidth = 0;
        for (ColoredText.TextSegment segment : data.getColoredText().getSegments()) {
            totalWidth += font.width(segment.text);
        }

        float x = CENTER_ALIGN ? (screenWidth - totalWidth * SCALE) / 2 : 10;
        float y = Y_POSITION;

        // 計算透明度
        float alpha = 1.0f;
        int fadeOutDuration = 20;

        if (currentTicks > data.getDuration() - fadeOutDuration) {
            // Fade out 階段
            alpha = (data.getDuration() - currentTicks) / (float) fadeOutDuration;
        }

        poseStack.pushPose();
        poseStack.translate(x, y, 0);
        poseStack.scale(SCALE, SCALE, 1.0f);

        // 渲染每個顏色片段
        float currentX = 0;
        for (ColoredText.TextSegment segment : data.getColoredText().getSegments()) {
            int colorWithAlpha = (Math.min(255, (int)(alpha * 255)) << 24) | (segment.color & 0xFFFFFF);
            guiGraphics.drawString(font, segment.text, (int)currentX, 0, colorWithAlpha, SHOW_SHADOW);
            currentX += font.width(segment.text);
        }

        poseStack.popPose();
    }
}