package com.blacksnow1002.realmmod.spell.spells;

import com.blacksnow1002.realmmod.ModEntities;
import com.blacksnow1002.realmmod.capability.cultivation.CultivationRealm;
import com.blacksnow1002.realmmod.entity.PlayerCloneEntity;
import com.blacksnow1002.realmmod.handlers.utility.DelayedTaskHandler;
import com.blacksnow1002.realmmod.spell.BaseSpell;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;

public class CloneSpell extends BaseSpell {

    private static final int DURATION_TICKS = 20 * 30; // 30秒持續時間
    private static final double DAMAGE_MULTIPLIER = 0.5; // 分身造成50%傷害
    private static final double HEALTH_MULTIPLIER = 0.5; // 分身生命值為50%
    private static final double SPEED_MULTIPLIER = 2.5; // 分身速度為玩家的250%（更快）

    @Override
    public String getName() {
        return "身外化身";
    }

    @Override
    public CultivationRealm getRequiredRealm() {
        return CultivationRealm.eighth;
    }

    @Override
    public int getCooldownTicks() {
        return 20 * 45; // 45秒冷卻
    }

    @Override
    public boolean cast(ServerPlayer player, ServerLevel level) {
        // 創造分身
        PlayerCloneEntity clone = createClone(player, level);

        if (clone != null) {
            // 召喚特效
            spawnSummonEffects(player, level);

            // 音效
            level.playSound(null, player.blockPosition(), SoundEvents.ENDERMAN_TELEPORT,
                    SoundSource.PLAYERS, 1.0F, 0.8F);

            // 訊息提示
            player.sendSystemMessage(Component.translatable("message.realmmod.spell.clone.start"));

            // 30秒後移除分身
            DelayedTaskHandler.schedule(() -> removeClone(player, clone, level), DURATION_TICKS);

            return true;
        }

        return false;
    }

    /**
     * 創造玩家分身
     */
    private PlayerCloneEntity createClone(ServerPlayer player, ServerLevel level) {
        // 在玩家前方2格召喚分身
        Vec3 spawnPos = player.position().add(player.getLookAngle().scale(2.0));

        // 創建新的分身實體
        PlayerCloneEntity clone = new PlayerCloneEntity(ModEntities.PLAYER_CLONE.get(), level);
        clone.setPos(spawnPos.x, spawnPos.y, spawnPos.z);

        // 設置主人（這會自動複製玩家外觀）
        clone.setOwner(player);

        // 複製玩家裝備和屬性
        copyPlayerEquipment(player, clone);
        copyPlayerAttributes(player, clone);

        // 添加到世界
        if (level.addFreshEntity(clone)) {
            return clone;
        }

        return null;
    }

    /**
     * 複製玩家裝備
     */
    private void copyPlayerEquipment(ServerPlayer player, PlayerCloneEntity clone) {
        // 複製主手武器
        ItemStack mainHand = player.getMainHandItem().copy();
        clone.setItemSlot(EquipmentSlot.MAINHAND, mainHand);

        // 複製副手物品
        ItemStack offHand = player.getOffhandItem().copy();
        clone.setItemSlot(EquipmentSlot.OFFHAND, offHand);

        // 複製護甲
        clone.setItemSlot(EquipmentSlot.HEAD, player.getItemBySlot(EquipmentSlot.HEAD).copy());
        clone.setItemSlot(EquipmentSlot.CHEST, player.getItemBySlot(EquipmentSlot.CHEST).copy());
        clone.setItemSlot(EquipmentSlot.LEGS, player.getItemBySlot(EquipmentSlot.LEGS).copy());
        clone.setItemSlot(EquipmentSlot.FEET, player.getItemBySlot(EquipmentSlot.FEET).copy());

        // 防止裝備掉落
        for (EquipmentSlot slot : EquipmentSlot.values()) {
            clone.setDropChance(slot, 0.0F);
        }
    }

