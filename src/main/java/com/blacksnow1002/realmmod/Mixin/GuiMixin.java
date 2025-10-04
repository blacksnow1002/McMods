package com.blacksnow1002.realmmod.Mixin;

import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.decoration.ArmorStand;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Gui.class)
public class GuiMixin {

    @Inject(method = "renderPlayerHealth", at = @At("HEAD"), cancellable = true)
    private void onRenderPlayerHealth(CallbackInfo ci) {
        Minecraft mc = Minecraft.getInstance();
        Player player = mc.player;

        if (player != null && player.getVehicle() instanceof ArmorStand armorStand) {
            if (armorStand.isInvisible()) {
                // 不取消，讓玩家血量正常顯示
            }
        }
    }

    @Inject(method = "renderVehicleHealth", at = @At("HEAD"), cancellable = true)
    private void onRenderVehicleHealth(CallbackInfo ci) {
        Minecraft mc = Minecraft.getInstance();
        Player player = mc.player;

        if (player != null && player.getVehicle() instanceof ArmorStand armorStand) {
            if (armorStand.isInvisible()) {
                ci.cancel();  // 取消坐騎血量渲染
            }
        }
    }



    @Inject(method = "render", at = @At("RETURN"))
    private void onRenderGui(GuiGraphics guiGraphics, DeltaTracker deltaTracker, CallbackInfo ci) {
        Minecraft mc = Minecraft.getInstance();
        Player player = mc.player;

        if (player != null && player.getVehicle() instanceof ArmorStand armorStand) {
            if (armorStand.isInvisible()) {
                var window = mc.getWindow();

                // 提示文字
                Component message = Component.literal("按 Shift 結束打坐");

                int width = mc.font.width(message);
                int x = (window.getGuiScaledWidth() - width) / 2;
                int y = window.getGuiScaledHeight() - 80;

                // 繪製文字
                guiGraphics.drawString(mc.font, message, x, y, 0xFFFFFF);
            }
        }
    }
}