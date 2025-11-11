package com.blacksnow1002.realmmod.client.core.mixin;

import com.blacksnow1002.realmmod.system.cultivation.CultivationRealm;
import com.blacksnow1002.realmmod.common.capability.ModCapabilities;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodData;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(FoodData.class)
public class FoodDataMixin {

    @Shadow
    private int foodLevel;

    @Shadow
    private float saturationLevel;

    /**
     * 攔截飽食度更新,阻止辟穀狀態下的消耗
     */
    @Inject(method = "tick", at = @At("HEAD"), cancellable = true)
    private void onTick(Player player, CallbackInfo ci) {
        player.getCapability(ModCapabilities.CULTIVATION_CAP).ifPresent(cap -> {
            if(cap.getRealm().ordinal() > CultivationRealm.second.ordinal()) {
                this.foodLevel = 20;
                this.saturationLevel = 5.0F;

                // 取消原版的飽食度消耗邏輯
                ci.cancel();
            }
        });
    }
}