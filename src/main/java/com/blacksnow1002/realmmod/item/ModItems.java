package com.blacksnow1002.realmmod.item;

import com.blacksnow1002.realmmod.RealmMod;
import com.blacksnow1002.realmmod.item.custom.*;
import net.minecraft.world.item.Item;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModItems {
    public static final DeferredRegister<Item> ITEMS =
            DeferredRegister.create(ForgeRegistries.ITEMS, RealmMod.MOD_ID);

    public static final RegistryObject<Item> SPIRIT_STONE_LOW = ITEMS.register("spirit_stone_low",
            () -> new SpiritStoneItem(new Item.Properties(), 10));
    public static final RegistryObject<Item> SPIRIT_STONE_MIDDLE = ITEMS.register("spirit_stone_middle",
            () -> new SpiritStoneItem(new Item.Properties(), 100));
    public static final RegistryObject<Item> SPIRIT_STONE_HIGH = ITEMS.register("spirit_stone_high",
            () -> new SpiritStoneItem(new Item.Properties(), 1000));

    public static final RegistryObject<Item> FOUNDATION_BUILD_ELIXIR = ITEMS.register("foundation_build_elixir",
            () -> new ElixirItem(new Item.Properties().food(ModFoodProperties.ELIXIR)));
    public static final RegistryObject<Item> SPIRIT_FRUIT = ITEMS.register("spirit_fruit",
            () ->  new SpiritFruitItem(new Item.Properties().food(ModFoodProperties.SPIRIT_FRUIT), 100));




    public static final RegistryObject<Item> MAGIC_DRUG = ITEMS.register("magic_drug",
            () -> new Item(new Item.Properties()));

    public static final RegistryObject<Item> ALEXANDRITE = ITEMS.register("alexandrite",
            () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> RAW_ALEXANDRITE = ITEMS.register("raw_alexandrite",
            () -> new Item(new Item.Properties()));

    public static final RegistryObject<Item> CHISEL = ITEMS.register("chisel",
            () -> new ChiselItem(new Item.Properties().durability(32)));

    public static final RegistryObject<Item> KOHLRABI = ITEMS.register("kohlrabi",
            () -> new Item(new Item.Properties().food(ModFoodProperties.KOHLRABI)));

    public static final RegistryObject<Item> AURORA_ASHES = ITEMS.register("aurora_ashes",
            () -> new FuelItem(new Item.Properties(), 1200));



    public  static void register(IEventBus eventBus) {
        ITEMS.register(eventBus);
    }
}
