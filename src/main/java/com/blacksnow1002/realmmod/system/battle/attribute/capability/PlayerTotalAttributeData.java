package com.blacksnow1002.realmmod.system.battle.attribute.capability;

import com.blacksnow1002.realmmod.core.capability.ModCapabilities;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Player;

public class PlayerTotalAttributeData implements IPlayerTotalAttributeData {
    // ===== 基礎屬性 =====
    private int playerTotalMaxHealth = 0;
    private int playerTotalHealthReceive = 0;
    private int playerTotalMaxMana = 0;
    private int playerTotalManaReceive = 0;
    private float playerTotalMoveSpeed = 0;

    // ===== 攻擊屬性 =====
    private int playerTotalPhysicalAttack = 0;
    private int playerTotalMagicalAttack = 0;
    private float playerTotalIgnoreDefense = 0.f;
    private float playerTotalAttackSpeed = 0;
    private float playerTotalAccuracyRate = 0.f;
    private float playerTotalCritRate = 0.f;
    private float playerTotalCritMagnification = 0.f;
    private float playerTotalLifeStealRate = 0.f;
    private int playerTotalLifeStealAmount = 0;
    private float playerTotalManaStealRate = 0.f;
    private int playerTotalManaStealAmount = 0;
    private float playerTotalDamageIncrease = 0;
    private int playerTotalGoldAttack = 0;
    private int playerTotalWoodAttack = 0;
    private int playerTotalWaterAttack = 0;
    private int playerTotalFireAttack = 0;
    private int playerTotalEarthAttack = 0;

    // ===== 防禦屬性 =====
    private int playerTotalPhysicalDefense = 0;
    private int playerTotalMagicalDefense = 0;
    private float playerTotalDamageDecrease = 0;
    private float playerTotalReflectRate = 0;
    private float playerTotalReflectMagnification = 0.f;
    private float playerTotalDebuffResistRate = 0;
    private float playerTotalTenacity = 0;
    private float playerTotalDodgeRate = 0.f;
    private int playerTotalGoldDefense = 0;
    private int playerTotalWoodDefense = 0;
    private int playerTotalWaterDefense = 0;
    private int playerTotalFireDefense = 0;
    private int playerTotalEarthDefense = 0;



    @Override
    public void refreshPlayerTotalAttributeData(Player player) {
        player.getCapability(ModCapabilities.REALM_ATTRIBUTE_CAP).ifPresent(cap -> {
            playerTotalPhysicalAttack += cap.getRealmAttack();
            playerTotalPhysicalDefense += cap.getRealmDefense();
            playerTotalMaxHealth += cap.getRealmMaxHealth();
            playerTotalMoveSpeed += cap.getRealmMoveSpeed();
            playerTotalDodgeRate += cap.getRealmDodgeRate();
            playerTotalCritRate += cap.getRealmCritRate();
            playerTotalCritMagnification += cap.getRealmCritMagnification();
            playerTotalMaxMana += cap.getRealmMaxMana();
        });
        player.getCapability(ModCapabilities.EQUIPMENT_ATTRIBUTE_CAP).ifPresent(cap -> {
            playerTotalPhysicalAttack += cap.getEquipmentAttack();
            playerTotalPhysicalDefense += cap.getEquipmentDefense();
            playerTotalMaxHealth += cap.getEquipmentMaxHealth();
            playerTotalMoveSpeed += cap.getEquipmentMoveSpeed();
            playerTotalDodgeRate += cap.getEquipmentDodgeRate();
            playerTotalCritRate += cap.getEquipmentCritRate();
            playerTotalCritMagnification += cap.getEquipmentCritMagnification();
            playerTotalMaxMana += cap.getEquipmentMaxMana();
        });
        player.getCapability(ModCapabilities.TECHNIQUE_ATTRIBUTE_CAP).ifPresent(cap -> {
            playerTotalPhysicalAttack += cap.getTechniqueAttack();
            playerTotalPhysicalDefense += cap.getTechniqueDefense();
            playerTotalMaxHealth += cap.getTechniqueMaxHealth();
            playerTotalMoveSpeed += cap.getTechniqueMoveSpeed();
            playerTotalDodgeRate += cap.getTechniqueDodgeRate();
            playerTotalCritRate += cap.getTechniqueCritRate();
            playerTotalCritMagnification += cap.getTechniqueCritMagnification();
            playerTotalMaxMana += cap.getTechniqueMaxMana();
        });
    }

