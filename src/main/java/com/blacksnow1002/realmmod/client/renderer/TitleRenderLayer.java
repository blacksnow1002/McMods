package com.blacksnow1002.realmmod.client.renderer;

import com.blacksnow1002.realmmod.client.cache.ClientTitleCache;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.entity.player.PlayerRenderer;
import net.minecraft.resources.ResourceLocation;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.RenderType;
import org.joml.Matrix4f;

public class TitleRenderLayer extends RenderLayer<AbstractClientPlayer, PlayerModel<AbstractClientPlayer>> {
    private static final float RENDER_DISTANCE = 32.0F;

    public TitleRenderLayer(PlayerRenderer renderer) {
        super(renderer);
    }

    @Override
    public void render(PoseStack poseStack, MultiBufferSource buffer, int light, AbstractClientPlayer player,
                       float limbSwing, float limbSwingAmount, float partialTicks,
                       float ageInTicks, float netHeadYaw, float headPitch) {

        // 計算與客戶端玩家的距離
        Minecraft mc = Minecraft.getInstance();
        if (mc.player != null && player.distanceToSqr(mc.player) > RENDER_DISTANCE * RENDER_DISTANCE) {
            return;
        }

        String texturePath = ClientTitleCache.getTexturePath(player.getUUID());
        if (texturePath == null || texturePath.isEmpty() || texturePath.equals("realmmod:textures/title/.png")) {
            return;
        }

        try {
            ResourceLocation texture = ClientTitleCache.getTexture(player.getUUID());

            poseStack.pushPose();

            // 獲取玩家模型的頭部位置
            // 先移動到身體中心,再往上移動到頭頂上方
            this.getParentModel().getHead().translateAndRotate(poseStack);

            // 在頭部上方額外偏移
            poseStack.translate(0.0D, -0.35D, 0.0D);

            // 讓稱號始終面向攝影機
            poseStack.mulPose(mc.getEntityRenderDispatcher().cameraOrientation());

            // 縮放 (注意 Y 軸是負的,所以圖片是正向的)
            poseStack.scale(-0.025F, -0.025F, 0.025F);

            Matrix4f matrix = poseStack.last().pose();

            // 使用帶透明度的 RenderType,並且禁用背面剔除
            VertexConsumer vertexConsumer = buffer.getBuffer(RenderType.text(texture));

            // 繪製矩形(稱號圖片) - 調整寬高比以符合實際圖片
            // 根據你的圖片,看起來應該是寬度比高度大很多
            float width = 60.0F;  // 增加寬度
            float height = 40.0F; // 增加高度,保持比例
            float x = -width / 2;
            float y = 0;

            // 繪製正面 (逆時針順序,修正 UV 使圖片正向且左右正確)
            vertexConsumer.addVertex(matrix, x, y + height, 0)
                    .setColor(255, 255, 255, 255)
                    .setUv(1, 0)
                    .setLight(light);

            vertexConsumer.addVertex(matrix, x + width, y + height, 0)
                    .setColor(255, 255, 255, 255)
                    .setUv(0, 0)
                    .setLight(light);

            vertexConsumer.addVertex(matrix, x + width, y, 0)
                    .setColor(255, 255, 255, 255)
                    .setUv(0, 1)
                    .setLight(light);

            vertexConsumer.addVertex(matrix, x, y, 0)
                    .setColor(255, 255, 255, 255)
                    .setUv(1, 1)
                    .setLight(light);

            // 繪製背面 (順時針順序,使兩面都可見)
            vertexConsumer.addVertex(matrix, x, y, 0)
                    .setColor(255, 255, 255, 255)
                    .setUv(0, 1)
                    .setLight(light);

            vertexConsumer.addVertex(matrix, x + width, y, 0)
                    .setColor(255, 255, 255, 255)
                    .setUv(1, 1)
                    .setLight(light);

            vertexConsumer.addVertex(matrix, x + width, y + height, 0)
                    .setColor(255, 255, 255, 255)
                    .setUv(1, 0)
                    .setLight(light);

            vertexConsumer.addVertex(matrix, x, y + height, 0)
                    .setColor(255, 255, 255, 255)
                    .setUv(0, 0)
                    .setLight(light);

            poseStack.popPose();
        } catch (Exception e) {
            // 靜默處理資源載入錯誤
            // 可選: 用於調試
            e.printStackTrace();
        }
    }
}