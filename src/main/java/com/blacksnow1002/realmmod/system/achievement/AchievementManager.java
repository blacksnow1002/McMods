package com.blacksnow1002.realmmod.system.achievement;

import com.blacksnow1002.realmmod.RealmMod;
import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.advancements.AdvancementProgress;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.PlayerAdvancements;
import net.minecraft.server.level.ServerPlayer;

import java.util.ArrayList;
import java.util.List;

public class AchievementManager {
    private static final String MOD_ID = RealmMod.MOD_ID;

    //授予成就
    public static boolean grantAchievement(ServerPlayer player, CustomAchievements achievement) {
        if (achievement.getParent() != null && !hasAchievement(player, achievement.getParent())) {
            return false;
        }

        ResourceLocation achievementId = achievement.getId();
        PlayerAdvancements playerAdvancements = player.getAdvancements();
        AdvancementHolder holder = player.server.getAdvancements().get(achievementId);

        if (holder == null) return false;

        AdvancementProgress progress = playerAdvancements.getOrStartProgress(holder);

        if (progress.isDone()) return false;

        for (String criterion : progress.getRemainingCriteria()) {
            playerAdvancements.award(holder, criterion);
        }

        return true;
    }

    public static boolean hasAchievement(ServerPlayer player, CustomAchievements parent) {
        ResourceLocation achievementId = parent.getId();
        PlayerAdvancements playerAdvancements = player.getAdvancements();
        AdvancementHolder holder = player.server.getAdvancements().get(achievementId);

        if (holder == null) return false;

        return playerAdvancements.getOrStartProgress(holder).isDone();
    }

    // 獲取玩家的成就進度百分比
    public static float getProgress(ServerPlayer player, CustomAchievements achievement) {
        ResourceLocation achievementId = achievement.getId();
        PlayerAdvancements playerAdvancements = player.getAdvancements();
        AdvancementHolder holder = player.server.getAdvancements().get(achievementId);

        if (holder == null) {
            return 0.0f;
        }

        AdvancementProgress progress = playerAdvancements.getOrStartProgress(holder);
        return progress.getPercent();
    }



    // 獲取玩家所有已完成的成就
    public static List<CustomAchievements> getCompletedAchievements(ServerPlayer player) {
        List<CustomAchievements> completed = new ArrayList<>();

        for (CustomAchievements achievement : CustomAchievements.values()) {
            if (hasAchievement(player, achievement)) {
                completed.add(achievement);
            }
        }

        return completed;
    }

    // 撤銷成就
    public static boolean revokeAchievement(ServerPlayer player, CustomAchievements achievement) {
        ResourceLocation achievementId = achievement.getId();
        PlayerAdvancements playerAdvancements = player.getAdvancements();
        AdvancementHolder holder = player.server.getAdvancements().get(achievementId);

        if (holder == null) {
            return false;
        }

        AdvancementProgress progress = playerAdvancements.getOrStartProgress(holder);

        if (!progress.isDone()) {
            return false; // 沒有獲得過
        }

        // 撤銷所有條件
        for (String criterion : progress.getCompletedCriteria()) {
            playerAdvancements.revoke(holder, criterion);
        }

        return true;
    }

    // 批量授予成就（自動處理前置）
    public static List<CustomAchievements> grantAchievementWithPrerequisites(ServerPlayer player, CustomAchievements achievement) {
        List<CustomAchievements> granted = new ArrayList<>();

        // 遞歸授予所有前置成就
        if (achievement.getParent() != null && !hasAchievement(player, achievement.getParent())) {
            granted.addAll(grantAchievementWithPrerequisites(player, achievement.getParent()));
        }

        // 授予目標成就
        if (grantAchievement(player, achievement)) {
            granted.add(achievement);
        }

        return granted;
    }
}
