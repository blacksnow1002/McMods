package com.blacksnow1002.realmmod.item;

import com.blacksnow1002.realmmod.RealmMod;
import com.blacksnow1002.realmmod.item.custom.*;
import com.blacksnow1002.realmmod.item.custom.pillItem.HealingPillItem;
import com.blacksnow1002.realmmod.profession.alchemy.recipe.types.HealingPillRecipe;
import com.blacksnow1002.realmmod.technique.TechniqueRegistry;
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

    public static final RegistryObject<Item> TECHNIQUE = ITEMS.register("technique",
            () -> new TechniqueItem(TechniqueRegistry.TechniqueIds.FIRE_SUPREME, new Item.Properties()));

    public static final RegistryObject<Item> HEALING_PILL_9 = ITEMS.register("healing_pill_9",
            () -> new HealingPillItem(new Item.Properties().food(ModFoodProperties.ELIXIR)));
    public static final RegistryObject<Item> ALCHEMY_RECIPE_HEALING_9 = ITEMS.register("alchemy_recipe_healing_9",
            () -> new AlchemyRecipeItem(new Item.Properties(), HealingPillRecipe::new));

    // 採集工具
    public static final RegistryObject<Item> HARVEST_TOOL_9_MORTAL = ITEMS.register("harvest_tool_9_mortal",
            () -> new HarvestToolItem(new Item.Properties().stacksTo(1), HarvestToolItem.Grades.MORTAL, 9));
    public static final RegistryObject<Item> HARVEST_TOOL_9_MYSTIC = ITEMS.register("harvest_tool_9_mystic",
            () -> new HarvestToolItem(new Item.Properties().stacksTo(1), HarvestToolItem.Grades.MYSTIC, 9));
    public static final RegistryObject<Item> HARVEST_TOOL_9_EARTH = ITEMS.register("harvest_tool_9_earth",
            () -> new HarvestToolItem(new Item.Properties().stacksTo(1), HarvestToolItem.Grades.EARTH, 9));
    public static final RegistryObject<Item> HARVEST_TOOL_9_HEAVEN = ITEMS.register("harvest_tool_9_heaven",
            () -> new HarvestToolItem(new Item.Properties().stacksTo(1), HarvestToolItem.Grades.HEAVEN, 9));

    // 重鑄石
    public static final RegistryObject<Item> TOOL_REFORGE_STONE = ITEMS.register("tool_reforge_stone",
            () -> new ToolReforgeItem(new Item.Properties().stacksTo(16))
    );


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
