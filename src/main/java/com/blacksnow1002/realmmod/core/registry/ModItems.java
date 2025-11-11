package com.blacksnow1002.realmmod.core.registry;

import com.blacksnow1002.realmmod.RealmMod;
import com.blacksnow1002.realmmod.item.ModFoodProperties;
import com.blacksnow1002.realmmod.system.cultivation.item.SpiritFruitItem;
import com.blacksnow1002.realmmod.system.cultivation.item.SpiritStoneItem;
import com.blacksnow1002.realmmod.system.profession.alchemy.item.pill.HealingPillItem;
import com.blacksnow1002.realmmod.system.profession.alchemy.item.AlchemyRecipeItem;
import com.blacksnow1002.realmmod.system.profession.alchemy.item.ElixirItem;
import com.blacksnow1002.realmmod.system.profession.alchemy.recipe.types.HealingPillRecipe;
import com.blacksnow1002.realmmod.system.profession.harvest.item.HarvestToolItem;
import com.blacksnow1002.realmmod.system.profession.reforge.item.ToolReforgeItem;
import com.blacksnow1002.realmmod.system.technique.TechniqueRegistry;
import com.blacksnow1002.realmmod.system.technique.item.TechniqueItem;
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

    public static final RegistryObject<Item> ALCHEMY_RECIPE_HEALING_9 = ITEMS.register("alchemy_recipe_healing_9",
            () -> new AlchemyRecipeItem(new Item.Properties(), HealingPillRecipe::new));
    public static final RegistryObject<Item> HEALING_PILL_9 = ITEMS.register("healing_pill_9",
            () -> new HealingPillItem(new Item.Properties().food(ModFoodProperties.ELIXIR)));
    public static final RegistryObject<Item> ALCHEMY_WASTE_ITEM = ITEMS.register("alchemy_waste_item",
            () -> new Item(new Item.Properties().food(ModFoodProperties.ELIXIR)));

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




    public  static void register(IEventBus eventBus) {
        ITEMS.register(eventBus);
    }
}
