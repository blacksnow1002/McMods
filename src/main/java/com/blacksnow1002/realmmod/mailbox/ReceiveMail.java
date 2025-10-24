package com.blacksnow1002.realmmod.mailbox;

import com.blacksnow1002.realmmod.capability.ModCapabilities;
import net.minecraft.core.NonNullList;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

import java.util.List;
import java.util.UUID;


public class ReceiveMail {

    public static boolean receiveMail(Player player, UUID mailId) {
        if (!(player instanceof ServerPlayer serverPlayer)) return false;
        MailboxStorage storage = MailboxStorage.get(serverPlayer.level());
        if (storage == null) {
            serverPlayer.sendSystemMessage(Component.literal("§c無法獲取郵件系統"));
            return false;
        }

        List<Mail> mails = storage.getMails(serverPlayer.getUUID());
        Mail targetMail = null;

        for (Mail mail : mails) {
            if (mail.getMailId().equals(mailId)) {
                targetMail = mail;
                break;
            }
        }

        if (targetMail == null) {
            serverPlayer.sendSystemMessage(Component.literal("§c找不到該郵件"));
            return false;
        }

        if (!serverPlayer.getUUID().equals(targetMail.getTargetId())) {
            serverPlayer.sendSystemMessage(Component.literal("這不是你的郵件"));
            return false;
        }

        int money = targetMail.getMoney();
        NonNullList<ItemStack> items = targetMail.getItems();

        int emptySlots = 0;
        for (int i=0;i<36;i++) {
            if (serverPlayer.getInventory().getItem(i).isEmpty()) emptySlots ++;
        }
        System.out.println("emptySlots: " + emptySlots);

        if (emptySlots < items.size()) {
            serverPlayer.sendSystemMessage(Component.literal("背包空間不足，無法領取信件"));
            return false;
        }

        for (ItemStack itemStack : items) {
            if (!itemStack.isEmpty()) {
                serverPlayer.getInventory().add(itemStack);
            }
        }

        if (money > 0) {
            serverPlayer.getCapability(ModCapabilities.MONEY_CAP).ifPresent(cap -> cap.addMoney(money));
        }

        serverPlayer.sendSystemMessage(Component.literal("已成功領取信件，獲得 " + money + " 靈幣與物品若干"));

        return true;
    }
}
