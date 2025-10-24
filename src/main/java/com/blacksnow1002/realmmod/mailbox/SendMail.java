package com.blacksnow1002.realmmod.mailbox;

import net.minecraft.core.NonNullList;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;

import java.util.UUID;

public class SendMail {
    public static void sendMail(ServerLevel level, UUID targetId, String senderName, int money, NonNullList<ItemStack> items, String message) {
        MailboxStorage storage = MailboxStorage.get(level);

        Mail mail = new Mail(UUID.randomUUID(), targetId, senderName, money, items, message);

        storage.addMail(targetId, mail);

        ServerPlayer onlinePlayer = level.getServer().getPlayerList().getPlayer(targetId);
        if (onlinePlayer != null) {
            ClientMailCache.addMail(onlinePlayer.getUUID(), mail);
            onlinePlayer.sendSystemMessage(Component.literal("§e[郵件] §f你收到了來自 §a" + senderName + " §f的郵件"));
        }
    }
}
