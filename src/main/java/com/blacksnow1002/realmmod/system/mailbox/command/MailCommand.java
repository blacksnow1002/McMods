package com.blacksnow1002.realmmod.system.mailbox.command;

import com.blacksnow1002.realmmod.system.mailbox.client.cache.ClientMailCache;
import com.blacksnow1002.realmmod.system.mailbox.Mail;
import com.blacksnow1002.realmmod.system.mailbox.MailboxMenuProvider;
import com.blacksnow1002.realmmod.system.mailbox.MailboxStorage;
import com.blacksnow1002.realmmod.core.network.ModMessages;
import com.blacksnow1002.realmmod.system.mailbox.network.S2C.MailSyncPacket;
import com.mojang.authlib.GameProfile;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.GameProfileArgument;
import net.minecraft.commands.arguments.item.ItemArgument;
import net.minecraft.commands.arguments.item.ItemInput;
import net.minecraft.core.NonNullList;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

public class MailCommand {

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher, CommandBuildContext context) {
        dispatcher.register(Commands.literal("mail")
                // 僅附帶金錢的郵件
                .then(Commands.literal("sendMoneyMail")
                        .requires(source -> source.hasPermission(2))
                        .then(Commands.argument("target", GameProfileArgument.gameProfile())
                                .then(Commands.argument("sender", StringArgumentType.string())
                                        .then(Commands.argument("amount", IntegerArgumentType.integer(1))
                                                .then(Commands.argument("message", StringArgumentType.greedyString())
                                                        .executes(ctx -> sendMail(
                                                                ctx,
                                                                IntegerArgumentType.getInteger(ctx, "amount"),
                                                                NonNullList.create()
                                                        ))
                                                )
                                        )
                                )
                        )
                )
                // 僅附帶物品的郵件
                .then(Commands.literal("sendItemMail")
                        .requires(source -> source.hasPermission(2))
                        .then(Commands.argument("target", GameProfileArgument.gameProfile())
                                .then(Commands.argument("sender", StringArgumentType.string())
                                        .then(Commands.argument("item", ItemArgument.item(context))
                                                .then(Commands.argument("count", IntegerArgumentType.integer(1, 64))
                                                        .then(Commands.argument("message", StringArgumentType.greedyString())
                                                                .executes(ctx -> sendMailWithItem(ctx))
                                                        )
                                                )
                                        )
                                )
                        )
                )
                // 附帶金錢+物品的郵件
                .then(Commands.literal("sendAllMail")
                        .requires(source -> source.hasPermission(2))
                        .then(Commands.argument("target", GameProfileArgument.gameProfile())
                                .then(Commands.argument("sender", StringArgumentType.string())
                                        .then(Commands.argument("money", IntegerArgumentType.integer(0))
                                                .then(Commands.argument("item", ItemArgument.item(context))
                                                        .then(Commands.argument("count", IntegerArgumentType.integer(1, 64))
                                                                .then(Commands.argument("message", StringArgumentType.greedyString())
                                                                        .executes(ctx -> sendAllMail(ctx))
                                                                )
                                                        )
                                                )
                                        )
                                )
                        )
                )
                // 發送手持物品
                .then(Commands.literal("sendhand")
                        .requires(source -> source.hasPermission(2))
                        .then(Commands.argument("target", GameProfileArgument.gameProfile())
                                .then(Commands.argument("message", StringArgumentType.greedyString())
                                        .executes(ctx -> sendHandItem(ctx, 0))
                                )
                        )
                )
                // 發送手持物品
                .then(Commands.literal("sendhand")
                        .requires(source -> source.hasPermission(2))
                        .then(Commands.argument("target", GameProfileArgument.gameProfile())
                                .then(Commands.argument("message", StringArgumentType.greedyString())
                                        .executes(ctx -> sendHandItem(ctx, 0))
                                )
                        )
                )
                // ✨ 新增：發送手持物品+金錢
                .then(Commands.literal("sendhandmoney")
                        .requires(source -> source.hasPermission(2))
                        .then(Commands.argument("target", GameProfileArgument.gameProfile())
                                .then(Commands.argument("money", IntegerArgumentType.integer(1))
                                        .then(Commands.argument("message", StringArgumentType.greedyString())
                                                .executes(ctx -> sendHandItem(
                                                        ctx,
                                                        IntegerArgumentType.getInteger(ctx, "money")
                                                ))
                                        )
                                )
                        )
                )
                // 查看自己的郵件
                .then(Commands.literal("openMailbox")
                        .executes(ctx -> {
                            ServerPlayer player = ctx.getSource().getPlayerOrException();

                            player.openMenu(new MailboxMenuProvider(player.getInventory()));
                            return Command.SINGLE_SUCCESS;
                        })
                )
                // 清空玩家郵件
                .then(Commands.literal("clear")
                        .requires(source -> source.hasPermission(2))
                        .then(Commands.argument("target", GameProfileArgument.gameProfile())
                                .executes(ctx -> clearMails(ctx))
                        )
                )
                // 查看玩家郵件數量
                .then(Commands.literal("check")
                        .requires(source -> source.hasPermission(2))
                        .then(Commands.argument("target", GameProfileArgument.gameProfile())
                                .executes(ctx -> checkMails(ctx))
                        )
                )
        );
    }

    /**
     * 通用發送郵件方法
     * 支援離線玩家
     */
    private static int sendMail(CommandContext<CommandSourceStack> ctx, int money, NonNullList<ItemStack> items) throws CommandSyntaxException {
        Collection<GameProfile> profiles = GameProfileArgument.getGameProfiles(ctx, "target");
        String message = StringArgumentType.getString(ctx, "message");
        CommandSourceStack source = ctx.getSource();
        String senderName = StringArgumentType.getString(ctx, "sender");

        for (GameProfile profile : profiles) {
            UUID targetId = profile.getId();
            String targetName = profile.getName();

            // 創建郵件
            Mail mail = new Mail(
                    UUID.randomUUID(),
                    targetId,
                    senderName,
                    money,
                    items,
                    message
            );

            // 儲存到伺服器 (離線玩家也會儲存)
            MailboxStorage storage = MailboxStorage.get(source.getLevel());
            storage.addMail(targetId, mail);

            // 如果目標玩家在線，同步到客戶端
            ServerPlayer onlinePlayer = source.getServer().getPlayerList().getPlayer(targetId);
            if (onlinePlayer != null) {
                ClientMailCache.addMail(onlinePlayer.getUUID(), mail); //TODO: 改封包
                onlinePlayer.sendSystemMessage(Component.literal("§e[郵件] §f你收到了來自 " + senderName + " 的郵件"));
            }

            // 回饋訊息
            String status = onlinePlayer != null ? "§a(在線)" : "§7(離線)";
            source.sendSuccess(() -> Component.literal("§a已發送郵件給 " + targetName + " " + status), false);
        }

        return 1;
    }

    /**
     * /mail senditem <玩家> <物品> <數量> <訊息>
     */
    private static int sendMailWithItem(CommandContext<CommandSourceStack> ctx) throws CommandSyntaxException {
        ItemInput itemInput = ItemArgument.getItem(ctx, "item");
        int count = IntegerArgumentType.getInteger(ctx, "count");

        ItemStack itemStack = itemInput.createItemStack(count, false);
        NonNullList<ItemStack> items = NonNullList.create();
        items.add(itemStack);

        return sendMail(ctx, 0, items);
    }

    /**
     * ✨ /mail sendall <玩家> <金錢> <物品> <數量> <訊息>
     * 同時發送金錢和物品
     */
    private static int sendAllMail(CommandContext<CommandSourceStack> ctx) throws CommandSyntaxException {
        int money = IntegerArgumentType.getInteger(ctx, "money");
        ItemInput itemInput = ItemArgument.getItem(ctx, "item");
        int count = IntegerArgumentType.getInteger(ctx, "count");

        ItemStack itemStack = itemInput.createItemStack(count, false);
        NonNullList<ItemStack> items = NonNullList.create();
        items.add(itemStack);

        return sendMail(ctx, money, items);
    }

    /**
     * /mail clear <玩家> - 清空指定玩家的郵件
     */
    private static int clearMails(CommandContext<CommandSourceStack> ctx) throws CommandSyntaxException {
        Collection<GameProfile> profiles = GameProfileArgument.getGameProfiles(ctx, "target");
        CommandSourceStack source = ctx.getSource();

        for (GameProfile profile : profiles) {
            UUID targetId = profile.getId();
            String targetName = profile.getName();

            MailboxStorage storage = MailboxStorage.get(source.getLevel());
            List<Mail> mails = storage.getMails(targetId);
            int count = mails.size();

            mails.clear();
            storage.setDirty();

            // 如果玩家在線，同步
            ServerPlayer onlinePlayer = source.getServer().getPlayerList().getPlayer(targetId);
            if (onlinePlayer != null) {
                ModMessages.sendToPlayer(new MailSyncPacket(targetId, List.of()), onlinePlayer);
            }

            source.sendSuccess(() -> Component.literal(
                    "§a已清空 " + targetName + " 的 " + count + " 封郵件"
            ), true);
        }

        return 1;
    }

    /**
     * /mail check <玩家> - 查看指定玩家的郵件數量
     */
    private static int checkMails(CommandContext<CommandSourceStack> ctx) throws CommandSyntaxException {
        Collection<GameProfile> profiles = GameProfileArgument.getGameProfiles(ctx, "target");
        CommandSourceStack source = ctx.getSource();

        for (GameProfile profile : profiles) {
            UUID targetId = profile.getId();
            String targetName = profile.getName();

            MailboxStorage storage = MailboxStorage.get(source.getLevel());
            List<Mail> mails = storage.getMails(targetId);

            String status = source.getServer().getPlayerList().getPlayer(targetId) != null ? "§a在線" : "§7離線";
            source.sendSuccess(() -> Component.literal(
                    "§e玩家 " + targetName + " (" + status + "§e) 目前有 §a" + mails.size() + " §e封郵件"
            ), false);
        }

        return 1;
    }

    /**
     * /mail sendhand <玩家> <訊息>
     * /mail sendhandmoney <玩家> <金錢> <訊息>
     * 發送手持物品 (支援 RPG 物品)
     */
    private static int sendHandItem(CommandContext<CommandSourceStack> ctx, int money) throws CommandSyntaxException {
        ServerPlayer sender = ctx.getSource().getPlayerOrException();
        Collection<GameProfile> profiles = GameProfileArgument.getGameProfiles(ctx, "target");
        String message = StringArgumentType.getString(ctx, "message");

        ItemStack heldItem = sender.getMainHandItem();

        if (heldItem.isEmpty()) {
            ctx.getSource().sendFailure(Component.literal("§c你手上沒有拿任何物品！"));
            return 0;
        }

        // 複製物品 (保留所有 NBT)
        ItemStack itemToSend = heldItem.copy();
        NonNullList<ItemStack> items = NonNullList.create();
        items.add(itemToSend);

        String senderName = sender.getName().getString();
        CommandSourceStack source = ctx.getSource();
        MailboxStorage storage = MailboxStorage.get(source.getLevel());

        for (GameProfile profile : profiles) {
            UUID targetId = profile.getId();
            String targetName = profile.getName();

            // 創建郵件
            Mail mail = new Mail(
                    UUID.randomUUID(),
                    targetId,
                    senderName,
                    money,
                    items,
                    message
            );

            // 儲存
            storage.addMail(targetId, mail);

            // 同步
            ServerPlayer onlinePlayer = source.getServer().getPlayerList().getPlayer(targetId);
            if (onlinePlayer != null) {
                List<Mail> updatedMails = storage.getMails(targetId);
                ModMessages.sendToPlayer(new MailSyncPacket(targetId, updatedMails), onlinePlayer);
                onlinePlayer.sendSystemMessage(Component.literal(
                        "§e[郵件] §f你收到了來自 " + senderName + " 的郵件"
                ));
            }

            // 獲取物品顯示名稱
            String itemName = heldItem.has(DataComponents.CUSTOM_NAME) ?
                    heldItem.get(DataComponents.CUSTOM_NAME).getString() :
                    heldItem.getItem().getDescription().getString();

            String status = onlinePlayer != null ? "§a(在線)" : "§7(離線)";
            String moneyInfo = money > 0 ? " §7+ §a" + money + " 金錢" : "";
            source.sendSuccess(() -> Component.literal(
                    "§a已發送 §e" + itemName + moneyInfo + " §a給 " + targetName + " " + status
            ), false);
        }

        return 1;
    }
}