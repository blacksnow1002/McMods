package com.blacksnow1002.realmmod.mailbox;

import com.blacksnow1002.realmmod.RealmMod; // 你的主類
import net.minecraft.core.registries.Registries;
import net.minecraft.world.inventory.MenuType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;
import net.minecraftforge.common.extensions.IForgeMenuType;

public class ModMenuTypes {
    public static final DeferredRegister<MenuType<?>> MENUS =
            DeferredRegister.create(Registries.MENU, RealmMod.MOD_ID);

    public static final RegistryObject<MenuType<MailboxScreen.MailboxMenu>> MAILBOX_MENU =
            MENUS.register("mailbox_menu", () ->
                    IForgeMenuType.create((containerId, playerInventory, data) ->
                            new MailboxScreen.MailboxMenu(containerId, playerInventory)
                    )
            );
}