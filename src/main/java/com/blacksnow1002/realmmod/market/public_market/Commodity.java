package com.blacksnow1002.realmmod.market.public_market;

import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;

import java.util.UUID;

public class Commodity {
    public UUID commodityId;
    public UUID sellerId;
    public String sellerName;
    public ItemStack item;
    public int price;
    public long timestamp;

    public static final long COMMODITY_EXPIRE_TIME = 7L * 24L * 60L * 60L * 1000L; //7天過期

    public Commodity(UUID commodityId, UUID sellerId, String sellerName, ItemStack item, int price) {
        this.commodityId = commodityId == null ? UUID.randomUUID() : commodityId;
        this.sellerId = sellerId;
        this.sellerName = sellerName;
        this.item = item.copy();
        this.price = price;
        this.timestamp = System.currentTimeMillis();
    }

    public UUID getCommodityId() {
        return commodityId;
    }

    public UUID getSellerId() {
        return sellerId;
    }

    public String getSellerName() {
        return sellerName;
    }

    public ItemStack getItem() {
        return item;
    }

    public int getPrice() {
        return price;
    }
    public long getTimestamp() {
        return timestamp;
    }

    public boolean isExpired() {
        return System.currentTimeMillis() - this.timestamp > COMMODITY_EXPIRE_TIME;
    }

    public CompoundTag saveNBTData(HolderLookup.Provider registries) {
        CompoundTag tag = new CompoundTag();
        tag.putUUID("commodityId", commodityId);
        tag.putUUID("sellerId", sellerId);
        tag.putString("sellerName", sellerName);
        tag.putInt("price", price);
        tag.put("Item", item.save(registries));
        tag.putLong("timestamp", timestamp);
        return tag;
    }

    public static Commodity loadNBTData(CompoundTag tag, HolderLookup.Provider registries) {
        Commodity commodity = new Commodity(
                tag.getUUID("commodityId"),
                tag.getUUID("sellerId"),
                tag.getString("sellerName"),
                ItemStack.parseOptional(registries, tag.getCompound("Item")),
                tag.getInt("price")

        );
        commodity.timestamp = tag.getLong("timestamp");
        return commodity;
    }
}
