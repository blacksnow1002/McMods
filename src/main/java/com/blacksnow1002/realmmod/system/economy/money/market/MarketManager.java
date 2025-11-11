package com.blacksnow1002.realmmod.system.economy.money.market;

import com.blacksnow1002.realmmod.core.capability.ModCapabilities;
import com.blacksnow1002.realmmod.system.mailbox.SendMail;
import net.minecraft.core.NonNullList;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;

import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;

public class MarketManager {

    private static final int MAX_ITEMS_PER_PLAYER = 5; // 每人上架上限

    private static boolean isItemTradable(ItemStack itemStack) {
        if (itemStack.isEmpty()) return false;
        if (itemStack.getCount() <= 0) return false;

        //TODO: 加入unTradable item NBT標籤
        return true;
    }

    public static boolean sellItem(ServerPlayer player, ItemStack item, int price) {

        if (!isItemTradable(item)) {
            player.sendSystemMessage(Component.literal("該物品無法交易"));
            return false;
        }

        CommodityStorage storage = CommodityStorage.get(player.serverLevel());

        int hadSold = storage.getPlayerCommoditiesCount(player.getUUID());
        if (hadSold >= MAX_ITEMS_PER_PLAYER) {
            player.sendSystemMessage(Component.literal("已達上架物品上限(" + MAX_ITEMS_PER_PLAYER + ")請先下架物品"));
            return false;
        }

        Commodity commodity = new Commodity(UUID.randomUUID(), player.getUUID(), player.getName().getString(), item, price);
        storage.addCommodity(commodity);

        player.sendSystemMessage(Component.literal("✅ 已上架物品：" + item.getHoverName().getString() + "數量" + item.getCount() + "，價格：" + price + "靈幣"));

        return true;
    }

    public static boolean buyItem(ServerPlayer buyer, UUID commodityId) {
        CommodityStorage storage = CommodityStorage.get(buyer.serverLevel());
        Commodity commodity = storage.getCommodity(commodityId);

        if (commodity.sellerId.equals(buyer.getUUID())) {
            buyer.sendSystemMessage(Component.literal("❌ 不能購買自己的物品"));
            return false;
        }

        // 讀取 buyer 的 money capability
        AtomicBoolean hasEnoughMoney = new  AtomicBoolean(false);
        final Commodity finalCommodity = commodity;
        buyer.getCapability(ModCapabilities.MONEY_CAP).ifPresent(money -> {
           if (money.getMoney() < finalCommodity.price) {
               buyer.sendSystemMessage(Component.literal("❌ 靈幣不足 (需要: " + finalCommodity.price + ")"));
           } else {
               money.subtractMoney(finalCommodity.price);
               hasEnoughMoney.set(true);
           }
        });
        if (!hasEnoughMoney.get())  return false;

        storage.removeCommodity(commodity.commodityId);

        // 發送郵件給賣家
        SendMail.sendMail(
                buyer.serverLevel(),
                commodity.sellerId,
                "萬寶樓",
                commodity.price,
                NonNullList.create(),
                "你售出了物品：" + commodity.itemStack.getHoverName().getString() + "獲得靈幣" + commodity.price
        );

        // 發送郵件給買家（給物品）
        NonNullList<ItemStack> items = NonNullList.create();
        items.add(commodity.itemStack);
        SendMail.sendMail(
                buyer.serverLevel(),
                buyer.getUUID(),
                "萬寶樓",
                0,
                items,
                "你購買了物品：" + commodity.itemStack.getHoverName().getString()
        );

        buyer.sendSystemMessage(Component.literal("✅ 購買成功，物品將由萬寶樓寄送"));

        return true;
    }

    public static boolean unsellItem(ServerPlayer player, UUID commodityId) {
        CommodityStorage storage = CommodityStorage.get(player.serverLevel());
        Commodity commodity = storage.getCommodity(commodityId);

        if (commodity == null) {
            player.sendSystemMessage(Component.literal("❌ 找不到該商品"));
            return false;
        }

        if (!commodity.sellerId.equals(player.getUUID())) {
            player.sendSystemMessage(Component.literal("❌ 找不到該商品"));
            return false;
        }

        // 退還物品給玩家
        NonNullList<ItemStack> items = NonNullList.create();
        items.add(commodity.itemStack);
        SendMail.sendMail(
                player.serverLevel(),
                player.getUUID(),
                "萬寶樓",
                0,
                items,
                "你下架了物品:" + commodity.itemStack.getHoverName().getString()
        );

        storage.removeCommodity(commodityId);
        player.sendSystemMessage(Component.literal("✅ 下架成功,物品將由萬寶樓寄送"));

        return true;
    }
}
