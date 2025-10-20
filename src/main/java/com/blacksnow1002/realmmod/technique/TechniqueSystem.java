package com.blacksnow1002.realmmod.technique;

import com.blacksnow1002.realmmod.assignment.AssignmentSystem;
import com.blacksnow1002.realmmod.capability.ModCapabilities;
import com.blacksnow1002.realmmod.capability.cultivation.CultivationRealm;
import com.blacksnow1002.realmmod.capability.spiritroot.ISpiritRootData;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;

import java.util.*;

/**
 * 功法系統 - 處理所有功法相關的業務邏輯
 * 協調 BaseTechnique（規則）和 TechniqueDataManager（數據）
 */
public class TechniqueSystem {

    private static TechniqueSystem INSTANCE;

    private final Map<String, BaseTechnique> techniques = new HashMap<>();
    private final TechniqueDataManager dataManager;

    private TechniqueSystem() {
        this.dataManager = new TechniqueDataManager();
    }

    public static TechniqueSystem getInstance() {
        if (INSTANCE == null) {
            throw new IllegalStateException("TechniqueSystem not initialized! Call init() first.");
        }
        return INSTANCE;
    }

    /** ✅ 初始化方法 */
    public static void init() {
        if (INSTANCE == null) {
            INSTANCE = new TechniqueSystem();
        }
    }

    // ==================== 功法註冊 ====================

    public void registerTechnique(BaseTechnique technique) {
        techniques.put(technique.getId(), technique);
    }

    public BaseTechnique getTechnique(String techniqueId) {
        return techniques.get(techniqueId);
    }

    public Collection<BaseTechnique> getAllTechniques() {
        return techniques.values();
    }

    // ==================== 功法解鎖 ====================

    /**
     * 檢查玩家是否可以解鎖功法
     */
    public boolean canUnlock(ServerPlayer player, String techniqueId) {
        BaseTechnique technique = techniques.get(techniqueId);
        if (technique == null) return false;

        // 已經解鎖
        if (dataManager.isUnlocked(player.getUUID(), techniqueId)) {
            return false;
        }

        return checkRealmRequirement(player, technique.getRequiredRealm())
                && checkSpiritRootRequirement(player, technique.getSpiritRootRequirements())
                && checkPrerequisiteTechniques(player, technique.getPrerequisiteTechniques())
                && checkUnlockAssignment(player, technique.getUnlockAssignmentId());
    }

    /**
     * 解鎖功法
     */
    public boolean unlockTechnique(ServerPlayer player, String techniqueId) {
        if (!canUnlock(player, techniqueId)) {
            return false;
        }

        UUID uuid = player.getUUID();
        dataManager.setUnlocked(uuid, techniqueId, true);
        dataManager.setLevel(uuid, techniqueId, 1);

        BaseTechnique technique = techniques.get(techniqueId);
        applyGlobalAttributes(player, technique, 1);

        player.sendSystemMessage(Component.literal("§a成功解鎖功法: " + techniques.get(techniqueId).getDisplayName()));
        return true;
    }

    // ==================== 功法進階 ====================

    /**
     * 檢查是否可以進階
     */
    public boolean canAdvance(ServerPlayer player, String techniqueId) {
        BaseTechnique technique = techniques.get(techniqueId);
        if (technique == null) return false;

        UUID uuid = player.getUUID();
        if (!dataManager.isUnlocked(uuid, techniqueId)) {
            return false;
        }

        int currentLevel = dataManager.getLevel(uuid, techniqueId);
        if (currentLevel >= technique.getMaxLevel()) {
            return false;
        }

        int targetLevel = currentLevel + 1;
        return checkRealmRequirement(player, technique.getRequiredRealmForLevel(targetLevel))
                && checkAdvanceAssignment(player, technique.getAdvanceAssignmentId(targetLevel));
    }

    /**
     * 功法進階
     */
    public boolean advanceTechnique(ServerPlayer player, String techniqueId) {
        if (!canAdvance(player, techniqueId)) {
            return false;
        }

        UUID uuid = player.getUUID();
        int newLevel = dataManager.getLevel(uuid, techniqueId) + 1;
        dataManager.setLevel(uuid, techniqueId, newLevel);

        BaseTechnique technique = techniques.get(techniqueId);
        applyGlobalAttributes(player, technique, newLevel);
        technique.onLevelReached(player, newLevel);

        player.sendSystemMessage(Component.literal("§a功法進階成功: " + technique.getDisplayName() + " → 第" + newLevel + "層"));
        return true;
    }

    // ==================== 功法裝備 ====================

