package com.blacksnow1002.realmmod.technique;

import com.blacksnow1002.realmmod.capability.cultivation.CultivationRealm;
import net.minecraft.server.level.ServerPlayer;

import java.util.*;

/**
 * 功法基礎類 - 僅定義功法的靜態屬性和規則
 * 不包含任何玩家數據管理或邏輯執行
 */
public abstract class BaseTechnique {

    private final String id;
    private final String displayName;
    private final int maxLevel;

    public BaseTechnique(String id, String displayName, int maxLevel) {
        this.id = id;
        this.displayName = displayName;
        this.maxLevel = maxLevel;
    }

    // ==================== 基本資訊 ====================

    public String getId() {
        return id;
    }

    public String getDisplayName() {
        return displayName;
    }

    public int getMaxLevel() {
        return maxLevel;
    }

    // ==================== 解鎖條件定義 ====================

    /**
     * 解鎖所需境界
     */
    public abstract CultivationRealm getRequiredRealm();

    /**
     * 解鎖所需靈根等級
     *
     * @return Map<靈根類型, 所需等級>
     */
    public Map<SpiritRootType, Integer> getSpiritRootRequirements() {
        return Collections.emptyMap();
    }

    /**
     * 解鎖所需前置功法ID列表
     */
    public List<String> getPrerequisiteTechniques() {
        return Collections.emptyList();
    }

    /**
     * 解鎖所需任務ID
     */
    public String getUnlockAssignmentId() {
        return null;
    }

    // ==================== 進階條件定義 ====================

    /**
     * 升級到指定層級所需境界
     */
    public CultivationRealm getRequiredRealmForLevel(int level) {
        return getRequiredRealm();
    }

    /**
     * 升級到指定層級所需任務ID
     */
    public String getAdvanceAssignmentId(int level) {
        return null;
    }

    public void onLevelReached(ServerPlayer player, int level) {
        return;
    }

    // ==================== 效果定義 ====================

    /**
     * 全域屬性加成 - 無論是否裝備都會生效
     *
     * @param level 功法等級
     * @return 屬性修飾符映射
     */
    public Map<String, Number> getGlobalAttributes(int level) {
        return Collections.emptyMap();
    }

    /**
     * 裝備屬性加成 - 只有裝備時才會生效
     *
     * @param level 功法等級
     * @return 屬性修飾符映射
     */
    public Map<String, Number> getEquippedAttributes(int level) {
        return Collections.emptyMap();
    }

    /**
     * 獲取裝備時解鎖的神通ID列表
     *
     * @param level 功法等級
     */
    public List<String> getProvidedSpells(int level) {
        return Collections.emptyList();
    }


    // ==================== 枚舉定義 ====================

    public enum SpiritRootType {
        GOLD("金", 0xFFD700),
        WOOD("木", 0x228B22),
        WATER("水", 0x4169E1),
        FIRE("火", 0xFF4500),
        EARTH("土", 0x8B4513),
        SUM("總和", 0x8B4513);

        private final String displayName;
        private final int color;

        SpiritRootType(String displayName, int color) {
            this.displayName = displayName;
            this.color = color;
        }

        public String getDisplayName() {
            return displayName;
        }

        public int getColor() {
            return color;
        }
    }

}