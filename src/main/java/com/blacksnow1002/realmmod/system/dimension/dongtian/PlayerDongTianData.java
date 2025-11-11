package com.blacksnow1002.realmmod.system.dimension.dongtian;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.Level;
import net.minecraft.server.level.ServerPlayer;


/**
 * 玩家洞天數據
 * 儲存玩家進入/離開洞天的位置信息
 */
public class PlayerDongTianData {

    // 玩家上次離開主世界的位置
    private BlockPos lastExitPos;
    private ResourceKey<Level> lastDimension;

    // 玩家是否已開闢洞天
    private boolean dongTianUnlocked = false;

    // 玩家的修為境界（用於判斷限制）
    private String cultivationLevel = "煉虛期";

    /**
     * 保存玩家離開主世界時的位置
     */
    public void saveExitPoint(ServerPlayer player) {
        this.lastExitPos = player.blockPosition();
        this.lastDimension = player.level().dimension();
    }

    /**
     * 獲取返回位置
     */
    public BlockPos getLastExitPos() {
        return lastExitPos;
    }

    /**
     * 獲取返回維度
     */
    public ResourceKey<Level> getLastDimension() {
        return lastDimension;
    }

    /**
     * 檢查洞天是否已解鎖
     */
    public boolean isDongTianUnlocked() {
        return dongTianUnlocked;
    }

    /**
     * 解鎖洞天
     */
    public void unlockDongTian() {
        this.dongTianUnlocked = true;
    }

    /**
     * 設定修為境界
     */
    public void setCultivationLevel(String level) {
        this.cultivationLevel = level;
    }

    /**
     * 獲取修為境界
     */
    public String getCultivationLevel() {
        return cultivationLevel;
    }

    /**
     * 獲取當前境界的實體限制
     */
    public int getEntityLimit() {
        return DongTianConfig.getEntityLimitForLevel(cultivationLevel);
    }

    /**
     * 獲取當前境界的方塊實體限制
     */
    public int getTileEntityLimit() {
        return DongTianConfig.getTileEntityLimitForLevel(cultivationLevel);
    }

    // ========== NBT序列化 ==========

    /**
     * 保存到NBT
     */
    public CompoundTag save(CompoundTag tag) {
        tag.putBoolean("dongTianUnlocked", dongTianUnlocked);
        tag.putString("cultivationLevel", cultivationLevel);

        if (lastExitPos != null) {
            tag.putInt("lastX", lastExitPos.getX());
            tag.putInt("lastY", lastExitPos.getY());
            tag.putInt("lastZ", lastExitPos.getZ());
        }

        if (lastDimension != null) {
            tag.putString("lastDimension", lastDimension.location().toString());
        }

        return tag;
    }

    /**
     * 從NBT加載
     */
    public static PlayerDongTianData load(CompoundTag tag) {
        PlayerDongTianData data = new PlayerDongTianData();

        data.dongTianUnlocked = tag.getBoolean("dongTianUnlocked");
        data.cultivationLevel = tag.getString("cultivationLevel");

        if (tag.contains("lastX")) {
            int x = tag.getInt("lastX");
            int y = tag.getInt("lastY");
            int z = tag.getInt("lastZ");
            data.lastExitPos = new BlockPos(x, y, z);
        }

        if (tag.contains("lastDimension")) {
            String dimStr = tag.getString("lastDimension");
            ResourceLocation dimLoc = ResourceLocation.parse(dimStr);
            data.lastDimension = ResourceKey.create(Registries.DIMENSION, dimLoc);
        }

        return data;
    }

    /**
     * 從玩家的持久化數據中獲取或創建
     */
    public static PlayerDongTianData get(ServerPlayer player) {
        CompoundTag persistentData = player.getPersistentData();

        if (persistentData.contains("DongTianData")) {
            return load(persistentData.getCompound("DongTianData"));
        } else {
            PlayerDongTianData newData = new PlayerDongTianData();
            // 設定默認返回位置為主世界出生點
            newData.lastDimension = Level.OVERWORLD;
            newData.lastExitPos = player.getServer().overworld().getSharedSpawnPos();
            return newData;
        }
    }

    /**
     * 保存到玩家的持久化數據
     */
    public void saveToPlayer(ServerPlayer player) {
        CompoundTag persistentData = player.getPersistentData();
        CompoundTag dongTianData = new CompoundTag();
        save(dongTianData);
        persistentData.put("DongTianData", dongTianData);
    }

    public void copyFrom(PlayerDongTianData oldData) {
        this.lastExitPos = oldData.lastExitPos;
        this.lastDimension = oldData.lastDimension;
        this.dongTianUnlocked = oldData.dongTianUnlocked;
        this.cultivationLevel = oldData.cultivationLevel;
    }
}