    /**
     * 檢查是否可以裝備
     */
    public boolean canEquip(ServerPlayer player, String techniqueId) {
        BaseTechnique technique = techniques.get(techniqueId);
        if (technique == null) return false;

        UUID uuid = player.getUUID();

        // 未解鎖
        if (!dataManager.isUnlocked(uuid, techniqueId)) {
            return false;
        }

        // 已裝備
        if (dataManager.isEquipped(uuid, techniqueId)) {
            return false;
        }

        return true;
    }

    /**
     * 裝備功法
     */
    public boolean equipTechnique(ServerPlayer player, String techniqueId) {
        if (!canEquip(player, techniqueId)) {
            return false;
        }

        UUID uuid = player.getUUID();
        dataManager.setEquipped(uuid, techniqueId, true);

        BaseTechnique technique = techniques.get(techniqueId);
        int level = dataManager.getLevel(uuid, techniqueId);

        // 應用裝備效果
        applyEquippedAttributes(player, technique, level, true);

        player.sendSystemMessage(Component.literal("§a裝備功法: " + technique.getDisplayName()));
        return true;
    }

    /**
     * 卸下功法
     */
    public boolean unequipTechnique(ServerPlayer player, String techniqueId) {
        UUID uuid = player.getUUID();

        if (!dataManager.isEquipped(uuid, techniqueId)) {
            return false;
        }

        BaseTechnique technique = techniques.get(techniqueId);
        int level = dataManager.getLevel(uuid, techniqueId);

        // 移除裝備效果
        applyEquippedAttributes(player, technique, level, false);

        dataManager.setEquipped(uuid, techniqueId, false);
        player.sendSystemMessage(Component.literal("§e卸下功法: " + technique.getDisplayName()));
        return true;
    }

    // ==================== 查詢方法 ====================

    public boolean isTechniqueUnlocked(Player player, String techniqueId) {
        return dataManager.isUnlocked(player.getUUID(), techniqueId);
    }

    public boolean isTechniqueEquipped(Player player, String techniqueId) {
        return dataManager.isEquipped(player.getUUID(), techniqueId);
    }

    public int getTechniqueLevel(Player player, String techniqueId) {
        return dataManager.getLevel(player.getUUID(), techniqueId);
    }

    public Set<String> getUnlockedTechniques(Player player) {
        return dataManager.getUnlockedTechniques(player.getUUID());
    }

    public Set<String> getEquippedTechniques(Player player) {
        return dataManager.getEquippedTechniques(player.getUUID());
    }

    // ==================== 輔助方法 ====================

    private boolean checkRealmRequirement(ServerPlayer player, CultivationRealm required) {
        if (required == null) return true;

        return player.getCapability(ModCapabilities.CULTIVATION_CAP).map(cap ->
                cap.getRealm().ordinal() >= required.ordinal()
        ).orElse(false);
    }

    private boolean checkSpiritRootRequirement(ServerPlayer player, Map<BaseTechnique.SpiritRootType, Integer> requirements) {
        if (requirements.isEmpty()) return true;

        return player.getCapability(ModCapabilities.SPIRIT_ROOT_CAP).map(spiritRoot -> {
            for (Map.Entry<BaseTechnique.SpiritRootType, Integer> entry : requirements.entrySet()) {
                int requiredLevel = entry.getValue();
                int actualLevel = getSpiritRootLevel(spiritRoot, entry.getKey());
                if (actualLevel < requiredLevel) {
                    return false;
                }
            }
            return true;
        }).orElse(false);
    }

    private int getSpiritRootLevel(ISpiritRootData spiritRoot, BaseTechnique.SpiritRootType type) {
        return switch (type) {
            case GOLD -> spiritRoot.getGoldRootLevel();
            case WOOD -> spiritRoot.getWoodRootLevel();
            case WATER -> spiritRoot.getWaterRootLevel();
            case FIRE -> spiritRoot.getFireRootLevel();
            case EARTH -> spiritRoot.getEarthRootLevel();
            case SUM ->  spiritRoot.getSumRootLevel();
        };
    }

    private boolean checkPrerequisiteTechniques(ServerPlayer player, List<String> prerequisites) {
        if (prerequisites.isEmpty()) return true;

        UUID uuid = player.getUUID();
        for (String prereq : prerequisites) {
            if (!dataManager.isUnlocked(uuid, prereq)) {
                return false;
            }
        }
        return true;
    }

    private boolean checkUnlockAssignment(ServerPlayer player, String assignmentId) {
        if (assignmentId == null) return true;
        // TODO: 整合任務系統
        return AssignmentSystem.getInstance().isAssignmentCompleted(player, assignmentId);
    }

