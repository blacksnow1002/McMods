package com.blacksnow1002.realmmod.market.public_market;

import com.blacksnow1002.realmmod.mailbox.SendMail;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.saveddata.SavedData;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class CommodityStorage extends SavedData {

    private static final String DATA_NAME = "commodity_data";

    private final Map<UUID, Commodity> commoditiesMap = new ConcurrentHashMap<>();

    // 玩家已上架物品
    private final Map<UUID, Set<UUID>> playerCommoditiesMap = new ConcurrentHashMap<>();

    // 計算過期日期
    private final Map<Integer, Set<UUID>> expiryBuckets = new ConcurrentHashMap<>();

    public CommodityStorage() {
    }

    // 上架商品
    public void addCommodity(Commodity commodity) {
        commoditiesMap.put(commodity.getCommodityId(),  commodity);
        playerCommoditiesMap
                .computeIfAbsent(commodity.getSellerId(), k -> ConcurrentHashMap.newKeySet())
                .add(commodity.getCommodityId());

        int expiryHour = getExpiryHour(commodity.getTimestamp());
        expiryBuckets
                .computeIfAbsent(expiryHour, k -> ConcurrentHashMap.newKeySet())
                .add(commodity.getCommodityId());
        setDirty();
    }

    private int getExpiryHour(long timestamp) {
        long expireTime = timestamp + Commodity.COMMODITY_EXPIRE_TIME;
        return  (int) (expireTime / (60L * 60L * 1000L));
    }

    // 獲取所有商品
    public List<Commodity> getAllCommodities() {
        return new  ArrayList<>(commoditiesMap.values());
    }

    // 獲取商品
    public Commodity getCommodity(UUID commodityId) {
        return commoditiesMap.get(commodityId);
    }

    // 獲取玩家上架物品
    public List<Commodity> getPlayerCommodities(UUID playerId) {
        Set<UUID> commodities = playerCommoditiesMap.get(playerId);
        if (commodities == null) return Collections.emptyList();

        return commodities.stream()
                .map(commoditiesMap::get)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    // 獲取玩家上架物品數量
    public int getPlayerCommoditiesCount(UUID playerId) {
        Set<UUID> commodities = playerCommoditiesMap.get(playerId);
        return commodities == null ? 0 : commodities.size();
    }

    // 下架商品
    public void removeCommodity(UUID commodityId) {
        Commodity commodity = commoditiesMap.get(commodityId);
        if (commodity != null) {
            Set<UUID> playerCommodities = playerCommoditiesMap.get(commodity.getSellerId());
            if (playerCommodities != null) {
                playerCommodities.remove(commodity.getCommodityId());
                if (playerCommodities.isEmpty()) {
                    playerCommoditiesMap.remove(commodity.getSellerId());
                }
            }

            int expireHour = getExpiryHour(commodity.getTimestamp());
            Set<UUID> bucket = expiryBuckets.get(expireHour);
            if (bucket != null) {
                bucket.remove(commodity.getCommodityId());
                if (bucket.isEmpty()) {
                    expiryBuckets.remove(expireHour);
                }
            }
            commoditiesMap.remove(commodityId);
        }
        setDirty();
    }

    // 處理過期商品
    public int cleanExpiredCommodities(ServerLevel level) {
        long now = System.currentTimeMillis();
        int currentHour = (int) (now / (60L * 60L * 1000L));

        int count = 0;

        // 檢查所有應該已經過期的物品
        Iterator<Map.Entry<Integer, Set<UUID>>> iterator = expiryBuckets.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<Integer, Set<UUID>> entry = iterator.next();
            int expiryHour = entry.getKey();

            if (expiryHour <= currentHour) {
                // 這一小時的商品該過期了
                Set<UUID> commodityIds = entry.getValue();
                for (UUID id : new ArrayList<>(commodityIds)) {
                    Commodity commodity = commoditiesMap.get(id);
                    if (commodity != null && commodity.isExpired()) {
                        removeCommodity(id);
                        returnItemToSeller(level, commodity);
                        count++;
                    }
                }
            }
        }

        return count;
    }

    // 退還過期物品
    private void returnItemToSeller(ServerLevel level, Commodity commodity) {
        SendMail.sendMail(
                level,
                commodity.getSellerId(),
                "萬寶樓",
                0,
                NonNullList.of(ItemStack.EMPTY, commodity.getItem()),
                "您的商品已過期並退還" + commodity.getItem().getHoverName().getString());
    }


    public static CommodityStorage get(Level level) {
        if (level instanceof ServerLevel serverLevel) {
            return serverLevel.getDataStorage().computeIfAbsent(
                    new Factory<>(
                            CommodityStorage::new,
                            CommodityStorage::load,
                            null
                    ),
                    DATA_NAME
            );
        }
        return null;
    }

    @Override
    public CompoundTag save(CompoundTag tag, HolderLookup.Provider registries) {
        ListTag list = new ListTag();

        for (Commodity c : commoditiesMap.values()) {
            list.add(c.saveNBTData(registries));
        }

        tag.put("commodities", list);
        return tag;
    }

    public static CommodityStorage load(CompoundTag tag, HolderLookup.Provider registries) {
        CommodityStorage storage = new CommodityStorage();
        ListTag list = tag.getList("commodities", Tag.TAG_COMPOUND);

        for (Tag t : list) {
            Commodity commodity = Commodity.loadNBTData((CompoundTag) t, registries);
            storage.commoditiesMap.put(commodity.getCommodityId(), commodity);

            storage.playerCommoditiesMap
                    .computeIfAbsent(commodity.getSellerId(), k -> ConcurrentHashMap.newKeySet())
                    .add(commodity.getCommodityId());

            int expiryHour = storage.getExpiryHour(commodity.getTimestamp());
            storage.expiryBuckets
                    .computeIfAbsent(expiryHour, k -> ConcurrentHashMap.newKeySet())
                    .add(commodity.getCommodityId());
        }

        return storage;
    }
}
