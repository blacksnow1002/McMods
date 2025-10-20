package com.blacksnow1002.realmmod.technique;

import com.blacksnow1002.realmmod.assignment.AssignmentSystem;
import com.blacksnow1002.realmmod.assignment.IAssignmentDataManager;
import com.blacksnow1002.realmmod.capability.ModCapabilities;
import com.blacksnow1002.realmmod.capability.cultivation.CultivationRealm;
import com.blacksnow1002.realmmod.capability.spiritroot.ISpiritRootData;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;

import java.util.*;

/**
 * 功法系統 - 處理所有功法相關的業務邏輯
 * 協調 BaseTechnique（規則）和 TechniqueDataManager（數據）
 */
public class TechniqueSystem {

    private static TechniqueSystem INSTANCE;

    private final Map<String, BaseTechnique> techniques = new HashMap<>();

    private TechniqueSystem() {
    }

    public static TechniqueSystem getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new TechniqueSystem();
        }
        return INSTANCE;
    }

    public static void init() {
        if (INSTANCE == null) {
            INSTANCE = new TechniqueSystem();
        }
    }

    public ITechniqueDataManager getTechniqueData(ServerPlayer player) {
        return player.getCapability(ModCapabilities.TECHNIQUE_CAP)
                .orElseThrow(() -> new IllegalStateException("Technique data manager has not been initialized!"));
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

        ITechniqueDataManager dataManager = getTechniqueData(player);
        // 已經解鎖
        if (dataManager.isUnlocked(techniqueId)) {
            player.sendSystemMessage(Component.literal("該功法已被解鎖"));
            return false;
        } else if (!(checkRealmRequirement(player, technique.getRequiredRealm()))) {
            player.sendSystemMessage(Component.literal("境界不足，請達到" + technique.getRequiredRealm().getDisplayName() + "再來解鎖"));
            return false;
        } else if (!(checkSpiritRootRequirement(player, technique.getSpiritRootRequirements()))) {
            player.sendSystemMessage(Component.literal("靈根等級不足"));
            return false;
        } else if (!checkPrerequisiteTechniques(player, technique.getPrerequisiteTechniques())) {
            player.sendSystemMessage(Component.literal("前置功法未滿足"));
            return false;
        } else if (!checkUnlockAssignment(player, technique.getUnlockAssignmentId())) {
            player.sendSystemMessage(Component.literal("前置任務未滿足"));
            return false;
        }
        return true;
    }

    /**
     * 解鎖功法
     */
    public boolean unlockTechnique(ServerPlayer player, String techniqueId) {
        if (!canUnlock(player, techniqueId)) {
            return false;
        }

        BaseTechnique technique = techniques.get(techniqueId);

        ITechniqueDataManager dataManager = getTechniqueData(player);

        dataManager.unlockTechnique(techniqueId);
        dataManager.setTechniqueLevel(techniqueId, 1);

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

        ITechniqueDataManager dataManager = getTechniqueData(player);
        if (!dataManager.isUnlocked(techniqueId)) {
            return false;
        }

        int currentLevel = dataManager.getTechniqueLevel(techniqueId);
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

        ITechniqueDataManager dataManager = getTechniqueData(player);

        int newLevel = dataManager.getTechniqueLevel(techniqueId) + 1;
        dataManager.setTechniqueLevel(techniqueId, newLevel);

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

        ITechniqueDataManager dataManager = getTechniqueData(player);

        // 未解鎖
        if (!dataManager.isUnlocked(techniqueId)) {
            return false;
        }

        // 已裝備
        if (dataManager.isEquipped(techniqueId)) {
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

        ITechniqueDataManager dataManager = getTechniqueData(player);
        dataManager.equipTechnique(techniqueId);

        BaseTechnique technique = techniques.get(techniqueId);
        int level = dataManager.getTechniqueLevel(techniqueId);

        // 應用裝備效果
        applyEquippedAttributes(player, technique, level, true);

        player.sendSystemMessage(Component.literal("§a裝備功法: " + technique.getDisplayName()));
        return true;
    }

    /**
     * 卸下功法
     */
    public boolean unequipTechnique(ServerPlayer player, String techniqueId) {
        ITechniqueDataManager dataManager = getTechniqueData(player);

        if (!dataManager.isEquipped(techniqueId)) {
            return false;
        }

        BaseTechnique technique = techniques.get(techniqueId);
        int level = dataManager.getTechniqueLevel(techniqueId);

        // 移除裝備效果
        applyEquippedAttributes(player, technique, level, false);

        dataManager.unequipTechnique(techniqueId);
        player.sendSystemMessage(Component.literal("§e卸下功法: " + technique.getDisplayName()));
        return true;
    }

    // ==================== 查詢方法 ====================

    public boolean isTechniqueUnlocked(ServerPlayer player, String techniqueId) {
        ITechniqueDataManager dataManager = getTechniqueData(player);
        return dataManager.isUnlocked(techniqueId);
    }

    public boolean isTechniqueEquipped(ServerPlayer player, String techniqueId) {
        ITechniqueDataManager dataManager = getTechniqueData(player);
        return dataManager.isEquipped(techniqueId);
    }

    public int getTechniqueLevel(ServerPlayer player, String techniqueId) {
        ITechniqueDataManager dataManager = getTechniqueData(player);
        return dataManager.getTechniqueLevel(techniqueId);
    }

    public Set<String> getUnlockedTechniques(ServerPlayer player) {
        ITechniqueDataManager dataManager = getTechniqueData(player);
        return dataManager.getUnlockedTechniques();
    }

    public Set<String> getEquippedTechniques(ServerPlayer player) {
        ITechniqueDataManager dataManager = getTechniqueData(player);
        return dataManager.getEquippedTechniques();
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

        ITechniqueDataManager dataManager = getTechniqueData(player);
        for (String prereq : prerequisites) {
            if (!dataManager.isUnlocked(prereq)) {
                return false;
            }
        }
        return true;
    }

    private boolean checkUnlockAssignment(ServerPlayer player, String assignmentId) {
        if (assignmentId == null) return true;
        return AssignmentSystem.getInstance().isAssignmentCompleted(player, assignmentId);
    }

    private boolean checkAdvanceAssignment(ServerPlayer player, String assignmentId) {
        if (assignmentId == null) return true;
        return AssignmentSystem.getInstance().isAssignmentCompleted(player, assignmentId);
    }

    private void applyGlobalAttributes(ServerPlayer player, BaseTechnique technique, int level) {
        // TODO: 全域效果
    }

    private void applyEquippedAttributes(ServerPlayer player, BaseTechnique technique, int level, boolean apply) {
        // TODO: 裝備效果
    }
}