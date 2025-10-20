package com.blacksnow1002.realmmod.technique;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.core.HolderLookup;

import java.util.*;

/**
 * 功法數據管理器 - 純數據存儲和持久化
 * 不包含任何業務邏輯
 */
public class TechniqueDataManager {

    // 玩家已解鎖的功法 <玩家UUID, 功法ID集合>
    private final Map<UUID, Set<String>> unlockedTechniques = new HashMap<>();

    // 玩家功法等級 <玩家UUID, <功法ID, 等級>>
    private final Map<UUID, Map<String, Integer>> techniqueLevels = new HashMap<>();

    // 玩家已裝備的功法 <玩家UUID, 功法ID集合>
    private final Map<UUID, Set<String>> equippedTechniques = new HashMap<>();

    // ==================== 解鎖數據 ====================

    public boolean isUnlocked(UUID playerUUID, String techniqueId) {
        Set<String> unlocked = unlockedTechniques.get(playerUUID);
        return unlocked != null && unlocked.contains(techniqueId);
    }

    public void setUnlocked(UUID playerUUID, String techniqueId, boolean unlocked) {
        if (unlocked) {
            unlockedTechniques.computeIfAbsent(playerUUID, k -> new HashSet<>()).add(techniqueId);
        } else {
            Set<String> set = unlockedTechniques.get(playerUUID);
            if (set != null) {
                set.remove(techniqueId);
            }
        }
    }

    public Set<String> getUnlockedTechniques(UUID playerUUID) {
        return new HashSet<>(unlockedTechniques.getOrDefault(playerUUID, Collections.emptySet()));
    }

    // ==================== 等級數據 ====================

    public int getLevel(UUID playerUUID, String techniqueId) {
        Map<String, Integer> levels = techniqueLevels.get(playerUUID);
        if (levels == null) return 0;
        return levels.getOrDefault(techniqueId, 0);
    }

    public void setLevel(UUID playerUUID, String techniqueId, int level) {
        techniqueLevels.computeIfAbsent(playerUUID, k -> new HashMap<>()).put(techniqueId, level);
    }

    public Map<String, Integer> getAllLevels(UUID playerUUID) {
        return new HashMap<>(techniqueLevels.getOrDefault(playerUUID, Collections.emptyMap()));
    }

    // ==================== 裝備數據 ====================

    public boolean isEquipped(UUID playerUUID, String techniqueId) {
        Set<String> equipped = equippedTechniques.get(playerUUID);
        return equipped != null && equipped.contains(techniqueId);
    }

    public void setEquipped(UUID playerUUID, String techniqueId, boolean equipped) {
        if (equipped) {
            equippedTechniques.computeIfAbsent(playerUUID, k -> new HashSet<>()).add(techniqueId);
        } else {
            Set<String> set = equippedTechniques.get(playerUUID);
            if (set != null) {
                set.remove(techniqueId);
            }
        }
    }

    public Set<String> getEquippedTechniques(UUID playerUUID) {
        return new HashSet<>(equippedTechniques.getOrDefault(playerUUID, Collections.emptySet()));
    }

    public int getEquippedCount(UUID playerUUID) {
        Set<String> equipped = equippedTechniques.get(playerUUID);
        return equipped != null ? equipped.size() : 0;
    }

    // ==================== 數據清理 ====================

    public void clearPlayerData(UUID playerUUID) {
        unlockedTechniques.remove(playerUUID);
        techniqueLevels.remove(playerUUID);
        equippedTechniques.remove(playerUUID);
    }

    // ==================== NBT 序列化 ====================

    public CompoundTag save(HolderLookup.Provider provider) {
        CompoundTag tag = new CompoundTag();
        CompoundTag playersTag = new CompoundTag();

        Set<UUID> allPlayers = new HashSet<>();
        allPlayers.addAll(unlockedTechniques.keySet());
        allPlayers.addAll(techniqueLevels.keySet());
        allPlayers.addAll(equippedTechniques.keySet());

        for (UUID uuid : allPlayers) {
            CompoundTag playerTag = new CompoundTag();

            // 保存已解鎖的功法
            Set<String> unlocked = unlockedTechniques.get(uuid);
            if (unlocked != null && !unlocked.isEmpty()) {
                ListTag unlockedList = new ListTag();
                for (String techniqueId : unlocked) {
                    CompoundTag techniqueTag = new CompoundTag();
                    techniqueTag.putString("id", techniqueId);
                    unlockedList.add(techniqueTag);
                }
                playerTag.put("unlocked", unlockedList);
            }

            // 保存功法等級
            Map<String, Integer> levels = techniqueLevels.get(uuid);
            if (levels != null && !levels.isEmpty()) {
                CompoundTag levelsTag = new CompoundTag();
                for (Map.Entry<String, Integer> entry : levels.entrySet()) {
                    levelsTag.putInt(entry.getKey(), entry.getValue());
                }
                playerTag.put("levels", levelsTag);
            }

            // 保存已裝備的功法
            Set<String> equipped = equippedTechniques.get(uuid);
            if (equipped != null && !equipped.isEmpty()) {
                ListTag equippedList = new ListTag();
                for (String techniqueId : equipped) {
                    CompoundTag techniqueTag = new CompoundTag();
                    techniqueTag.putString("id", techniqueId);
                    equippedList.add(techniqueTag);
                }
                playerTag.put("equipped", equippedList);
            }

            playersTag.put(uuid.toString(), playerTag);
        }

        tag.put("players", playersTag);
        return tag;
    }

    public void load(CompoundTag tag, HolderLookup.Provider provider) {
        unlockedTechniques.clear();
        techniqueLevels.clear();
        equippedTechniques.clear();

        if (!tag.contains("players")) {
            return;
        }

        CompoundTag playersTag = tag.getCompound("players");

        for (String uuidStr : playersTag.getAllKeys()) {
            UUID uuid = UUID.fromString(uuidStr);
            CompoundTag playerTag = playersTag.getCompound(uuidStr);

            // 讀取已解鎖的功法
            if (playerTag.contains("unlocked")) {
                Set<String> unlocked = new HashSet<>();
                ListTag unlockedList = playerTag.getList("unlocked", Tag.TAG_COMPOUND);
                for (int i = 0; i < unlockedList.size(); i++) {
                    CompoundTag techniqueTag = unlockedList.getCompound(i);
                    unlocked.add(techniqueTag.getString("id"));
                }
                unlockedTechniques.put(uuid, unlocked);
            }

            // 讀取功法等級
            if (playerTag.contains("levels")) {
                Map<String, Integer> levels = new HashMap<>();
                CompoundTag levelsTag = playerTag.getCompound("levels");
                for (String techniqueId : levelsTag.getAllKeys()) {
                    levels.put(techniqueId, levelsTag.getInt(techniqueId));
                }
                techniqueLevels.put(uuid, levels);
            }

            // 讀取已裝備的功法
            if (playerTag.contains("equipped")) {
                Set<String> equipped = new HashSet<>();
                ListTag equippedList = playerTag.getList("equipped", Tag.TAG_COMPOUND);
                for (int i = 0; i < equippedList.size(); i++) {
                    CompoundTag techniqueTag = equippedList.getCompound(i);
                    equipped.add(techniqueTag.getString("id"));
                }
                equippedTechniques.put(uuid, equipped);
            }
        }
    }
}