package com.blacksnow1002.realmmod.mailbox;

import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.saveddata.SavedData;

import java.util.*;

public class MailboxStorage extends SavedData {

    private static final String DATA_NAME = "mailbox_data";

    private final Map<UUID, List<Mail>> mailboxes = new HashMap<>();

    public MailboxStorage() {
    }

    // 添加郵件
    public void addMail(UUID recipientId, Mail mail) {
        mailboxes.computeIfAbsent(recipientId, k -> new ArrayList<>()).add(mail);
        setDirty();
    }

    // 獲取玩家的所有郵件
    public List<Mail> getMails(UUID playerId) {
        return new ArrayList<>(mailboxes.getOrDefault(playerId, new ArrayList<>()));
    }

    // 刪除郵件
    public void removeMail(UUID playerId, UUID mailId) {
        List<Mail> mails = mailboxes.get(playerId);
        if (mails != null) {
             mails.removeIf(mail -> mail.getMailId().equals(mailId));
             setDirty();
        }
    }


    public static MailboxStorage get(Level level) {
        if (level instanceof ServerLevel serverLevel) {
            return serverLevel.getDataStorage().computeIfAbsent(
                    new SavedData.Factory<>(
                            MailboxStorage::new,
                            MailboxStorage::load,
                            null
                    ),
                    DATA_NAME
            );
        }
        return null;
    }

    @Override
    public CompoundTag save(CompoundTag tag, HolderLookup.Provider registries) {
        CompoundTag mailboxesTag = new CompoundTag();

        for (Map.Entry<UUID, List<Mail>> entry : mailboxes.entrySet()) {
            ListTag mailList = new ListTag();
            for (Mail mail : entry.getValue()) {
                mailList.add(mail.save(registries));
            }
            mailboxesTag.put(entry.getKey().toString(), mailList);
        }

        tag.put("mailboxes", mailboxesTag);
        return tag;
    }

    public static MailboxStorage load(CompoundTag tag, HolderLookup.Provider registries) {
        MailboxStorage storage = new MailboxStorage();
        CompoundTag mailboxesTag = tag.getCompound("mailboxes");

        for (String key : mailboxesTag.getAllKeys()) {
            UUID playerId = UUID.fromString(key);
            ListTag mailList = mailboxesTag.getList(key, 10);
            List<Mail> mails = new ArrayList<>();

            for (int i = 0; i < mailList.size(); i++) {
                mails.add(Mail.load(mailList.getCompound(i), registries));
            }

            storage.mailboxes.put(playerId, mails);
        }

        return storage;
    }
}
