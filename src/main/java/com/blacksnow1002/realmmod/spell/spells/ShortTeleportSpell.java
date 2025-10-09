package com.blacksnow1002.realmmod.spell.spells;

import com.blacksnow1002.realmmod.capability.cultivation.CultivationRealm;
import com.blacksnow1002.realmmod.spell.BaseSpell;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

public class ShortTeleportSpell extends BaseSpell {
    // 可傳送的最大距離（方塊）
    private static final double MAX_TELEPORT_DISTANCE = 10.0;

    @Override
    public String getName() {
        return "撕裂空間";
    }

    @Override
    public CultivationRealm getRequiredRealm() {
        return CultivationRealm.seventh;
    }

    @Override
    public int getCooldownTicks() {
        return 20 * 3; // 3秒冷卻（短距離傳送可以短一點）
    }

    @Override
    public boolean cast(ServerPlayer player, ServerLevel level) {
        // 獲取玩家視線方向
        Vec3 eyePos = player.getEyePosition(1.0F);
        Vec3 lookVec = player.getLookAngle();
        Vec3 targetPos = eyePos.add(lookVec.scale(MAX_TELEPORT_DISTANCE));

        // 射線檢測，找到視線碰到的方塊
        ClipContext context = new ClipContext(
                eyePos,
                targetPos,
                ClipContext.Block.COLLIDER,
                ClipContext.Fluid.NONE,
                player
        );

        BlockHitResult result = level.clip(context);
        Vec3 teleportPos;

        if (result.getType() == HitResult.Type.BLOCK) {
            // 碰到方塊：傳送到方塊前方
            BlockPos hitBlockPos = result.getBlockPos();
            BlockPos frontPos = hitBlockPos.relative(result.getDirection());
            teleportPos = new Vec3(frontPos.getX() + 0.5, frontPos.getY(), frontPos.getZ() + 0.5);
        } else {
            // 沒碰到方塊：傳送到視線最遠處
            teleportPos = targetPos;
        }

        // 記錄起始位置（用於粒子效果）
        Vec3 startPos = player.position();

        // 起點粒子效果
        spawnTeleportParticles(level, startPos);

        // 起點音效
        level.playSound(null, player.blockPosition(),
                SoundEvents.ENDERMAN_TELEPORT,
                SoundSource.PLAYERS, 1.0F, 1.0F);

        // 執行傳送
        player.teleportTo(teleportPos.x, teleportPos.y, teleportPos.z);

        // 終點粒子效果
        spawnTeleportParticles(level, teleportPos);

        // 終點音效
        level.playSound(null, BlockPos.containing(teleportPos),
                SoundEvents.ENDERMAN_TELEPORT,
                SoundSource.PLAYERS, 1.0F, 1.2F);

        player.displayClientMessage(Component.translatable("message.realmmod.spell.short_teleport_spell.success"), true);
        return true; // 成功
    }

    /**
     * 生成傳送粒子效果
     */
    private void spawnTeleportParticles(ServerLevel level, Vec3 pos) {
        // 螺旋上升的粒子效果
        for (int i = 0; i < 30; i++) {
            double angle = i * 0.4;
            double radius = 0.5;
            double offsetX = Math.cos(angle) * radius;
            double offsetZ = Math.sin(angle) * radius;
            double offsetY = i * 0.1;

            level.sendParticles(ParticleTypes.PORTAL,
                    pos.x + offsetX,
                    pos.y + offsetY,
                    pos.z + offsetZ,
                    1, // 粒子數量
                    0, 0, 0, // 速度
                    0); // 額外速度
        }

        // 周圍隨機粒子
        for (int i = 0; i < 20; i++) {
            level.sendParticles(ParticleTypes.REVERSE_PORTAL,
                    pos.x + (level.random.nextDouble() - 0.5) * 2,
                    pos.y + level.random.nextDouble() * 2,
                    pos.z + (level.random.nextDouble() - 0.5) * 2,
                    1,
                    0, 0, 0,
                    0.05);
        }
    }
}