    // ===== 基礎屬性 =====
    @Override
    public int getPlayerTotalMaxHealth() { return playerTotalMaxHealth; }
    @Override
    public void setPlayerTotalMaxHealth(int maxHealthValue) { this.playerTotalMaxHealth = maxHealthValue; }

    @Override
    public int getPlayerTotalHealthReceive() { return playerTotalHealthReceive; }
    @Override
    public void setPlayerTotalHealthReceive(int healthReceiveValue) { this.playerTotalHealthReceive = healthReceiveValue;}

    @Override
    public int getPlayerTotalMaxMana() { return playerTotalMaxMana; }
    @Override
    public void setPlayerTotalMaxMana(int maxManaValue) { this.playerTotalMaxMana = maxManaValue; }

    @Override
    public int getPlayerTotalManaReceive() { return playerTotalManaReceive; }
    @Override
    public void setPlayerTotalManaReceive(int manaReceiveValue) { playerTotalManaReceive = manaReceiveValue; }

    @Override
    public float getPlayerTotalMoveSpeed() { return playerTotalMoveSpeed; }
    @Override
    public void setPlayerTotalMoveSpeed(float speedValue) { this.playerTotalMoveSpeed = speedValue; }



    // ===== 攻擊屬性 =====
    // 基礎攻擊
    @Override
    public int getPlayerTotalPhysicalAttack() { return playerTotalPhysicalAttack; }
    @Override
    public void setPlayerTotalPhysicalAttack(int attackValue) { this.playerTotalPhysicalAttack = attackValue; }

    @Override
    public int getPlayerTotalMagicalAttack() { return playerTotalMagicalAttack; }
    @Override
    public void setPlayerTotalMagicalAttack(int attackValue) { this.playerTotalMagicalAttack = attackValue; }

    @Override
    public float getPlayerTotalIgnoreDefense() { return playerTotalIgnoreDefense; }
    @Override
    public void setPlayerTotalIgnoreDefense(float ignoreDefenseRate) {  this.playerTotalIgnoreDefense = ignoreDefenseRate; }

    @Override
    public float getPlayerTotalAttackSpeed() { return playerTotalAttackSpeed; }
    @Override
    public void setPlayerTotalAttackSpeed(float speedValue) { this.playerTotalAttackSpeed = speedValue; }


    // 命中率 爆擊率 爆擊傷害
    @Override
    public float getPlayerTotalAccuracyRate() { return playerTotalAccuracyRate; }
    @Override
    public void setPlayerTotalAccuracyRate(float accuracyRate) {  this.playerTotalAccuracyRate = accuracyRate; }

    @Override
    public float getPlayerTotalCritRate() { return playerTotalCritRate; }
    @Override
    public void setPlayerTotalCritRate(float CritRate) { this.playerTotalCritRate = CritRate; }

    @Override
    public float getPlayerTotalCritMagnification() { return playerTotalCritMagnification; }
    @Override
    public void setPlayerTotalCritMagnification(float critMagnification) { this.playerTotalCritMagnification = critMagnification; }


    // 吸血 吸藍
    @Override
    public float getPlayerTotalLifeStealRate() { return playerTotalLifeStealRate; }
    @Override
    public void setPlayerTotalLifeStealRate(float lifeStealRate) { playerTotalLifeStealRate = lifeStealRate; }

    @Override
    public int getPlayerTotalLifeStealAmount() { return playerTotalLifeStealAmount; }
    @Override
    public void setPlayerTotalLifeStealAmount(int lifeStealAmount) { this.playerTotalLifeStealAmount = lifeStealAmount; }

    @Override
    public float getPlayerTotalManaStealRate() { return playerTotalManaStealRate; }
    @Override
    public void setPlayerTotalManaStealRate(float manaStealRate) { playerTotalManaStealRate = manaStealRate; }

    @Override
    public int getPlayerTotalManaStealAmount() { return playerTotalManaStealAmount; }
    @Override
    public void setPlayerTotalManaStealAmount(int manaStealAmount) { playerTotalManaStealAmount = manaStealAmount; }


    // 屬性傷害
    @Override
    public int getPlayerTotalGoldAttack() { return playerTotalGoldAttack; }
    @Override
    public void setPlayerTotalGoldAttack(int goldAttackValue) { playerTotalGoldAttack = goldAttackValue; }

    @Override
    public int getPlayerTotalWoodAttack() { return playerTotalWoodAttack; }
    @Override
    public void setPlayerTotalWoodAttack(int woodAttackValue) { playerTotalWoodAttack = woodAttackValue; }

