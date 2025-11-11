package com.blacksnow1002.realmmod.client.mailbox.screen;

import com.blacksnow1002.realmmod.common.network.ModMessages;
import com.blacksnow1002.realmmod.system.mailbox.network.C2S.ClaimMailPacket;
import com.blacksnow1002.realmmod.common.registry.ModMenuTypes;
import com.blacksnow1002.realmmod.system.mailbox.Mail;
import com.blacksnow1002.realmmod.client.mailbox.cache.ClientMailCache;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

public class MailboxScreen extends AbstractContainerScreen<MailboxScreen.MailboxMenu> {
    private static final ResourceLocation CONTAINER_BACKGROUND =
            ResourceLocation.withDefaultNamespace("textures/gui/container/generic_54.png");

    private final List<Mail> mails;
    private final UUID playerId;

    public MailboxScreen(MailboxMenu menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title);

        System.out.println("MailboxScreen opened");

        this.playerId = Minecraft.getInstance().player.getUUID();
        this.mails = ClientMailCache.getMails(playerId);
        this.imageHeight = 114 + 6 * 18; // 6行箱子
        this.inventoryLabelY = this.imageHeight - 94;
    }

    @Override
    protected void renderBg(GuiGraphics graphics, float partialTick, int mouseX, int mouseY) {
        int x = (this.width - this.imageWidth) / 2;
        int y = (this.height - this.imageHeight) / 2;
        graphics.blit(CONTAINER_BACKGROUND, x, y, 0, 0, this.imageWidth, 6 * 18 + 17);
        graphics.blit(CONTAINER_BACKGROUND, x, y + 6 * 18 + 17, 0, 126, this.imageWidth, 96);
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        super.render(graphics, mouseX, mouseY, partialTick);
        this.renderTooltip(graphics, mouseX, mouseY);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        System.out.println(3);
        // 檢查是否點擊到格子
        Slot hoveredSlot = this.getSlotUnderMouse();
        System.out.println("懸停的格子: " + (hoveredSlot != null ? hoveredSlot.index : "null"));

        if (hoveredSlot != null && hoveredSlot.container == this.menu.mailContainer) {
            System.out.println("點擊到郵件格子！");

            // 只允許左鍵點擊
            if (button == 0) {
                int slotIndex = hoveredSlot.getSlotIndex();
                System.out.println("格子索引: " + slotIndex + ", 郵件數量: " + mails.size());

                if (slotIndex < mails.size()) {
                    Mail mail = mails.get(slotIndex);
                    System.out.println("發送領取封包，郵件ID: " + mail.getMailId());

                    // 發送封包到伺服器
                    ModMessages.sendToServer(new ClaimMailPacket(mail.getMailId()));
                    ClientMailCache.removeMail(playerId, mail.getMailId());
                }
            }
            // 阻止預設行為
            return true;
        }

        // 其他格子使用預設行為
        return super.mouseClicked(mouseX, mouseY, button);
    }

    // 📌 新增：從伺服器接收更新後刷新畫面
    public void refreshMailDisplay() {
        System.out.println(4);
        this.mails.clear();
        this.mails.addAll(ClientMailCache.getMails(playerId));
        this.menu.updateMailSlots();
    }

    // 內部容器菜單類
    public static class MailboxMenu extends AbstractContainerMenu {
        private final MailInventory mailContainer;
        private final List<Mail> mails;

        public MailboxMenu(int containerId, Inventory playerInventory) {
            super(ModMenuTypes.MAILBOX_MENU.get(), containerId);

            UUID playerId = playerInventory.player.getUUID();
            this.mails = ClientMailCache.getMails(playerId);
            this.mailContainer = new MailInventory(mails);

            // 添加郵件格子 (6行9列 = 54格)
            for (int row = 0; row < 6; row++) {
                for (int col = 0; col < 9; col++) {
                    this.addSlot(new MailSlot(mailContainer, col + row * 9, 8 + col * 18, 18 + row * 18));
                }
            }

            // 添加玩家背包
            for (int row = 0; row < 3; row++) {
                for (int col = 0; col < 9; col++) {
                    this.addSlot(new Slot(playerInventory, col + row * 9 + 9,
                            8 + col * 18, 103 + row * 18 + 6 * 18 - 18));
                }
            }

            // 添加玩家快捷欄
            for (int col = 0; col < 9; col++) {
                this.addSlot(new Slot(playerInventory, col, 8 + col * 18, 161 + 6 * 18 - 18));
            }
        }

        @Override
        public ItemStack quickMoveStack(net.minecraft.world.entity.player.Player player, int index) {
            System.out.println(5);
            // 禁止shift點擊郵件格子
            return ItemStack.EMPTY;
        }

        @Override
        public boolean stillValid(net.minecraft.world.entity.player.Player player) {
            return true;
        }

        public void updateMailSlots() {
            System.out.println(7);
            mailContainer.updateItems();
            this.broadcastChanges();
        }

        // 自定義郵件格子，防止物品被拿出
        private static class MailSlot extends Slot {
            public MailSlot(net.minecraft.world.Container container, int slot, int x, int y) {
                super(container, slot, x, y);
            }

            @Override
            public boolean mayPickup(net.minecraft.world.entity.player.Player player) {

                System.out.println(8);
                return false; // 📌 改為 false，完全禁止拿起
            }

            @Override
            public boolean mayPlace(ItemStack stack) {
                System.out.println(9);
                return false; // 不允許放入物品
            }
        }
    }

    // 郵件容器類
    private static class MailInventory implements net.minecraft.world.Container {
        private final List<Mail> mails;
        private final List<ItemStack> items;

        public MailInventory(List<Mail> mails) {
            this.mails = mails;
            this.items = new ArrayList<>();
            updateItems();
        }

        public void updateItems() {
            System.out.println(10);
            items.clear();
            for (Mail mail : mails) {
                // 使用紙張代表郵件
                ItemStack mailItem = new ItemStack(Items.PAPER);

                // 設置標題
                mailItem.set(net.minecraft.core.component.DataComponents.CUSTOM_NAME,
                        Component.literal("§6來自 " + mail.getSenderName() + " 的郵件"));

                // 添加描述 (Lore)
                List<Component> lore = new ArrayList<>();
                lore.add(Component.literal("")); // 空行
                lore.add(Component.literal("§e發送者: §f" + mail.getSenderName()));
                lore.add(Component.literal("§6訊息: §f" + mail.getMessage()));
                lore.add(Component.literal("")); // 空行

                // 顯示金錢
                if (mail.getMoney() > 0) {
                    lore.add(Component.literal("§a💰 金錢: §f" + mail.getMoney()));
                }

                // 顯示物品
                if (!mail.getItems().isEmpty()) {
                    lore.add(Component.literal("§b📦 附件物品:"));
                    for (ItemStack item : mail.getItems()) {
                        if (!item.isEmpty()) {
                            String itemName = item.has(net.minecraft.core.component.DataComponents.CUSTOM_NAME) ?
                                    item.get(net.minecraft.core.component.DataComponents.CUSTOM_NAME).getString() :
                                    item.getItem().getDescription().getString();
                            lore.add(Component.literal("  §7- §f" + itemName + " §7x" + item.getCount()));
                        }
                    }
                }

                lore.add(Component.literal("")); // 空行

                // 添加時間戳
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm");
                String time = sdf.format(new Date(mail.getTimestamp()));
                lore.add(Component.literal("§7📅 " + time));

                lore.add(Component.literal("")); // 空行
                lore.add(Component.literal("§e§l▶ 點擊領取"));

                // 設置 Lore
                mailItem.set(net.minecraft.core.component.DataComponents.LORE,
                        new net.minecraft.world.item.component.ItemLore(lore));

                items.add(mailItem);
            }

            // 填充空格子
            while (items.size() < 54) {
                items.add(ItemStack.EMPTY);
            }
        }

        @Override
        public int getContainerSize() {
            return 54;
        }

        @Override
        public boolean isEmpty() {
            return mails.isEmpty();
        }

        @Override
        public ItemStack getItem(int slot) {
            return slot < items.size() ? items.get(slot) : ItemStack.EMPTY;
        }

        @Override
        public ItemStack removeItem(int slot, int amount) {
            return ItemStack.EMPTY; // 禁止移除
        }

        @Override
        public ItemStack removeItemNoUpdate(int slot) {
            return ItemStack.EMPTY; // 禁止移除
        }

        @Override
        public void setItem(int slot, ItemStack stack) {
            // 不允許設置物品
        }

        @Override
        public void setChanged() {
        }

        @Override
        public boolean stillValid(net.minecraft.world.entity.player.Player player) {
            return true;
        }

        @Override
        public void clearContent() {
        }
    }
}