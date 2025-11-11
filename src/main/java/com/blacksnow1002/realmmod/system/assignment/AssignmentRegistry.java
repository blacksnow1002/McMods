package com.blacksnow1002.realmmod.system.assignment;

import com.blacksnow1002.realmmod.assignment.assignments.*;
import com.blacksnow1002.realmmod.system.assignment.assignments.*;

/**
 * 任務註冊表 - 集中管理所有任務的註冊
 */
public class AssignmentRegistry {

    /**
     * 註冊所有任務
     * 在模組初始化時調用
     */
    public static void registerAll() {
        AssignmentSystem system = AssignmentSystem.getInstance();

        system.registerAssignment(new TestKillEntityAssignment());
        system.registerAssignment(new TestKillEntitySecondAssignment());
        system.registerAssignment(new TestAnotherSecondAssignment());
        system.registerAssignment(new TestAnotherAssignment());

        // ==================== 火焰至尊功相關任務 ====================
        system.registerAssignment(new FireSupremeUnlockAssignment());
        system.registerAssignment(new FireSupremeLevel3Assignment());
        system.registerAssignment(new FireSupremeLevel6Assignment());
        system.registerAssignment(new FireSupremeLevel9Assignment());

        // 🔮 未來可以繼續添加更多任務
    }

    /**
     * 獲取任務（便捷方法）
     */
    public static BaseAssignment get(String assignmentId) {
        return AssignmentSystem.getInstance().getAssignment(assignmentId);
    }

    /**
     * 檢查任務是否存在
     */
    public static boolean exists(String assignmentId) {
        return get(assignmentId) != null;
    }

    // ==================== 任務ID常量 ====================

    public static final class AssignmentIds {
        public static final String TEST_KILL_ENTITY_ASSIGNMENT = "test_kill_entity_assignment";
        public static final String TEST_KILL_ENTITY_SECOND_ASSIGNMENT = "test_kill_entity_second_assignment";
        public static final String TEST_ANOTHER_ASSIGNMENT = "test_another_assignment";
        public static final String TEST_ANOTHER_SECOND_ASSIGNMENT = "test_another_second_assignment";
        //引氣訣任務
        public static final String QI_CONDENSATION_UNLOCK = "qi_condensation_unlock";
        public static final String QI_CONDENSATION_LEVEL_2 = "qi_condensation_level_2";
        public static final String QI_CONDENSATION_LEVEL_3 = "qi_condensation_level_3";


        // 火焰至尊功任務
        public static final String FIRE_SUPREME_UNLOCK = "fire_supreme_unlock";
        public static final String FIRE_SUPREME_LEVEL_3 = "fire_supreme_level_3";
        public static final String FIRE_SUPREME_LEVEL_6 = "fire_supreme_level_6";
        public static final String FIRE_SUPREME_LEVEL_9 = "fire_supreme_level_9";
    }
}