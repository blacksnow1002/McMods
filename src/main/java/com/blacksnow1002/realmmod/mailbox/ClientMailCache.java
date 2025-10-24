package com.blacksnow1002.realmmod.mailbox;

import java.util.*;

public class ClientMailCache {
    private static final Map<UUID, List<Mail>> mailCache = new HashMap<>();

    public static void setMails(UUID uuid, List<Mail> mails) {
        mailCache.put(uuid, new ArrayList<>(mails));
        System.out.println("[ClientMailCache] 已快取玩家 " + uuid + " 的 " + mails.size() + " 封郵件");
    }

    // 獲取玩家所有郵件
    public static List<Mail> getMails(UUID uuid) {
        return mailCache.getOrDefault(uuid, new ArrayList<>());
    }

    // 新增郵件，如果郵件太多則刪除
    public static void addMail(UUID uuid, Mail mail) {
        List<Mail> mails = mailCache.getOrDefault(uuid, new ArrayList<>());
        if (mails.size() >= 54) {
            mails.sort(Comparator.comparing(Mail::getTimestamp));
            mails.removeFirst();
        }
        mails.add(mail);
    }

    // 刪除郵件
    public static void removeMail(UUID playerId, UUID mailId) {
        List<Mail> mails = mailCache.get(playerId);
        if (mails != null) {
            mails.removeIf(mail -> mail.getMailId().equals(mailId));
        }
    }

    //清除快取(玩家離線時使用)
    public static void clearMails(UUID uuid) {
        mailCache.remove(uuid);
    }


    public static int getMailCount(UUID uuid) {
        return mailCache.getOrDefault(uuid, new ArrayList<>()).size();
    }
}
