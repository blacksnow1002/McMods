package com.blacksnow1002.realmmod.system.mailbox;

import com.blacksnow1002.realmmod.system.mailbox.screen.MailboxScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;

import javax.annotation.Nullable;

public class MailboxMenuProvider implements MenuProvider {

    private final Inventory playerInventory;

    public MailboxMenuProvider(Inventory playerInventory) {
        this.playerInventory = playerInventory;
    }

    @Override
    public Component getDisplayName() {
        return Component.literal("修仙郵箱");
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int containerId, Inventory inventory, Player player) {
        // 這裡返回 Menu，Screen 會自動在客戶端生成
        return new MailboxScreen.MailboxMenu(containerId, playerInventory);
    }
}
