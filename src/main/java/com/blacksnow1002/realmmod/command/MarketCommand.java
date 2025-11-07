package com.blacksnow1002.realmmod.command;

import com.blacksnow1002.realmmod.market.public_market.Commodity;
import com.blacksnow1002.realmmod.market.public_market.CommodityStorage;
import com.blacksnow1002.realmmod.market.public_market.MarketManager;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.UUID;

public class MarketCommand {

    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("MM-dd HH:mm");

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("market")
                // 查看市場所有商品
                .then(Commands.literal("view")
                        .executes(ctx -> {
                            ServerPlayer player = ctx.getSource().getPlayerOrException();
                            CommodityStorage storage = CommodityStorage.get(player.serverLevel());

                            if (storage == null) {
                                player.sendSystemMessage(Component.literal("❌ 市場系統錯誤"));
                                return 0;
                            }

                            List<Commodity> commodities = storage.getAllCommodities();

                            if (commodities.isEmpty()) {
                                player.sendSystemMessage(Component.literal("📦 市場目前沒有任何物品"));
                                return 1;
                            }

                            player.sendSystemMessage(Component.literal("§6§l=== 萬寶樓市場 ==="));
                            player.sendSystemMessage(Component.literal("§7共有 §e" + commodities.size() + " §7件商品"));

                            for (int i = 0; i < Math.min(commodities.size(), 20); i++) { // 限制顯示前20個
                                Commodity c = commodities.get(i);

                                // 顯示商品信息
                                Component itemName = Component.literal("§f" + c.itemStack.getHoverName().getString());
                                Component count = Component.literal(" §7x" + c.itemStack.getCount());
                                Component price = Component.literal(" §e" + c.price + "靈石");
                                Component seller = Component.literal(" §7(賣家: " + c.sellerName + ")");

                                Component message = Component.literal("§7[" + (i+1) + "] ")
                                        .append(itemName)
                                        .append(count)
                                        .append(price)
                                        .append(seller);

                                // 購買按鈕
                                Component buyButton = Component.literal(" §a[購買]")
                                        .withStyle(Style.EMPTY
                                                .withColor(ChatFormatting.GREEN)
                                                .withClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND,
                                                        "/market buy " + c.getCommodityId().toString())));

                                player.sendSystemMessage(message.copy().append(buyButton));
                            }

                            if (commodities.size() > 20) {
                                player.sendSystemMessage(Component.literal("§7... 還有 " + (commodities.size() - 20) + " 件商品"));
                            }

                            return 1;
                        })
                )

                // 上架手持物品
                .then(Commands.literal("sell")
                        .then(Commands.argument("price", IntegerArgumentType.integer(1))
                                .executes(ctx -> {
                                    ServerPlayer player = ctx.getSource().getPlayerOrException();
                                    int price = IntegerArgumentType.getInteger(ctx, "price");
                                    ItemStack item = player.getMainHandItem();

                                    if (item.isEmpty()) {
                                        player.sendSystemMessage(Component.literal("❌ 請手持要出售的物品"));
                                        return 0;
                                    }

                                    // 複製物品用於上架
                                    ItemStack itemToSell = item.copy();

                                    if (MarketManager.sellItem(player, itemToSell, price)) {
                                        // 上架成功後清空手持物品
                                        item.setCount(0);
                                        return 1;
                                    }

                                    return 0;
                                })
                        )
                )

                // 購買商品
                .then(Commands.literal("buy")
                        .then(Commands.argument("commodityId", StringArgumentType.string())
                                .executes(ctx -> {
                                    ServerPlayer player = ctx.getSource().getPlayerOrException();
                                    String commodityIdStr = StringArgumentType.getString(ctx, "commodityId");

                                    try {
                                        UUID commodityId = UUID.fromString(commodityIdStr);
                                        return MarketManager.buyItem(player, commodityId) ? 1 : 0;
                                    } catch (IllegalArgumentException e) {
                                        player.sendSystemMessage(Component.literal("❌ 無效的商品ID"));
                                        return 0;
                                    }
                                })
                        )
                )

                // 下架商品
                .then(Commands.literal("unsell")
                        .then(Commands.argument("commodityId", StringArgumentType.string())
                                .executes(ctx -> {
                                    ServerPlayer player = ctx.getSource().getPlayerOrException();
                                    String commodityIdStr = StringArgumentType.getString(ctx, "commodityId");

                                    try {
                                        UUID commodityId = UUID.fromString(commodityIdStr);
                                        return MarketManager.unsellItem(player, commodityId) ? 1 : 0;
                                    } catch (IllegalArgumentException e) {
                                        player.sendSystemMessage(Component.literal("❌ 無效的商品ID"));
                                        return 0;
                                    }
                                })
                        )
                )

                // 查看自己的商品
                .then(Commands.literal("myItems")
                        .executes(ctx -> {
                            ServerPlayer player = ctx.getSource().getPlayerOrException();
                            CommodityStorage storage = CommodityStorage.get(player.serverLevel());

                            if (storage == null) {
                                player.sendSystemMessage(Component.literal("❌ 市場系統錯誤"));
                                return 0;
                            }

                            // 使用索引快速查詢
                            List<Commodity> myCommodities = storage.getPlayerCommodities(player.getUUID());

                            if (myCommodities.isEmpty()) {
                                player.sendSystemMessage(Component.literal("📦 你目前沒有上架任何物品"));
                                player.sendSystemMessage(Component.literal("§7使用 /market sell <價格> 來上架手持物品"));
                                return 1;
                            }

                            player.sendSystemMessage(Component.literal("§6§l=== 我的上架商品 ==="));
                            player.sendSystemMessage(Component.literal("§7共有 §e" + myCommodities.size() + " §7件商品"));

                            for (Commodity c : myCommodities) {
                                String timeStr = DATE_FORMAT.format(new Date(c.getTimestamp()));

                                Component message = Component.literal("§f" + c.itemStack.getHoverName().getString())
                                        .append(Component.literal(" §7x" + c.itemStack.getCount()))
                                        .append(Component.literal(" §e" + c.price + "靈石"))
                                        .append(Component.literal(" §8(" + timeStr + ")"));

                                Component unsellButton = Component.literal(" §c[下架]")
                                        .withStyle(Style.EMPTY
                                                .withColor(ChatFormatting.RED)
                                                .withClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND,
                                                        "/market unsell " + c.getCommodityId().toString())));

                                player.sendSystemMessage(message.copy().append(unsellButton));
                            }

                            return 1;
                        })
                )

                // 管理員命令:清理過期商品
                .then(Commands.literal("clean")
                        .requires(source -> source.hasPermission(2)) // 需要OP權限
                        .executes(ctx -> {
                            ServerPlayer player = ctx.getSource().getPlayerOrException();
                            CommodityStorage storage = CommodityStorage.get(player.serverLevel());

                            if (storage == null) {
                                player.sendSystemMessage(Component.literal("❌ 市場系統錯誤"));
                                return 0;
                            }

                            int cleaned = storage.cleanExpiredCommodities(player.serverLevel());
                            player.sendSystemMessage(Component.literal("✅ 已清理 " + cleaned + " 件過期商品"));

                            return 1;
                        })
                )

                // 顯示幫助信息
                .executes(ctx -> {
                    ServerPlayer player = ctx.getSource().getPlayerOrException();
                    player.sendSystemMessage(Component.literal("§6§l=== 萬寶樓使用說明 ==="));
                    player.sendSystemMessage(Component.literal("§e/market view §7- 查看市場所有商品"));
                    player.sendSystemMessage(Component.literal("§e/market sell <價格> §7- 上架手持物品"));
                    player.sendSystemMessage(Component.literal("§e/market buy <ID> §7- 購買商品"));
                    player.sendSystemMessage(Component.literal("§e/market myItems §7- 查看我的上架商品"));
                    player.sendSystemMessage(Component.literal("§e/market unsell <ID> §7- 下架商品"));
                    return 1;
                })
        );
    }
}