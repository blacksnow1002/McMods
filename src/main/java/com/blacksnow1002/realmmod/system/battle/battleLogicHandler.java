package com.blacksnow1002.realmmod.system.battle;

import com.blacksnow1002.realmmod.common.capability.ModCapabilities;
import com.blacksnow1002.realmmod.system.battle.attribute.capability.IPlayerTotalAttributeData;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.event.entity.living.LivingDamageEvent;

import java.util.concurrent.ThreadLocalRandom;


// TODO: 修改標籤屬性
public class battleLogicHandler {
    private static final String REFLECT_DAMAGE_TAG = "ReflectDamage";

//    @SubscribeEvent
    public void onLivingEntity(LivingDamageEvent event) {
        LivingEntity target = event.getEntity();
        Entity source = event.getSource().getEntity();
        String damageType = event.getSource().getMsgId();

        // 避免反傷輪迴
        if (target.getTags().contains(REFLECT_DAMAGE_TAG)) return;

        // 設定雙方屬性
        if (!(source instanceof LivingEntity)) return;
        LazyOptional<IPlayerTotalAttributeData> targetCap = target.getCapability(ModCapabilities.PLAYER_TOTAL_ATTRIBUTE_CAP);
        LazyOptional<IPlayerTotalAttributeData> sourceCap = source.getCapability(ModCapabilities.PLAYER_TOTAL_ATTRIBUTE_CAP);

        if (!targetCap.isPresent() || !sourceCap.isPresent()) return;

        IPlayerTotalAttributeData targetAttribute = targetCap.resolve().orElseThrow();
        IPlayerTotalAttributeData sourceAttribute = sourceCap.resolve().orElseThrow();

        var rand = ThreadLocalRandom.current();

        // 1. 基礎攻擊 & 防禦
        float baseAttack = damageType.equals("physical")
                ? sourceAttribute.getPlayerTotalPhysicalAttack()
                : sourceAttribute.getPlayerTotalMagicalAttack();

        float baseDefense = damageType.equals("magical")
                ? targetAttribute.getPlayerTotalPhysicalDefense()
                : targetAttribute.getPlayerTotalMagicalDefense();

        // 2. 爆擊、命中、閃避
        if (rand.nextFloat() < sourceAttribute.getPlayerTotalCritRate()) {
            baseAttack *= (1 + sourceAttribute.getPlayerTotalCritMagnification());
        }else if (rand.nextFloat() > sourceAttribute.getPlayerTotalAccuracyRate()) {
            //沒命中 是否閃避
            if (rand.nextFloat() < targetAttribute.getPlayerTotalDodgeRate()) {
                event.setAmount(0);
                return;
            }
        }

        // 3. 無視防禦
        baseDefense *= (1 - sourceAttribute.getPlayerTotalIgnoreDefense());

        // 4. 屬性傷害 & 防禦
        float elementAttack = 0;
        float elementDefense = 0;

        switch (damageType) {
            case "gold":
                elementAttack = sourceAttribute.getPlayerTotalGoldAttack();
                elementDefense = targetAttribute.getPlayerTotalGoldDefense();
                break;
            case "wood":
                elementAttack = sourceAttribute.getPlayerTotalWoodAttack();
                elementDefense = targetAttribute.getPlayerTotalWoodDefense();
                break;
            case "water":
                elementAttack = sourceAttribute.getPlayerTotalWaterAttack();
                elementDefense = targetAttribute.getPlayerTotalWaterDefense();
                break;
            case "fire":
                elementAttack = sourceAttribute.getPlayerTotalFireAttack();
                elementDefense = targetAttribute.getPlayerTotalFireDefense();
                break;
            case "earth":
                elementAttack = sourceAttribute.getPlayerTotalEarthAttack();
                elementDefense = targetAttribute.getPlayerTotalEarthDefense();
                break;
        }

        // 5. 計算最終傷害
        // 基礎傷害
        float physicalDamage = Math.max(0, baseAttack - baseDefense);
        // 属性傷害
        float elementalDamage = Math.max(0, elementAttack - elementDefense);
        // 總傷害 = (基礎傷害 + 属性伤害) * 增傷倍率 * 傷害減免
        float totalDamage = (physicalDamage + elementalDamage)
                * (1 + sourceAttribute.getPlayerTotalDamageIncrease())
                * (1 - targetAttribute.getPlayerTotalDamageDecrease());

        // 設定最小傷害
        totalDamage = Math.max(1, totalDamage);

        // 6. 反傷計算
        if (targetAttribute.getPlayerTotalReflectRate() > 0 && rand.nextFloat() < targetAttribute.getPlayerTotalReflectRate()) {
            float reflectDamage = totalDamage * targetAttribute.getPlayerTotalReflectMagnification();
            if (source instanceof LivingEntity attacker) {
                attacker.addTag(REFLECT_DAMAGE_TAG);
                try {
                    attacker.hurt(event.getSource(), reflectDamage);
                } finally {
                    attacker.removeTag(REFLECT_DAMAGE_TAG);
                }
            }
        }

        // 7. 吸血
        if (sourceAttribute.getPlayerTotalLifeStealRate() > 0 && rand.nextFloat() < sourceAttribute.getPlayerTotalLifeStealRate()) {
            float lifeSteal = totalDamage * sourceAttribute.getPlayerTotalLifeStealRate();
            if (source instanceof LivingEntity attacker) {
                attacker.heal(lifeSteal);
                totalDamage += lifeSteal;
            }
        }

        // 8. 吸藍
        if (sourceAttribute.getPlayerTotalManaStealRate() > 0 && rand.nextFloat() < sourceAttribute.getPlayerTotalManaStealRate()) {
            float manaSteal = totalDamage * sourceAttribute.getPlayerTotalManaStealRate();
            source.getCapability(ModCapabilities.MANA_CAP).ifPresent((sourceMana) -> {
                sourceMana.addMana(manaSteal);
            });
            target.getCapability(ModCapabilities.MANA_CAP).ifPresent((targetMana) -> {
                targetMana.subtractMana(manaSteal);
            });
        }

        // 9. 負面狀態處理
        if (damageType.equals("debuff")) {
            if (rand.nextFloat() < targetAttribute.getPlayerTotalDebuffResistRate()) {
                // 抵抗负面状态，不应用debuff
                // 可以在这里取消某些effect
            } else {
                // 韧性减少持续时间
                // 如果有应用的effect，持续时间 = 原时间 * (1 - tenacity)
                // 这需要在具体的effect处理中实现
            }
        }

        // 10. 應用最終傷害
        event.setAmount(totalDamage);
    }
}