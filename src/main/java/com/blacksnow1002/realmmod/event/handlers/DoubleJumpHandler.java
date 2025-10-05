package com.blacksnow1002.realmmod.event.handlers;

import com.blacksnow1002.realmmod.capability.CultivationRealm;
import com.blacksnow1002.realmmod.capability.ModCapabilities;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
public class DoubleJumpHandler {
    private static boolean hasDoubleJumped = false;  // 是否已經使用了二段跳
    private static boolean wasJumping = false;       // 上一個 tick 是否在按跳躍鍵
    private static boolean wasOnGround = true;       // 上一個 tick 是否在地面

    @SubscribeEvent
    public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
        if (event.player == null || !event.player.isAlive()) return;
        if (event.phase != TickEvent.Phase.END) return;
        if (!(event.player instanceof LocalPlayer player)) return;
        if (!player.level().isClientSide) return;

        CompoundTag data = player.getPersistentData();
        boolean allow = data.getInt("RealmOrdinal") > CultivationRealm.second.ordinal();

        if (allow) {
            boolean isJumping = player.input.jumping;
            boolean isOnGround = player.onGround();

            // 調試信息
            if (isJumping && !wasJumping) {
                player.displayClientMessage(
                        Component.literal("§e跳躍按下 | 在地面: " + isOnGround +
                                " | 已二段跳: " + hasDoubleJumped +
                                " | Y速度: " + String.format("%.2f", player.getDeltaMovement().y)),
                        true
                );
            }

            // 離開地面時重置二段跳標記
            if (wasOnGround && !isOnGround) {
                hasDoubleJumped = false;
            }

            // 在地面上時重置二段跳
            if (isOnGround) {
                hasDoubleJumped = false;
            }

            // 二段跳觸發條件：
            // 1. 當前按下跳躍鍵 && 上一tick沒按（邊緣觸發）
            // 2. 不在地面上
            // 3. 還沒有使用過二段跳
            // 4. 玩家正在下落（或上升速度很慢）
            if (isJumping && !wasJumping && !isOnGround && !hasDoubleJumped) {
                // 額外檢查：確保玩家真的在空中（不是剛離開地面）
                if (!wasOnGround) {
                    // 執行二段跳
                    player.setDeltaMovement(
                            player.getDeltaMovement().x,
                            0.42D,  // 標準跳躍速度
                            player.getDeltaMovement().z
                    );

                    hasDoubleJumped = true;

                    player.displayClientMessage(
                            Component.literal("§6§l二段跳發動！"),
                            true
                    );

                    System.out.println(">>> 二段跳執行成功！");
                }
            }

            // 更新狀態
            wasJumping = isJumping;
            wasOnGround = isOnGround;
        }
    }
}