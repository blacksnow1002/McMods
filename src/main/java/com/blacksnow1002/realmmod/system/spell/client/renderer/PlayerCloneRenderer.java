package com.blacksnow1002.realmmod.system.spell.client.renderer;

import com.blacksnow1002.realmmod.system.spell.entity.PlayerCloneEntity;
import com.mojang.authlib.GameProfile;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.HumanoidMobRenderer;
import net.minecraft.client.renderer.entity.layers.HumanoidArmorLayer;
import net.minecraft.client.renderer.entity.layers.ItemInHandLayer;
import net.minecraft.client.resources.DefaultPlayerSkin;
import net.minecraft.resources.ResourceLocation;

/**
 * 玩家分身渲染器 - 使用玩家皮膚和模型
 */
public class PlayerCloneRenderer extends HumanoidMobRenderer<PlayerCloneEntity, PlayerModel<PlayerCloneEntity>> {

    public PlayerCloneRenderer(EntityRendererProvider.Context context) {
        super(context, new PlayerModel<>(context.bakeLayer(ModelLayers.PLAYER), false), 0.5F);

        // 添加護甲層
        this.addLayer(new HumanoidArmorLayer<>(this,
                new HumanoidModel<>(context.bakeLayer(ModelLayers.PLAYER_INNER_ARMOR)),
                new HumanoidModel<>(context.bakeLayer(ModelLayers.PLAYER_OUTER_ARMOR)),
                context.getModelManager()));

        // 添加手持物品層
        this.addLayer(new ItemInHandLayer<>(this, context.getItemInHandRenderer()));
    }

    @Override
    public ResourceLocation getTextureLocation(PlayerCloneEntity entity) {
        GameProfile profile = entity.getGameProfile();

        if (profile != null) {
            System.out.println("[Renderer] 使用玩家皮膚: " + profile.getName());
            // 在 1.21.1 中，get() 返回 PlayerSkin 對象，需要調用 texture() 方法
            return DefaultPlayerSkin.get(profile.getId()).texture();
        }

        System.out.println("[Renderer] 警告：使用預設皮膚（GameProfile 為 null）");
        // 備用：使用預設皮膚（Steve）
        return DefaultPlayerSkin.getDefaultTexture();
    }

    @Override
    public void render(PlayerCloneEntity entity, float entityYaw, float partialTicks,
                       PoseStack poseStack, MultiBufferSource buffer, int packedLight) {

        // 根據裝備調整模型
        this.model.young = false;
        this.model.setAllVisible(true);

        // 設置模型部分的可見性（根據護甲）
        this.model.hat.visible = true;
        this.model.jacket.visible = true;
        this.model.leftPants.visible = true;
        this.model.rightPants.visible = true;
        this.model.leftSleeve.visible = true;
        this.model.rightSleeve.visible = true;

        super.render(entity, entityYaw, partialTicks, poseStack, buffer, packedLight);
    }

    @Override
    protected void scale(PlayerCloneEntity entity, PoseStack poseStack, float partialTickTime) {
        // 正常玩家大小
        poseStack.scale(0.9375F, 0.9375F, 0.9375F);
    }
}