    @Override
    public int getPlayerTotalWaterAttack() { return playerTotalWaterAttack; }
    @Override
    public void setPlayerTotalWaterAttack(int waterAttackValue) { playerTotalWaterAttack = waterAttackValue; }

    @Override
    public int getPlayerTotalFireAttack() { return playerTotalFireAttack; }
    @Override
    public void setPlayerTotalFireAttack(int fireAttackValue) { playerTotalFireAttack = fireAttackValue; }

    @Override
    public int getPlayerTotalEarthAttack() { return playerTotalEarthAttack; }
    @Override
    public void setPlayerTotalEarthAttack(int earthAttackValue) { playerTotalEarthAttack = earthAttackValue; }


    // 最終增傷
    @Override
    public float getPlayerTotalDamageIncrease() { return playerTotalDamageIncrease; }
    @Override
    public void setPlayerTotalDamageIncrease(float damageIncrease) { playerTotalDamageIncrease = damageIncrease; }



    // ===== 防禦屬性 =====
    // 基礎防禦
    @Override
    public int getPlayerTotalPhysicalDefense() { return playerTotalPhysicalDefense; }
    @Override
    public void setPlayerTotalPhysicalDefense(int defenseValue) { this.playerTotalPhysicalDefense = defenseValue; }

    @Override
    public int getPlayerTotalMagicalDefense() { return playerTotalMagicalDefense; }
    @Override
    public void setPlayerTotalMagicalDefense(int defenseValue) {  this.playerTotalMagicalDefense = defenseValue; }


    // 閃避率 反彈率
    @Override
    public float getPlayerTotalDodgeRate() { return playerTotalDodgeRate; }
    @Override
    public void setPlayerTotalDodgeRate(float dodgeRate) {  this.playerTotalDodgeRate = dodgeRate; }
    @Override
    public float getPlayerTotalReflectRate() { return playerTotalReflectRate; }
    @Override
    public void setPlayerTotalReflectRate(float reflectRate) { this.playerTotalReflectRate = reflectRate; }

    @Override
    public float getPlayerTotalReflectMagnification() { return playerTotalReflectMagnification; }
    @Override
    public void setPlayerTotalReflectMagnification(float reflectMagnification) { this.playerTotalReflectMagnification = reflectMagnification; }

    // debuff 相關
    @Override
    public float getPlayerTotalDebuffResistRate() { return playerTotalDebuffResistRate; }
    @Override
    public void setPlayerTotalDebuffResistRate(float debuffResistRate) { this.playerTotalDebuffResistRate = debuffResistRate; }

    @Override
    public float getPlayerTotalTenacity() { return playerTotalTenacity; }
    @Override
    public void setPlayerTotalTenacity(float tenacityValue) { this.playerTotalTenacity = tenacityValue; }

    // 屬性抗性
    @Override
    public int getPlayerTotalGoldDefense() { return playerTotalGoldDefense; }
    @Override
    public void setPlayerTotalGoldDefense(int goldDefenseValue) { playerTotalGoldDefense = goldDefenseValue; }

    @Override
    public int getPlayerTotalWoodDefense() { return playerTotalWoodDefense; }
    @Override
    public void setPlayerTotalWoodDefense(int woodDefenseValue) { playerTotalWoodDefense = woodDefenseValue; }

    @Override
    public int getPlayerTotalWaterDefense() { return playerTotalWaterDefense; }
    @Override
    public void setPlayerTotalWaterDefense(int waterDefenseValue) { playerTotalWaterDefense = waterDefenseValue; }

    @Override
    public int getPlayerTotalFireDefense() { return playerTotalFireDefense; }
    @Override
    public void setPlayerTotalFireDefense(int fireDefenseValue) { playerTotalFireDefense = fireDefenseValue; }

    @Override
    public int getPlayerTotalEarthDefense() { return playerTotalEarthDefense; }
    @Override
    public void setPlayerTotalEarthDefense(int earthDefenseValue) { playerTotalEarthDefense = earthDefenseValue; }

    // 最終減傷
    @Override
    public float getPlayerTotalDamageDecrease() { return playerTotalDamageDecrease; }
    @Override
    public void setPlayerTotalDamageDecrease(float damageDecrease) { this.playerTotalDamageDecrease = damageDecrease; }

    @Override
    public CompoundTag saveNBTData() {
        CompoundTag nbt = new CompoundTag();
        //TODO: 補完屬性
        return nbt;
    }

    @Override
    public void loadNBTData(CompoundTag nbt) {
        //TODO: 補完屬性
    }
}
