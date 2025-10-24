package com.blacksnow1002.realmmod.mailbox;

import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.world.item.ItemStack;

import java.util.UUID;

//Mail 包含；收件人id 送件人名稱 金錢 物品 留言 時間戳記
public class Mail {
    private final UUID mailId;
    private final UUID targetId;
    private final String senderName;
    private final int money;
    private final NonNullList<ItemStack> items;
    private long timestamp;
    private final String message;

    public Mail(UUID mailId, UUID targetId, String senderName, int money, NonNullList<ItemStack> items, String message) {
        this.mailId = mailId != null ? mailId : UUID.randomUUID();
        this.targetId = targetId;
        this.senderName = senderName;
        this.money = money;
        this.items = items;
        this.timestamp = System.currentTimeMillis();
        this.message = message;
    }

    public UUID getMailId() { return mailId; }
    public UUID getTargetId() { return targetId; }
    public String getSenderName() { return senderName; }
    public int getMoney() { return money; }
    public NonNullList<ItemStack> getItems() { return items; }
    public long getTimestamp() { return timestamp; }
    public String getMessage() { return message; }

    //ByteBuf 使用
    public void toBytes(RegistryFriendlyByteBuf buf) {
        buf.writeUUID(mailId);
        buf.writeUUID(targetId);
        buf.writeUtf(senderName);
        buf.writeInt(money);
        buf.writeLong(timestamp);
        buf.writeUtf(message);

        buf.writeInt(items.size());
        for (ItemStack stack : items) {
            ItemStack.STREAM_CODEC.encode(buf, stack);
        }
    }

    public static Mail fromBytes(RegistryFriendlyByteBuf buf) {
        UUID mailId = buf.readUUID();
        UUID targetId = buf.readUUID();
        String senderName = buf.readUtf();
        int money = buf.readInt();
        long timestamp = buf.readLong();
        String message = buf.readUtf();

        int itemCount = buf.readInt();
        NonNullList<ItemStack> items = NonNullList.create();
        for (int i = 0; i < itemCount; i++) {
            items.add(ItemStack.STREAM_CODEC.decode(buf));
        }
        Mail mail = new Mail(mailId, targetId, senderName, money, items, message);
        mail.timestamp = timestamp;
        return mail;
    }

    // NBT序列化
    public CompoundTag save(HolderLookup.Provider registries) {
        CompoundTag tag = new CompoundTag();
        tag.putUUID("mailId", mailId);
        tag.putUUID("targetId", targetId);
        tag.putString("senderName", senderName);
        tag.putInt("money", money);
        tag.putLong("timestamp", timestamp);
        tag.putString("message", message);

        ListTag itemsList = new ListTag();
        for (ItemStack stack : items) {
            if (!stack.isEmpty()) {
                itemsList.add(stack.save(registries));
            }
        }
        tag.put("items", itemsList);

        return tag;
    }

    public static Mail load(CompoundTag tag, HolderLookup.Provider registries) {
        UUID mailId = tag.getUUID("mailId");
        UUID targetId = tag.getUUID("targetId");
        String senderName = tag.getString("senderName");
        int money = tag.getInt("money");
        String message = tag.getString("message");

        NonNullList<ItemStack> items = NonNullList.create();
        ListTag itemsList = tag.getList("items", 10); // 10 = CompoundTag
        for (int i = 0; i < itemsList.size(); i++) {
            ItemStack stack = ItemStack.parseOptional(registries, itemsList.getCompound(i));
            if (!stack.isEmpty()) {
                items.add(stack);
            }
        }

        Mail mail = new Mail(mailId, targetId, senderName, money, items, message);
        mail.timestamp = tag.getLong("timestamp");
        return mail;
    }
}