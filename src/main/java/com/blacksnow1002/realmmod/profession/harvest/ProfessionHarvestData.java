package com.blacksnow1002.realmmod.profession.harvest;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.Tag;

import java.util.HashMap;
import java.util.Map;

public class ProfessionHarvestData implements IProfessionHarvestData {
    private int rank = 0; // 0=未入門, 1~9品
    private int exp = 0;
    private final int[] requiredExp = {0, 984100, 328000, 109300, 36400, 12100, 4000, 1300, 400, 100};

    // 每個方塊ID的成功率累積(稀有產物失敗時增加)
    private Map<String, Double> successRates = new HashMap<>();

    // 每個品級的首次成功記錄
    private boolean[] firstSuccess = new boolean[10];

    // 心魔狀態
    private boolean isHeartDemon = false;

    // 天材地寶每日出現次數(低品級採集時) 每品級不同
    private int[] dailyTreasureCount = new int[10];

    // 上次重置日期
    private long lastResetDay = 0;

    @Override
    public int getRank() {
        return rank;
    }

    @Override
    public void setRank(int rank) { this.rank = Math.max(0, Math.min(9, rank)); }


    @Override
    public int getExp() {
        return exp;
    }

    @Override
    public void setExp(int exp) {
        this.exp = exp;
    }

    @Override
    public void addExp(int exp) {
        if (this.rank > 0 && this.rank <= 9) {
            this.exp = Math.min(requiredExp[this.rank], this.exp + exp);
        }
    }

    @Override
    public void subtractExp(int exp) {
        this.exp = Math.max(0, this.exp - exp);
    }

    @Override
    public int getRequiredExp() {
        if (rank >= 0 && rank <= 9) {
            return requiredExp[rank];
        }
        return 0;
    }

    @Override
    public double getSuccessRateBonus(String blockId) {
        return successRates.getOrDefault(blockId, 0.0);
    }

    @Override
    public void setSuccessRateBonus(String blockId, double rate) {
        successRates.put(blockId, Math.min(0.3, rate)); //加成最多30% = 總成功率80%
    }

    @Override
    public void addSuccessRateBonus(String blockId, double rate) {
        double successRate = getSuccessRateBonus(blockId);
        setSuccessRateBonus(blockId, successRate + rate);
    }

    @Override
    public boolean hasFirstSuccess(int rank) {
        if (rank >= 0 && rank < firstSuccess.length) {
            return firstSuccess[rank];
        }
        return false;
    }

    @Override
    public void setFirstSuccess(int rank) {
        if (rank >= 0 && rank < firstSuccess.length) {
            this.firstSuccess[rank] = true;
        }
    }

    @Override
    public boolean isHeartDemon() {
        return isHeartDemon;
    }
    @Override
    public void setHeartDemon(boolean state) {
        this.isHeartDemon = state;
    }

    @Override
    public int getDailyTreasureCount(int rank) {
        return dailyTreasureCount[rank];
    }

    @Override
    public void incrementDailyTreasure(int rank) {
        dailyTreasureCount[rank]++;
    }

    @Override
    public void clearDailyTreasure() {
        dailyTreasureCount = new int[10];
    }

    @Override
    public CompoundTag saveNBTData() {
        CompoundTag tag = new CompoundTag();
        tag.putInt("HarvestRank", rank);
        tag.putInt("HarvestExp", exp);
        tag.putBoolean("HeartDemon", isHeartDemon);

        // 保存成功率
        CompoundTag rateTag = new CompoundTag();
        for (Map.Entry<String, Double> entry : successRates.entrySet()) {
            rateTag.putDouble(entry.getKey(), entry.getValue());
        }
        tag.put("HarvestSuccessRates", rateTag);

        // 保存首次成功記錄
        ListTag firstList = new ListTag();
        for (boolean b : firstSuccess) {
            firstList.add(StringTag.valueOf(String.valueOf(b)));
        }
        tag.put("HarvestFirstSuccess", firstList);

        // 保存每日天材地寶計數
        ListTag dailyTreasureList = new ListTag();
        for (int i :  dailyTreasureCount) {
            dailyTreasureList.add(StringTag.valueOf(String.valueOf(i)));
        }
        tag.put("HarvestDailyTreasure", dailyTreasureList);

        return tag;
    }

    @Override
    public void loadNBTData(CompoundTag tag) {
        this.rank = tag.getInt("HarvestRank");
        this.exp = tag.getInt("HarvestExp");
        this.isHeartDemon = tag.getBoolean("HeartDemon");

        // 讀取成功率
        if (tag.contains("HarvestSuccessRates")) {
            CompoundTag rateTag = tag.getCompound("HarvestSuccessRates");
            successRates.clear();
            for (String key : rateTag.getAllKeys()) {
                successRates.put(key, rateTag.getDouble(key));
            }
        }

        // 讀取首次成功記錄
        if (tag.contains("HarvestFirstSuccess")) {
            ListTag list = tag.getList("HarvestFirstSuccess", Tag.TAG_STRING);
            for (int i = 0; i < Math.min(list.size(), firstSuccess.length); i++) {
                firstSuccess[i] = Boolean.parseBoolean(list.getString(i));
            }
        }

        // 讀取每日天材地寶計數
        if (tag.contains("HarvestDailyTreasure")) {
            ListTag dailyTreasureList = tag.getList("HarvestDailyTreasure", Tag.TAG_STRING);
            for (int i = 0; i <Math.min(dailyTreasureList.size(), dailyTreasureCount.length); i++) {
                dailyTreasureCount[i] = Integer.parseInt(dailyTreasureList.getString(i));
            }
        }
    }
}
