package com.blacksnow1002.realmmod.system.technique;

import com.blacksnow1002.realmmod.system.technique.techniques.FireSupremeTechnique;

/**
 * 功法註冊表 - 集中管理所有功法的註冊
 */
public class TechniqueRegistry {

    /**
     * 註冊所有功法
     * 在模組初始化時調用
     */
    public static void registerAll() {
        TechniqueSystem system = TechniqueSystem.getInstance();

        // ==================== 火系功法 ====================
        system.registerTechnique(new FireSupremeTechnique());

        // 🔮 未來可以繼續添加更多功法
    }

    /**
     * 獲取功法（便捷方法）
     */
    public static BaseTechnique get(String techniqueId) {
        return TechniqueSystem.getInstance().getTechnique(techniqueId);
    }

    /**
     * 檢查功法是否存在
     */
    public static boolean exists(String techniqueId) {
        return get(techniqueId) != null;
    }

    // ==================== 功法ID常量 ====================
    // 方便在代碼中引用，避免字符串錯誤

    public static final class TechniqueIds {
        public static final String FIRE_SUPREME = "fire_supreme";
        public static final String QI_CONDENSATION = "qi_condensation";

    }
}