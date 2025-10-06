package com.blacksnow1002.realmmod.spell;

import com.blacksnow1002.realmmod.capability.ModCapabilities;
import com.blacksnow1002.realmmod.capability.CultivationRealm;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.network.chat.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public abstract class BaseSpell {

    // 🔹 記錄玩家的冷卻時間：<玩家UUID, <法術名稱, 剩餘tick>>
    private static final Map<UUID, Map<String, Integer>> cooldowns = new HashMap<>();

    public abstract String getName();
    public abstract CultivationRealm getRequiredRealm();
    public abstract int getCooldownTicks();
    public abstract boolean cast(ServerPlayer player, ServerLevel level);
    public boolean canCancelEarly(ServerPlayer player) {
        return false; // 預設不允許
    }

    public void tryCast(ServerPlayer player) {
        player.getCapability(ModCapabilities.CULTIVATION_CAP).ifPresent(cap -> {

            // 1️⃣ 境界檢查
            if (cap.getRealm().ordinal() < getRequiredRealm().ordinal()) {
                player.displayClientMessage(Component.translatable(
                        "message.realmmod.spell.common.low_ordinal",
                        getName()),
                        true);
                return;
            }

            // 2️⃣ 冷卻檢查
            if (canCancelEarly(player)) {
                cast(player, (ServerLevel) player.level());
                return;
            }

            if (isOnCooldown(player)) {
                int remainingTicks = getRemainingCooldown(player);
                player.displayClientMessage(Component.translatable(
                        "message.realmmod.spell.common.cold_time" ,
                        getName(),
                        (remainingTicks / 20)),
                        true);
                return;
            }

            // 3️⃣ 消耗靈氣（未來可加入 cap.addQi(-getQiCost())）

            // 4️⃣ 執行施法
            boolean success = cast(player, (ServerLevel) player.level());


            // 5️⃣ 加入冷卻
            if(success) {
                setCooldown(player, getCooldownTicks());

            }
        });
    }

    // --- 🕒 以下是冷卻邏輯 ---

    public static void tickAllCooldowns() {
        cooldowns.values().removeIf(playerCooldowns -> {
            // 減少冷卻時間
            playerCooldowns.entrySet().removeIf(entry -> {
                int newValue = entry.getValue() - 1;
                if (newValue <= 0) {
                    return true; // 冷卻結束，移除
                }
                entry.setValue(newValue);
                return false;
            });
            // 如果該玩家沒有任何冷卻，移除整個玩家記錄
            return playerCooldowns.isEmpty();
        });
    }

    private boolean isOnCooldown(ServerPlayer player) {
        Map<String, Integer> playerCooldowns = cooldowns.get(player.getUUID());
        if (playerCooldowns == null) {
            return false;
        }
        return playerCooldowns.getOrDefault(getName(), 0) > 0;
    }

    private int getRemainingCooldown(ServerPlayer player) {
        Map<String, Integer> playerCooldowns = cooldowns.get(player.getUUID());
        if (playerCooldowns == null) {
            return 0;
        }
        return playerCooldowns.getOrDefault(getName(), 0);
    }

    private void setCooldown(ServerPlayer player, int ticks) {
        cooldowns.computeIfAbsent(player.getUUID(), k -> new HashMap<>())
                .put(getName(), ticks);
    }
}