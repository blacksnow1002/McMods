package com.blacksnow1002.realmmod.system.cultivation;

import com.blacksnow1002.realmmod.RealmMod;
import com.blacksnow1002.realmmod.system.battle.attribute.capability.source.realm.RealmAttributeData;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraftforge.event.AddReloadListenerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

@Mod.EventBusSubscriber(modid = RealmMod.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class RealmAttributeLoader {

    private static final Gson GSON = new Gson();
    private static final Map<CultivationRealm, RealmConfig> CONFIG_MAP = new HashMap<>();
    private static final ResourceLocation CONFIG_LOCATION =
            ResourceLocation.fromNamespaceAndPath(RealmMod.MOD_ID, "config/realm_init_player_attribute.json");

    /**
     * 境界完整配置類
     * 包含所有境界相關的數值
     */
    public static class RealmConfig {
        // 境界基礎資訊
        public String displayName = "";
        public int maxLayer = 1;
        public int requiredPerLayer = 1;
        public float breakthroughSuccessPossibility = 1.0f;
        public int realmAge = 40;

        // 玩家屬性
        public int attack = 5;
        public int defense = 2;
        public int maxHealth = 20;
        public float speed = 0.1f;
        public float dodgeRate = 0.0f;
        public float critRate = 0.05f;
        public float critMagnification = 1.5f;
        public int currentMana = 0;
        public int maxMana = 0;
    }

    /**
     * 註冊資源重載監聽器
     */
    @SubscribeEvent
    public static void onResourceReload(AddReloadListenerEvent event) {
        event.addListener((preparationBarrier, resourceManager, preparationsProfiler,
                           reloadProfiler, backgroundExecutor, gameExecutor) -> {
            return preparationBarrier.wait(null).thenRunAsync(() -> {
                loadConfigs(resourceManager);
            }, gameExecutor);
        });
    }

    /**
     * 從資源管理器加載配置
     */
    private static void loadConfigs(ResourceManager resourceManager) {
        try {
            Resource resource = resourceManager.getResource(CONFIG_LOCATION).orElse(null);
            if (resource == null) {
                RealmMod.LOGGER.warn("Realm config not found at " + CONFIG_LOCATION + ", using defaults");
                return;
            }

            try (InputStreamReader reader = new InputStreamReader(resource.open())) {
                JsonObject root = GSON.fromJson(reader, JsonObject.class);

                for (CultivationRealm realm : CultivationRealm.values()) {
                    String realmName = realm.name();
                    if (root.has(realmName)) {
                        RealmConfig config = GSON.fromJson(
                                root.get(realmName), RealmConfig.class);
                        CONFIG_MAP.put(realm, config);
                    } else {
                        RealmMod.LOGGER.warn("Missing config for realm: " + realmName);
                    }
                }

                // 清除 enum 中的緩存
                clearAllCaches();

                RealmMod.LOGGER.info("Successfully loaded realm configs for " + CONFIG_MAP.size() + " realms");
            }
        } catch (Exception e) {
            RealmMod.LOGGER.error("Failed to load realm configs, using defaults", e);
        }
    }

    /**
     * 清除所有境界的緩存
     */
    private static void clearAllCaches() {
        for (CultivationRealm realm : CultivationRealm.values()) {
            realm.clearCache();
        }
    }

    /**
     * 獲取指定境界的完整配置
     */
    public static RealmConfig getConfig(CultivationRealm realm) {
        return CONFIG_MAP.getOrDefault(realm, CONFIG_MAP.get(CultivationRealm.first));
    }

    /**
     * 應用境界屬性到玩家數據
     */
    public static void applyRealmAttributes(CultivationRealm realm,
                                            RealmAttributeData data) {
        RealmConfig config = getConfig(realm);

        // 應用玩家屬性
        data.setRealmAttack(config.attack);
        data.setRealmDefense(config.defense);
        data.setRealmMaxHealth(config.maxHealth);
        data.setRealmMoveSpeed(config.speed);
        data.setRealmDodgeRate(config.dodgeRate);
        data.setRealmCritRate(config.critRate);
        data.setRealmCritMagnification(config.critMagnification);

        // 設置法力值
        data.setRealmMaxMana(config.maxMana);
    }
}