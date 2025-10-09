package com.blacksnow1002.realmmod.spell.spells;

import com.blacksnow1002.realmmod.capability.cultivation.CultivationRealm;
import com.blacksnow1002.realmmod.spell.BaseSpell;
import com.mojang.authlib.GameProfile;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.decoration.ArmorStand;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.component.ResolvableProfile;
import net.minecraft.world.level.GameType;
import net.minecraft.world.phys.Vec3;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class SpiritOutSpell extends BaseSpell {

    // 儲存所有玩家的出竅數據
    private static final Map<UUID, SpiritOutData> activeSpiritOuts = new HashMap<>();

    private static final double MAX_SPIRIT_RANGE = 50.0D;
    private static final int DURATION_TICKS = 20 * 30; // 30秒

    @Override
    public String getName() {
        return "元嬰出竅";
    }

    @Override
    public CultivationRealm getRequiredRealm() {
        return CultivationRealm.fifth;
    }

    @Override
    public boolean canCancelEarly(ServerPlayer player) {
        return activeSpiritOuts.containsKey(player.getUUID());
    }

    @Override
    public int getCooldownTicks() {
        return 20 * 35;
    }

    @Override
    public boolean cast(ServerPlayer player, ServerLevel level) {
        UUID playerId = player.getUUID();

        // 如果已經在出竅狀態，則返回肉身（手動結束）
        if (activeSpiritOuts.containsKey(playerId)) {
            returnToBody(player, level);
            player.sendSystemMessage(Component.translatable("message.realmmod.spell.spirit_out.end_fast"));
            return false;
        }

        Vec3 bodyPos = player.position();
        GameType originalGameMode = player.gameMode.getGameModeForPlayer();

        // 創建肉身
        ArmorStand bodyStand = createBodyEntity(player, level, bodyPos);

        // 儲存出竅數據
        SpiritOutData data = new SpiritOutData(
                bodyPos,
                bodyStand,
                originalGameMode,
                level.getGameTime() + DURATION_TICKS
        );
        activeSpiritOuts.put(playerId, data);

        // 進入靈魂狀態
        player.setGameMode(GameType.SPECTATOR);
        player.setInvulnerable(true);
        player.setGlowingTag(true);

        player.sendSystemMessage(Component.translatable("message.realmmod.spell.spirit_out.start"));
        player.sendSystemMessage(Component.translatable("message.realmmod.spell.spirit_out.limitation_message",
                (int)MAX_SPIRIT_RANGE,
                DURATION_TICKS/20));
        player.sendSystemMessage(Component.translatable("message.realmmod.spell.spirit_out.back_message"));

        return true;
    }

    /**
     * 返回肉身（內部版本，不從 Map 中移除）
     */
    private static void returnToBodyInternal(ServerPlayer player, ServerLevel level, SpiritOutData data) {
        // 先恢復遊戲模式（必須在傳送前）
        player.setGameMode(data.originalGameMode);
        player.setInvulnerable(false);
        player.setGlowingTag(false);

        // 安全傳送回肉身
        try {
            player.teleportTo(level, data.bodyPos.x, data.bodyPos.y, data.bodyPos.z,
                    player.getYRot(), player.getXRot());
        } catch (Exception e) {
            // 備用傳送方法
            player.moveTo(data.bodyPos.x, data.bodyPos.y, data.bodyPos.z);
        }

        // 移除肉身實體
        if (data.bodyEntity != null && data.bodyEntity.isAlive()) {
            data.bodyEntity.remove(Entity.RemovalReason.DISCARDED);
        }

        player.sendSystemMessage(Component.translatable("message.realmmod.spell.spirit_out.end"));
    }

    /**
     * 返回肉身（公開版本，用於手動調用）
     */
    public static void returnToBody(ServerPlayer player, ServerLevel level) {
        UUID playerId = player.getUUID();
        SpiritOutData data = activeSpiritOuts.remove(playerId);

        if (data == null) return;

        returnToBodyInternal(player, level, data);
    }

    /**
     * 每 tick 檢查所有出竅玩家（在 ServerTickEvent 中調用）
     */
    public static void tickSpiritOuts(ServerLevel level) {
        activeSpiritOuts.entrySet().removeIf(entry -> {
            UUID playerId = entry.getKey();
            SpiritOutData data = entry.getValue();
            ServerPlayer player = level.getServer().getPlayerList().getPlayer(playerId);

            // 玩家不存在或已離線 - 清理數據
            if (player == null || !player.isAlive()) {
                if (data.bodyEntity != null && data.bodyEntity.isAlive()) {
                    data.bodyEntity.remove(Entity.RemovalReason.DISCARDED);
                }
                return true;
            }

            // 檢查玩家是否在當前維度
            if (player.level() != level) {
                return false; // 玩家在其他維度，跳過
            }

            // 檢查時間是否到期
            if (level.getGameTime() >= data.endTime) {
                returnToBodyInternal(player, level, data);
                player.sendSystemMessage(Component.translatable("message.realmmod.spell.spirit_out.times_up"));
                return true;
            }

            // 檢查距離是否超出範圍
            double distance = player.position().distanceTo(data.bodyPos);
            if (distance > MAX_SPIRIT_RANGE) {
                returnToBodyInternal(player, level, data);
                player.sendSystemMessage(Component.translatable("message.realmmod.spell.spirit_out.distance"));
                return true;
            }

            // 檢查肉身是否被破壞
            if (data.bodyEntity == null || !data.bodyEntity.isAlive()) {
                returnToBodyInternal(player, level, data);
                player.sendSystemMessage(Component.translatable("message.realmmod.spell.spirit_out.attacked"));
                return true;
            }


            long ticksRemaining = data.endTime - level.getGameTime();
            int secondsRemaining = (int)(ticksRemaining / 20);
            player.displayClientMessage(
                    Component.translatable(
                            distance > MAX_SPIRIT_RANGE ?"message.realmmod.spell.spirit_out.remind.big" : "message.realmmod.spell.spirit_out.remind.small" ,
                            secondsRemaining,
                            (int)distance,
                            (int)MAX_SPIRIT_RANGE
                    ),
                    true // true = ActionBar, false = 聊天框
            );

            return false;
        });
    }

    /**
     * 玩家登出時清理
     */
    public static void onPlayerLogout(ServerPlayer player) {
        UUID playerId = player.getUUID();
        if (activeSpiritOuts.containsKey(playerId)) {
            SpiritOutData data = activeSpiritOuts.remove(playerId);

            player.setGameMode(data.originalGameMode);
            player.setInvulnerable(false);
            player.setGlowingTag(false);

            if (data.bodyEntity != null && data.bodyEntity.isAlive()) {
                data.bodyEntity.remove(Entity.RemovalReason.DISCARDED);
            }
        }
    }

    /**
     * 創建肉身實體
     */
    private ArmorStand createBodyEntity(ServerPlayer player, ServerLevel level, Vec3 bodyPos) {
        ArmorStand bodyStand = new ArmorStand(EntityType.ARMOR_STAND, level);
        bodyStand.setPos(bodyPos.x, bodyPos.y, bodyPos.z);
        bodyStand.setYRot(player.getYRot());
        bodyStand.setXRot(player.getXRot());

        bodyStand.setCustomName(Component.translatable(
                "message.realmmod.spell.spirit_out.armor_stand.name",
                player.getDisplayName().getString()
        ));
        bodyStand.setCustomNameVisible(true);
        bodyStand.setNoGravity(true);
        bodyStand.setInvulnerable(true);
        bodyStand.setShowArms(true);
        bodyStand.setPose(Pose.SITTING);

        // ✨ 複製玩家裝備到盔甲座
        copyPlayerEquipment(player, bodyStand);

        // 🔒 防止物品被拿走 - 使用 NBT 設置禁用槽位
        disableAllSlots(bodyStand);

        level.addFreshEntity(bodyStand);
        return bodyStand;
    }

    /**
     * 禁用盔甲座的所有裝備槽位（防止物品被拿走）
     */
    private void disableAllSlots(ArmorStand armorStand) {
        CompoundTag nbt = new CompoundTag();
        armorStand.saveWithoutId(nbt);

        // 設置 DisabledSlots 標籤 (所有槽位都禁用)
        // 二進制: 0x3F3F3F = 4144959
        nbt.putInt("DisabledSlots", 4144959);

        armorStand.load(nbt);
    }

    /**
     * 複製玩家的裝備、頭顱和手持物品到盔甲座
     */
    private void copyPlayerEquipment(ServerPlayer player, ArmorStand bodyStand) {

        ItemStack playerHead = new ItemStack(Items.PLAYER_HEAD);
        GameProfile profile = new GameProfile(player.getUUID(), player.getName().getString());
        // 正確使用 DataComponents.PROFILE
        playerHead.set(DataComponents.PROFILE, new ResolvableProfile(profile));
        bodyStand.setItemSlot(EquipmentSlot.HEAD, playerHead);


        // 複製胸甲
        ItemStack chestplate = player.getInventory().getArmor(2);
        if (!chestplate.isEmpty()) {
            bodyStand.setItemSlot(EquipmentSlot.CHEST, chestplate.copy());
        }

        // 複製護腿
        ItemStack leggings = player.getInventory().getArmor(1);
        if (!leggings.isEmpty()) {
            bodyStand.setItemSlot(EquipmentSlot.LEGS, leggings.copy());
        }

        // 複製靴子
        ItemStack boots = player.getInventory().getArmor(0);
        if (!boots.isEmpty()) {
            bodyStand.setItemSlot(EquipmentSlot.FEET, boots.copy());
        }

        // 複製主手物品
        ItemStack mainHand = player.getMainHandItem();
        if (!mainHand.isEmpty()) {
            bodyStand.setItemSlot(EquipmentSlot.MAINHAND, mainHand.copy());
        }

        // 複製副手物品
        ItemStack offHand = player.getOffhandItem();
        if (!offHand.isEmpty()) {
            bodyStand.setItemSlot(EquipmentSlot.OFFHAND, offHand.copy());
        }
    }

    /**
     * 儲存出竅數據的內部類
     */
    private static class SpiritOutData {
        final Vec3 bodyPos;
        final ArmorStand bodyEntity;
        final GameType originalGameMode;
        final long endTime;

        SpiritOutData(Vec3 bodyPos, ArmorStand bodyEntity, GameType originalGameMode, long endTime) {
            this.bodyPos = bodyPos;
            this.bodyEntity = bodyEntity;
            this.originalGameMode = originalGameMode;
            this.endTime = endTime;
        }
    }
}