    private boolean checkAdvanceAssignment(ServerPlayer player, String assignmentId) {
        if (assignmentId == null) return true;
        // TODO: 整合任務系統
        return AssignmentSystem.getInstance().isAssignmentCompleted(player, assignmentId);
    }

    private void applyGlobalAttributes(ServerPlayer player, BaseTechnique technique, int level) {
        Map<String, Number> attributes = technique.getGlobalAttributes(level);
        player.getCapability(ModCapabilities.TECHNIQUE_ATTRIBUTE_CAP).ifPresent(cap -> {
            cap.setTechniqueAttack(cap.getTechniqueAttack() + (int) attributes.getOrDefault("attack", 0));
            cap.setTechniqueDefense(cap.getTechniqueDefense() + (int) attributes.getOrDefault("defense", 0));
            cap.setTechniqueMaxHealth(cap.getTechniqueMaxHealth() + (int) attributes.getOrDefault("health", 0));
            cap.setTechniqueMoveSpeed(cap.getTechniqueMoveSpeed() + (float) attributes.getOrDefault("speed", 0));
            cap.setTechniqueDodgeRate(cap.getTechniqueDodgeRate() + (float) attributes.getOrDefault("dodgeRate", 0));
            cap.setTechniqueCritRate(cap.getTechniqueCritRate() + (float) attributes.getOrDefault("critRate", 0));
            cap.setTechniqueCritMagnification(cap.getTechniqueCritMagnification() + (float) attributes.getOrDefault("critMag", 0));
            cap.setTechniqueMaxMana(cap.getTechniqueMaxMana() + (int) attributes.getOrDefault("mana", 0));
        });
    }

    private void applyEquippedAttributes(ServerPlayer player, BaseTechnique technique, int level, boolean apply) {
        Map<String, Number> attributes = technique.getEquippedAttributes(level);
        player.getCapability(ModCapabilities.TECHNIQUE_ATTRIBUTE_CAP).ifPresent(cap -> {
            if (apply) {
                cap.setTechniqueAttack(cap.getTechniqueAttack() + (int) attributes.getOrDefault("attack", 0));
                cap.setTechniqueDefense(cap.getTechniqueDefense() + (int) attributes.getOrDefault("defense", 0));
                cap.setTechniqueMaxHealth(cap.getTechniqueMaxHealth() + (int) attributes.getOrDefault("health", 0));
                cap.setTechniqueMoveSpeed(cap.getTechniqueMoveSpeed() + (float) attributes.getOrDefault("speed", 0));
                cap.setTechniqueDodgeRate(cap.getTechniqueDodgeRate() + (float) attributes.getOrDefault("dodgeRate", 0));
                cap.setTechniqueCritRate(cap.getTechniqueCritRate() + (float) attributes.getOrDefault("critRate", 0));
                cap.setTechniqueCritMagnification(cap.getTechniqueCritMagnification() + (float) attributes.getOrDefault("critMag", 0));
                cap.setTechniqueMaxMana(cap.getTechniqueMaxMana() + (int) attributes.getOrDefault("mana", 0));
            } else {
                cap.setTechniqueAttack(cap.getTechniqueAttack() - (int) attributes.getOrDefault("attack", 0));
                cap.setTechniqueDefense(cap.getTechniqueDefense() - (int) attributes.getOrDefault("defense", 0));
                cap.setTechniqueMaxHealth(cap.getTechniqueMaxHealth() - (int) attributes.getOrDefault("health", 0));
                cap.setTechniqueMoveSpeed(cap.getTechniqueMoveSpeed() - (float) attributes.getOrDefault("speed", 0));
                cap.setTechniqueDodgeRate(cap.getTechniqueDodgeRate() - (float) attributes.getOrDefault("dodgeRate", 0));
                cap.setTechniqueCritRate(cap.getTechniqueCritRate() - (float) attributes.getOrDefault("critRate", 0));
                cap.setTechniqueCritMagnification(cap.getTechniqueCritMagnification() - (float) attributes.getOrDefault("critMag", 0));
                cap.setTechniqueMaxMana(cap.getTechniqueMaxMana() - (int) attributes.getOrDefault("mana", 0));
            }
        });

        player.getCapability(ModCapabilities.PLAYER_TOTAL_ATTRIBUTE_CAP).ifPresent(cap -> {
            cap.refreshPlayerTotalAttributeData(player);
        });
    }

    // ==================== 數據管理 ====================

    public TechniqueDataManager getDataManager() {
        return dataManager;
    }
}