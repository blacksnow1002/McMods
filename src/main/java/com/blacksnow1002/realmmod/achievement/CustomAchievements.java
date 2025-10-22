package com.blacksnow1002.realmmod.achievement;

import com.blacksnow1002.realmmod.RealmMod;
import net.minecraft.advancements.*;
import net.minecraft.advancements.critereon.ImpossibleTrigger;
import net.minecraft.advancements.critereon.InventoryChangeTrigger;
import net.minecraft.advancements.critereon.ItemPredicate;
import net.minecraft.advancements.critereon.PlayerTrigger;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;

import java.util.Map;
import java.util.function.Consumer;

public enum CustomAchievements {
    // 探索分頁
    EXPLORATION_ROOT("exploration/root", Items.COMPASS, null, AdvancementType.TASK, false, false),
    FIND_VILLAGE("exploration/find_village", Items.BELL, EXPLORATION_ROOT, AdvancementType.TASK, true, true),
    FIND_DESERT("exploration/find_desert", Items.SAND, EXPLORATION_ROOT, AdvancementType.TASK, true, true),
    VISIT_10_BIOMES("exploration/visit_10_biomes", Items.MAP, FIND_DESERT, AdvancementType.GOAL, true, true),
    WORLD_EXPLORER("exploration/world_explorer", Items.FILLED_MAP, VISIT_10_BIOMES, AdvancementType.CHALLENGE, true, true),

    // 戰鬥分頁
    COMBAT_ROOT("combat/root", Items.IRON_SWORD, null, AdvancementType.TASK, false, false),
    KILL_ZOMBIE("combat/kill_zombie", Items.ROTTEN_FLESH, COMBAT_ROOT, AdvancementType.TASK, true, true),
    KILL_SKELETON("combat/kill_skeleton", Items.BONE, COMBAT_ROOT, AdvancementType.TASK, true, true),
    KILL_10_MOBS("combat/kill_10_mobs", Items.DIAMOND_SWORD, KILL_ZOMBIE, AdvancementType.GOAL, true, true),
    MONSTER_HUNTER("combat/monster_hunter", Items.NETHERITE_SWORD, KILL_10_MOBS, AdvancementType.CHALLENGE, true, true),

    // 建築分頁
    BUILDING_ROOT("building/root", Items.OAK_PLANKS, null, AdvancementType.TASK, false, false),
    BUILD_HOUSE("building/build_house", Items.BRICKS, BUILDING_ROOT, AdvancementType.TASK, true, true),
    BUILD_CASTLE("building/build_castle", Items.STONE_BRICKS, BUILD_HOUSE, AdvancementType.GOAL, true, true),
    MASTER_BUILDER("building/master_builder", Items.NETHER_BRICKS, BUILD_CASTLE, AdvancementType.CHALLENGE, true, true);

    private final String path;
    private final Item icon;
    private final CustomAchievements parent;
    private final AdvancementType type;
    private final boolean showToast;
    private final boolean announceToChat;

    CustomAchievements(String path, Item icon, CustomAchievements parent,
                       AdvancementType type, boolean showToast, boolean announceToChat) {
        this.path = path;
        this.icon = icon;
        this.parent = parent;
        this.type = type;
        this.showToast = showToast;
        this.announceToChat = announceToChat;
    }

    public String getPath() {
        return path;
    }

    public ResourceLocation getId() {
        return ResourceLocation.fromNamespaceAndPath(RealmMod.MOD_ID, path);
    }

    public CustomAchievements getParent() {
        return parent;
    }

    public boolean isRoot() {
        return parent == null;
    }

    // 生成成就 (用於 DataGen)
    public AdvancementHolder generate(Consumer<AdvancementHolder> saver,
                                      Map<CustomAchievements, AdvancementHolder> holders) {
        Advancement.Builder builder = Advancement.Builder.advancement();

        // 設置顯示信息
        builder.display(
                icon,
                Component.translatable("advancement." + RealmMod.MOD_ID + "." + path.replace("/", ".") + ".title"),
                Component.translatable("advancement." + RealmMod.MOD_ID + "." + path.replace("/", ".") + ".description"),
                isRoot() ? ResourceLocation.withDefaultNamespace("textures/block/stone.png") : null,
                type,
                showToast,
                announceToChat,
                false
        );

        // 設置父成就
        if (parent != null && holders.containsKey(parent)) {
            builder.parent(holders.get(parent));
        }

        if (isRoot()) {
            builder.addCriterion("auto_grant", PlayerTrigger.TriggerInstance.tick());
        } else {
            // 非根成就使用手動觸發
            builder.addCriterion("manual_only",
                    InventoryChangeTrigger.TriggerInstance.hasItems(
                            ItemPredicate.Builder.item().of(Items.BARRIER).build()
                    )
            );
        }

        // 修正: 使用 Criterion.simple() 包裝 TriggerInstance
        // 使用一個假的條件（用一個不存在的物品）
        // 這樣成就就不會自動觸發，只能通過指令手動授予
        builder.addCriterion("manual_trigger",
                InventoryChangeTrigger.TriggerInstance.hasItems(
                        ItemPredicate.Builder.item().of(Items.BARRIER).build()  // 使用屏障方塊作為條件
                )
        );

        AdvancementHolder holder = builder.save(saver, getId());
        holders.put(this, holder);
        return holder;
    }

    // 根據 ID 查找成就
    public static CustomAchievements getById(String id) {
        for (CustomAchievements achievement : values()) {
            if (achievement.path.equals(id)) {
                return achievement;
            }
        }
        return null;
    }
}