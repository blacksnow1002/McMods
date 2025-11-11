package com.blacksnow1002.realmmod.system.battle.attribute.capability;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Player;

public interface IPlayerTotalAttributeData {
    void refreshPlayerTotalAttributeData(Player player);

    // ===== 基礎屬性 =====
    //氣血
    int getPlayerTotalMaxHealth();
    void setPlayerTotalMaxHealth(int maxHealthValue);

    //氣血回復速度
    int getPlayerTotalHealthReceive();
    void setPlayerTotalHealthReceive(int healthReceiveValue);

    //真元
    int getPlayerTotalMaxMana();
    void setPlayerTotalMaxMana(int maxManaValue);

    //真元回復速度
    int getPlayerTotalManaReceive();
    void setPlayerTotalManaReceive(int manaReceiveValue);

    //移動速度
    float getPlayerTotalMoveSpeed();
    void setPlayerTotalMoveSpeed(float speedValue);


    // ===== 攻擊屬性 =====
    //物攻
    int getPlayerTotalPhysicalAttack();
    void setPlayerTotalPhysicalAttack(int attackValue);

    //法攻
    int getPlayerTotalMagicalAttack();
    void setPlayerTotalMagicalAttack(int attackValue);

    //無視防禦
    float getPlayerTotalIgnoreDefense();
    void setPlayerTotalIgnoreDefense(float defenseValue);

    //攻速
    float getPlayerTotalAttackSpeed();
    void setPlayerTotalAttackSpeed(float speedValue);

    //命中率
    float getPlayerTotalAccuracyRate();
    void setPlayerTotalAccuracyRate(float accuracyRateValue);

    //爆擊率
    float getPlayerTotalCritRate();
    void setPlayerTotalCritRate(float CritRate);

    //爆擊傷害倍率
    float getPlayerTotalCritMagnification();
    void setPlayerTotalCritMagnification(float critMagnification);

    //吸血率
    float getPlayerTotalLifeStealRate();
    void setPlayerTotalLifeStealRate(float lifeStealRate);

    //吸血量
    int getPlayerTotalLifeStealAmount();
    void setPlayerTotalLifeStealAmount(int lifeStealAmount);

    //吸藍率
    float getPlayerTotalManaStealRate();
    void setPlayerTotalManaStealRate(float manaStealRate);

    //吸藍量
    int getPlayerTotalManaStealAmount();
    void setPlayerTotalManaStealAmount(int manaStealAmount);

    //最終增傷
    float getPlayerTotalDamageIncrease();
    void setPlayerTotalDamageIncrease(float damageIncrease);

    //屬性傷害
    int getPlayerTotalGoldAttack();
    void setPlayerTotalGoldAttack(int goldAttackValue);

    int getPlayerTotalWoodAttack();
    void setPlayerTotalWoodAttack(int woodAttackValue);

    int getPlayerTotalWaterAttack();
    void setPlayerTotalWaterAttack(int waterAttackValue);

    int getPlayerTotalFireAttack();
    void setPlayerTotalFireAttack(int fireAttackValue);

    int getPlayerTotalEarthAttack();
    void setPlayerTotalEarthAttack(int earthAttackValue);

    // ===== 防禦屬性 =====
    //物防
    int getPlayerTotalPhysicalDefense();
    void setPlayerTotalPhysicalDefense(int defenseValue);

    //法防
    int getPlayerTotalMagicalDefense();
    void setPlayerTotalMagicalDefense(int defenseValue);

    //攻擊減免
    float getPlayerTotalDamageDecrease();
    void setPlayerTotalDamageDecrease(float damageDecrease);

    //反彈傷害率
    float getPlayerTotalReflectRate();
    void setPlayerTotalReflectRate(float reflectRate);

    float getPlayerTotalReflectMagnification();
    void setPlayerTotalReflectMagnification(float reflectAmount);

    //負面傷害免疫 (減少中 deBuff 機率)
    float getPlayerTotalDebuffResistRate();
    void setPlayerTotalDebuffResistRate(float debuffResistRate);

    //韌性 (debuff 持續時間減短)
    float getPlayerTotalTenacity();
    void setPlayerTotalTenacity(float tenacityValue);

    //閃避率
    float getPlayerTotalDodgeRate();
    void setPlayerTotalDodgeRate(float dodgeRate);

    //屬性防禦
    int getPlayerTotalGoldDefense();
    void setPlayerTotalGoldDefense(int goldDefenseValue);

    int getPlayerTotalWoodDefense();
    void setPlayerTotalWoodDefense(int woodDefenseValue);

    int getPlayerTotalWaterDefense();
    void setPlayerTotalWaterDefense(int waterDefenseValue);

    int getPlayerTotalFireDefense();
    void setPlayerTotalFireDefense(int fireDefenseValue);

    int getPlayerTotalEarthDefense();
    void setPlayerTotalEarthDefense(int earthDefenseValue);

    CompoundTag saveNBTData();
    void loadNBTData(CompoundTag nbt);

}
