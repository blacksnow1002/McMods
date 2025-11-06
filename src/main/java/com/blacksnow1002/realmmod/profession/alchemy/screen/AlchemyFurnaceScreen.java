package com.blacksnow1002.realmmod.profession.alchemy.screen;

import com.blacksnow1002.realmmod.RealmMod;
import com.blacksnow1002.realmmod.network.ModMessages;
import com.blacksnow1002.realmmod.profession.alchemy.network.C2S.StartAlchemyPacket;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

public class AlchemyFurnaceScreen extends AbstractContainerScreen<AlchemyFurnaceMenu> {
    private static final ResourceLocation TEXTURE =
            ResourceLocation.fromNamespaceAndPath(RealmMod.MOD_ID, "textures/gui/alchemy_furnace.png");

    private Button startButton;

    public AlchemyFurnaceScreen(AlchemyFurnaceMenu menu, Inventory playerInv, Component title) {
        super(menu, playerInv, title);
        this.imageHeight = 166;
        this.imageWidth = 176;
        this.inventoryLabelY = this.imageHeight - 94;
    }

    @Override
    protected void init() {
        super.init();

        // 添加"開始煉丹"按鈕
        // 按鈕位置根據你的GUI調整
        int buttonX = this.leftPos + 60;
        int buttonY = this.topPos + 60;
        int buttonWidth = 56;
        int buttonHeight = 12;

        this.startButton = Button.builder(
                        Component.literal("開始煉丹"),
                        button -> onStartAlchemyClick())
                .bounds(buttonX, buttonY, buttonWidth, buttonHeight)
                .build();

        this.addRenderableWidget(this.startButton);
    }

    private void onStartAlchemyClick() {
        // 發送數據包到服務器執行煉丹邏輯
        ModMessages.sendToServer(new StartAlchemyPacket(this.menu.getBlockEntity().getBlockPos(), this.menu.getItems()));
    }

    @Override
    protected void renderBg(GuiGraphics graphics, float partialTick, int mouseX, int mouseY) {
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.setShaderTexture(0, TEXTURE);

        int x = (this.width - this.imageWidth) / 2;
        int y = (this.height - this.imageHeight) / 2;

        graphics.blit(TEXTURE, x, y, 0, 0, this.imageWidth, this.imageHeight);
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        this.renderBackground(graphics, mouseX, mouseY, partialTick);
        super.render(graphics, mouseX, mouseY, partialTick);
        this.renderTooltip(graphics, mouseX, mouseY);
    }

    @Override
    protected void renderLabels(GuiGraphics graphics, int mouseX, int mouseY) {
        // 繪製標題
        graphics.drawString(this.font, this.title, this.titleLabelX, this.titleLabelY, 4210752, false);
        // 如果需要的話可以不繪製背包標題
        // graphics.drawString(this.font, this.playerInventoryTitle, this.inventoryLabelX, this.inventoryLabelY, 4210752, false);
    }
}