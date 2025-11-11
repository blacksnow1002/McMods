package com.blacksnow1002.realmmod.common.registry;

import com.blacksnow1002.realmmod.RealmMod; // 你的主類
import com.blacksnow1002.realmmod.client.mailbox.screen.MailboxScreen;
import com.blacksnow1002.realmmod.client.profession.alchemy.screen.AlchemyFurnaceMenu;
import com.blacksnow1002.realmmod.client.profession.reforge.screen.ReforgeFurnaceMenu;
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

    public static final RegistryObject<MenuType<AlchemyFurnaceMenu>> ALCHEMY_FURNACE_MENU =
            MENUS.register("alchemy_furnace_menu", () ->
                    IForgeMenuType.create((containerId, playerInventory, data ) ->
                            new AlchemyFurnaceMenu(containerId, playerInventory, data)));

    public static final RegistryObject<MenuType<ReforgeFurnaceMenu>> REFORGE_FURNACE_MENU =
            MENUS.register("reforge_furnace_menu", () ->
                    IForgeMenuType.create((containerId, playerInventory, data ) ->
                            new ReforgeFurnaceMenu(containerId, playerInventory, data)));
}