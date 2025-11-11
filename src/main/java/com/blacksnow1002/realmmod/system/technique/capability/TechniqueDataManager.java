package com.blacksnow1002.realmmod.system.technique.capability;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;

import java.util.*;

/**
 * 功法數據管理器 - 純數據存儲和持久化
 * 不包含任何業務邏輯
 */
public class TechniqueDataManager implements ITechniqueDataManager{

    // 玩家已解鎖的功法 <玩家UUID, 功法ID集合>
    private final Set<String> unlockedTechniques = new HashSet<>();

    // 玩家功法等級 <功法ID, 等級>
    private final Map<String, Integer> techniqueLevels = new HashMap<>();

    // 玩家已裝備的功法 <玩家UUID, 功法ID集合>
    private final Set<String> equippedTechniques = new HashSet<>();

    // ==================== 解鎖數據 ====================

    @Override
    public boolean isUnlocked(String techniqueId) {
        return unlockedTechniques.contains(techniqueId);
    }

    @Override
    public void unlockTechnique(String techniqueId) {
        unlockedTechniques.add(techniqueId);
    }

    @Override
    public Set<String> getUnlockedTechniques() {
        return unlockedTechniques;
    }

    // ==================== 等級數據 ====================
    @Override
    public int getTechniqueLevel(String techniqueId) {
        return techniqueLevels.getOrDefault(techniqueId, 0);
    }

    @Override
    public void setTechniqueLevel(String  techniqueId, int level) {
        techniqueLevels.put(techniqueId, level);
    }

    // ==================== 裝備數據 ====================
    @Override
    public Set<String> getEquippedTechniques() {
        return equippedTechniques;
    }

    @Override
    public boolean isEquipped(String techniqueId) {
        return equippedTechniques.contains(techniqueId);
    }


    @Override
    public boolean equipTechnique(String techniqueId) {
        equippedTechniques.add(techniqueId);
        return true;
    }

    @Override
    public boolean unequipTechnique(String techniqueId) {
        if (equippedTechniques.contains(techniqueId)) {
            equippedTechniques.remove(techniqueId);
            return true;
        } else {
            return false;
        }
    }

    // ==================== NBT 序列化 ====================
    @Override
    public CompoundTag saveNBTData() {
        CompoundTag tag = new CompoundTag();

        ListTag unlockedList = new ListTag();
        for (String id : unlockedTechniques) {
            CompoundTag t = new CompoundTag();
            t.putString("id", id);
            unlockedList.add(t);
        }
        tag.put("unlocked", unlockedList);

        // equipped
        ListTag equippedList = new ListTag();
        for (String id : equippedTechniques) {
            CompoundTag t = new CompoundTag();
            t.putString("id", id);
            equippedList.add(t);
        }
        tag.put("equipped", equippedList);

        // levels
        CompoundTag levelsTag = new CompoundTag();
        for (var entry : techniqueLevels.entrySet()) {
            levelsTag.putInt(entry.getKey(), entry.getValue());
        }
        tag.put("levels", levelsTag);

        return tag;
        }

    @Override
    public void loadNBTData(CompoundTag tag) {
        unlockedTechniques.clear();
        equippedTechniques.clear();
        techniqueLevels.clear();

        // unlocked
        if (tag.contains("unlocked", Tag.TAG_LIST)) {
            ListTag list = tag.getList("unlocked", Tag.TAG_COMPOUND);
            for (Tag t : list) {
                unlockedTechniques.add(((CompoundTag)t).getString("id"));
            }
        }

        // equipped
        if (tag.contains("equipped", Tag.TAG_LIST)) {
            ListTag list = tag.getList("equipped", Tag.TAG_COMPOUND);
            for (Tag t : list) {
                equippedTechniques.add(((CompoundTag)t).getString("id"));
            }
        }

        // levels
        if (tag.contains("levels", Tag.TAG_COMPOUND)) {
            CompoundTag lvlTag = tag.getCompound("levels");
            for (String key : lvlTag.getAllKeys()) {
                techniqueLevels.put(key, lvlTag.getInt(key));
            }
        }
    }
}