    /**
     * 複製玩家屬性
     */
    private void copyPlayerAttributes(ServerPlayer player, PlayerCloneEntity clone) {
        // 設置生命值為玩家的50%
        double maxHealth = player.getMaxHealth() * HEALTH_MULTIPLIER;
        clone.getAttribute(Attributes.MAX_HEALTH).setBaseValue(maxHealth);
        clone.setHealth((float) maxHealth);

        // 設置攻擊力為玩家的50%
        double attackDamage = player.getAttributeValue(Attributes.ATTACK_DAMAGE) * DAMAGE_MULTIPLIER;
        clone.getAttribute(Attributes.ATTACK_DAMAGE).setBaseValue(attackDamage);

        // 設置移動速度為玩家的120%（比玩家快一些）
        // 玩家基礎速度是 0.1D，乘以 1.2 = 0.12D（稍快於玩家行走）
        double moveSpeed = player.getAttributeValue(Attributes.MOVEMENT_SPEED) * SPEED_MULTIPLIER;
        clone.getAttribute(Attributes.MOVEMENT_SPEED).setBaseValue(moveSpeed);

        // 設置攻擊速度
        if (player.getAttribute(Attributes.ATTACK_SPEED) != null) {
            double attackSpeed = player.getAttributeValue(Attributes.ATTACK_SPEED);
            if (clone.getAttribute(Attributes.ATTACK_SPEED) != null) {
                clone.getAttribute(Attributes.ATTACK_SPEED).setBaseValue(attackSpeed);
            }
        }
    }

    /**
     * 移除分身
     */
    private void removeClone(ServerPlayer player, PlayerCloneEntity clone, ServerLevel level) {
        if (clone != null && clone.isAlive()) {
            // 消失特效
            spawnDespawnEffects(clone, level);

            // 音效
            level.playSound(null, clone.blockPosition(), SoundEvents.ENDERMAN_TELEPORT,
                    SoundSource.PLAYERS, 0.8F, 1.2F);

            // 移除實體
            clone.discard();

            player.sendSystemMessage(Component.translatable("message.realmmod.spell.clone.end"));
        }
    }

    /**
     * 召喚特效
     */
    private void spawnSummonEffects(ServerPlayer player, ServerLevel level) {
        Vec3 pos = player.position().add(player.getLookAngle().scale(2.0));

        // 螺旋粒子效果
        for (int i = 0; i < 50; i++) {
            double angle = (i / 50.0) * Math.PI * 4;
            double radius = 0.5 + (i / 50.0) * 1.5;
            double height = (i / 50.0) * 2.0;

            double x = pos.x + Math.cos(angle) * radius;
            double z = pos.z + Math.sin(angle) * radius;
            double y = pos.y + height;

            level.sendParticles(ParticleTypes.PORTAL, x, y, z, 1, 0, 0, 0, 0);
        }

        // 中心爆發
        for (int i = 0; i < 30; i++) {
            double angle = Math.random() * Math.PI * 2;
            double radius = Math.random() * 2.0;
            double x = pos.x + Math.cos(angle) * radius;
            double z = pos.z + Math.sin(angle) * radius;

            level.sendParticles(ParticleTypes.ENCHANT, x, pos.y + 1, z, 1, 0, 0.5, 0, 0.1);
        }

        // 額外的閃光效果
        level.sendParticles(ParticleTypes.END_ROD, pos.x, pos.y + 1, pos.z, 20, 0.5, 0.5, 0.5, 0.1);
    }

    /**
     * 消失特效
     */
    private void spawnDespawnEffects(PlayerCloneEntity clone, ServerLevel level) {
        Vec3 pos = clone.position();

        // 煙霧效果
        for (int i = 0; i < 40; i++) {
            double angle = Math.random() * Math.PI * 2;
            double radius = Math.random() * 1.5;
            double x = pos.x + Math.cos(angle) * radius;
            double z = pos.z + Math.sin(angle) * radius;
            double y = pos.y + Math.random() * 2.0;

            level.sendParticles(ParticleTypes.CLOUD, x, y, z, 1, 0, 0.2, 0, 0.05);
        }

        // 靈魂粒子效果
        for (int i = 0; i < 20; i++) {
            double offsetX = (Math.random() - 0.5) * 2.0;
            double offsetZ = (Math.random() - 0.5) * 2.0;
            double offsetY = Math.random() * 1.5;

            level.sendParticles(ParticleTypes.SOUL,
                    pos.x + offsetX, pos.y + offsetY, pos.z + offsetZ,
                    1, 0, 0.1, 0, 0.02);
        }
    }
}