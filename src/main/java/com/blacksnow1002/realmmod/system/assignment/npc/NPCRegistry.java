package com.blacksnow1002.realmmod.system.assignment.npc;

import com.blacksnow1002.realmmod.assignment.npc.npcs.*;
import com.blacksnow1002.realmmod.system.assignment.npc.npcs.TestNPC;

import java.util.*;

/**
 * NPC 註冊表 - 管理所有 NPC 與任務的對應關係
 */
public class NPCRegistry {

    private static NPCRegistry INSTANCE;
    private final Map<String, BaseNPC> npc_map = new HashMap<>();

    private NPCRegistry() {
    }

    public static NPCRegistry getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new NPCRegistry();
        }
        return INSTANCE;
    }

    /**
     * 初始化所有 NPC - 在遊戲啟動時調用
     */
    public static void registerAll() {
        NPCRegistry registry = getInstance();
        registry.registerNPC(new TestNPC());
        // 未來還能加：registry.registerNPC(new MerchantNPC()); ...
    }

    // ==================== 註冊 ====================

    /**
     * 註冊 NPC
     */
    public void registerNPC(BaseNPC npc) {
        if (npc_map.containsKey(npc.getNpcId())) {
            throw new IllegalArgumentException("NPC ID 已存在: " + npc.getNpcId());
        }
        npc_map.put(npc.getNpcId(), npc);
        System.out.println("[NPC 系統] 註冊 NPC: " + npc.getNpcName() + " (" + npc.getNpcId() + ")");
    }

    /**
     * 獲取 NPC
     */
    public BaseNPC getNPC(String npcId) {
        return npc_map.get(npcId);
    }

    /**
     * 獲取所有 NPC
     */
    public Collection<BaseNPC> getAllNPCs() {
        return npc_map.values();
    }

    /**
     * 檢查 NPC 是否存在
     */
    public boolean hasNPC(String npcId) {
        return npc_map.containsKey(npcId);
    }

    public static final class NPCIds {
        public static final String TEST_NPC = "test_npc";
    }

    // ==================== 查詢 ====================

    /**
     * 根據任務 ID 獲取提供該任務的 NPC
     */
    public BaseNPC getNPCByAssignment(String assignmentId) {
        for (BaseNPC npc : npc_map.values()) {
            if (npc.getAssignmentIds().contains(assignmentId)) {
                return npc;
            }
        }
        return null;
    }

    /**
     * 獲取提供指定任務的所有 NPC
     */
    public List<BaseNPC> getNPCsByAssignment(String assignmentId) {
        List<BaseNPC> result = new ArrayList<>();
        for (BaseNPC npc : npc_map.values()) {
            if (npc.getAssignmentIds().contains(assignmentId)) {
                result.add(npc);
            }
        }
        return result;
    }
}