package com.blacksnow1002.realmmod.spell.spells;

import com.blacksnow1002.realmmod.capability.cultivation.CultivationRealm;
import com.blacksnow1002.realmmod.event.handlers.DelayedTaskHandler;
import com.blacksnow1002.realmmod.spell.BaseSpell;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.phys.Vec3;

public class GiantSpell extends BaseSpell {

    // 屬性修改器的ResourceLocation（1.21.1使用ResourceLocation）
    private static final ResourceLocation HEALTH_MODIFIER_ID = ResourceLocation.fromNamespaceAndPath("realmmod", "giant_health_boost");
    private static final ResourceLocation SPEED_MODIFIER_ID = ResourceLocation.fromNamespaceAndPath("realmmod", "giant_speed_reduction");
    private static final ResourceLocation SCALE_MODIFIER_ID = ResourceLocation.fromNamespaceAndPath("realmmod", "giant_scale");

    private static final int DURATION_TICKS = 200; // 10秒持續時間
    private static final double HEALTH_BOOST = 40.0; // 增加20顆心
    private static final double SPEED_REDUCTION = -0.3; // 降低30%速度
    private static final double SCALE_MULTIPLIER = 3.0; // 3倍體型
    private static final double AOE_RANGE = 5.0; // 範圍攻擊半徑

    @Override
    public String getName() {
        return "法天象地";
    }

    @Override
    public CultivationRealm getRequiredRealm() {
        return CultivationRealm.eighth;
    }

    @Override
    public int getCooldownTicks() {
        return 20 * 30; // 30秒冷卻
    }

    @Override
    public boolean cast(ServerPlayer player, ServerLevel level) {
        // 啟動巨人形態
        activateGiantForm(player, level);

        // 10秒後取消巨人形態
        DelayedTaskHandler.schedule(() -> deactivateGiantForm(player, level), DURATION_TICKS);

        return true;
    }

    /**
     * 啟動巨人形態
     */
    private void activateGiantForm(ServerPlayer player, ServerLevel level) {
        // 1. 增加生命值上限
        var healthAttr = player.getAttribute(Attributes.MAX_HEALTH);
        if (healthAttr != null) {
            healthAttr.addTransientModifier(new AttributeModifier(
                    HEALTH_MODIFIER_ID,
                    HEALTH_BOOST,
                    AttributeModifier.Operation.ADD_VALUE
            ));
            // 恢復增加的生命值
            player.setHealth(player.getHealth() + (float) HEALTH_BOOST);
        }

        // 2. 降低移動速度
        var speedAttr = player.getAttribute(Attributes.MOVEMENT_SPEED);
        if (speedAttr != null) {
            speedAttr.addTransientModifier(new AttributeModifier(
                    SPEED_MODIFIER_ID,
                    SPEED_REDUCTION,
                    AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL
            ));
        }

        // 3. 增加體型（1.21.1 使用 SCALE 屬性）
        var scaleAttr = player.getAttribute(Attributes.SCALE);
        if (scaleAttr != null) {
            scaleAttr.addTransientModifier(new AttributeModifier(
                    SCALE_MODIFIER_ID,
                    SCALE_MULTIPLIER - 1.0, // 因為是乘法，所以減1
                    AttributeModifier.Operation.ADD_MULTIPLIED_BASE
            ));
        }

        // 4. 給予額外效果
        player.addEffect(new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, DURATION_TICKS, 1, false, true)); // 抗性提升2
        player.addEffect(new MobEffectInstance(MobEffects.DAMAGE_BOOST, DURATION_TICKS, 2, false, true)); // 力量3

        // 5. 視覺效果
        spawnTransformationEffects(player, level);

        // 6. 音效
        level.playSound(null, player.blockPosition(), SoundEvents.LIGHTNING_BOLT_THUNDER,
                SoundSource.PLAYERS, 1.0F, 0.7F);

        // 7. 訊息提示
        player.sendSystemMessage(Component.translatable("message.realmmod.spell.giant.start"));
    }

    /**
     * 取消巨人形態
     */
    private void deactivateGiantForm(ServerPlayer player, ServerLevel level) {
        if (player == null || !player.isAlive()) return;

        // 1. 移除生命值加成（先調整當前生命值，避免死亡）
        var healthAttr = player.getAttribute(Attributes.MAX_HEALTH);
        if (healthAttr != null) {
            float currentHealth = player.getHealth();
            float maxHealthAfterRemoval = (float) (healthAttr.getBaseValue());

            healthAttr.removeModifier(HEALTH_MODIFIER_ID);

            // 如果當前生命值超過新上限，調整為新上限
            if (currentHealth > maxHealthAfterRemoval) {
                player.setHealth(maxHealthAfterRemoval);
            }
        }

        // 2. 移除速度減益
        var speedAttr = player.getAttribute(Attributes.MOVEMENT_SPEED);
        if (speedAttr != null) {
            speedAttr.removeModifier(SPEED_MODIFIER_ID);
        }

        // 3. 移除體型加成
        var scaleAttr = player.getAttribute(Attributes.SCALE);
        if (scaleAttr != null) {
            scaleAttr.removeModifier(SCALE_MODIFIER_ID);
        }

        // 4. 視覺效果
        spawnDeactivationEffects(player, level);

        // 5. 音效
        level.playSound(null, player.blockPosition(), SoundEvents.LIGHTNING_BOLT_IMPACT,
                SoundSource.PLAYERS, 0.8F, 1.2F);

        // 6. 訊息提示
        player.sendSystemMessage(Component.translatable("message.realmmod.spell.giant.end"));
    }

    /**
     * 變身特效
     */
    private void spawnTransformationEffects(ServerPlayer player, ServerLevel level) {
        Vec3 pos = player.position();

        // 環繞粒子效果
        for (int i = 0; i < 100; i++) {
            double angle = Math.random() * Math.PI * 2;
            double radius = 2.0 + Math.random() * 2.0;
            double x = pos.x + Math.cos(angle) * radius;
            double z = pos.z + Math.sin(angle) * radius;
            double y = pos.y + Math.random() * 3.0;

            level.sendParticles(ParticleTypes.SOUL_FIRE_FLAME, x, y, z, 1,
                    0, 0.2, 0, 0.05);
            level.sendParticles(ParticleTypes.END_ROD, x, y, z, 1,
                    0, 0.1, 0, 0.02);
        }

        // 地面衝擊波
        for (int i = 0; i < 50; i++) {
            double angle = (i / 50.0) * Math.PI * 2;
            for (double r = 0.5; r <= 4.0; r += 0.5) {
                double x = pos.x + Math.cos(angle) * r;
                double z = pos.z + Math.sin(angle) * r;
                level.sendParticles(ParticleTypes.EXPLOSION, x, pos.y, z, 1, 0, 0, 0, 0);
            }
        }
    }

    /**
     * 解除特效
     */
    private void spawnDeactivationEffects(ServerPlayer player, ServerLevel level) {
        Vec3 pos = player.position();

        for (int i = 0; i < 50; i++) {
            double angle = Math.random() * Math.PI * 2;
            double radius = Math.random() * 2.0;
            double x = pos.x + Math.cos(angle) * radius;
            double z = pos.z + Math.sin(angle) * radius;
            double y = pos.y + Math.random() * 2.0;

            level.sendParticles(ParticleTypes.POOF, x, y, z, 1,
                    0, 0.1, 0, 0.05);
        }
    }

}