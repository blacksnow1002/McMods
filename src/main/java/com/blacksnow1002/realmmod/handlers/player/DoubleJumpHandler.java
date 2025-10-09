package com.blacksnow1002.realmmod.handlers.player;

import com.blacksnow1002.realmmod.capability.cultivation.CultivationRealm;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
public class DoubleJumpHandler {
    private static final Map<UUID, PlayerJumpState> playerStates = new HashMap<>();

    private static class PlayerJumpState {
        boolean hasDoubleJumped = false;
        boolean wasJumping = false;
        boolean wasOnGround = true;
        int dimensionId = 0;
    }

    @SubscribeEvent
    public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
        if (!(event.player instanceof LocalPlayer player)) return;
        if (event.phase != TickEvent.Phase.END) return;
        if (!player.level().isClientSide) return;
        if (!player.isAlive()) return;

        UUID uuid = player.getUUID();
        PlayerJumpState state = playerStates.computeIfAbsent(uuid, k -> new PlayerJumpState());

        // 🔹 維度切換 → 重置狀態
        int currentDim = player.level().dimension().location().hashCode();
        if (state.dimensionId != currentDim) {
            state.dimensionId = currentDim;
            state.hasDoubleJumped = false;
            state.wasOnGround = true;
            state.wasJumping = false;
        }

        CompoundTag data = player.getPersistentData();
        boolean allow = data.getInt("RealmOrdinal") > CultivationRealm.second.ordinal();
        if (!allow)  { return; }

        boolean isJumping = player.input.jumping;
        boolean isOnGround = player.onGround();

        // 🔹 離開地面 → 重置二段跳
        if (state.wasOnGround && !isOnGround) {
            state.hasDoubleJumped = false;
        }

        // 🔹 在地面上時重置二段跳
        if (isOnGround) {
            state.hasDoubleJumped = false;
        }

        // 🔹 二段跳條件判斷
        if (isJumping && !state.wasJumping && !isOnGround && !state.hasDoubleJumped) {
            if (!state.wasOnGround) {
                player.setDeltaMovement(
                        player.getDeltaMovement().x,
                        0.42D, // 標準跳躍速度
                        player.getDeltaMovement().z
                );

                state.hasDoubleJumped = true;

                player.displayClientMessage(
                        Component.translatable("message.realmmod.two_jump_success"),
                        true
                );
            }
        }

        // 🔹 更新狀態
        state.wasJumping = isJumping;
        state.wasOnGround = isOnGround;
    }
}
