package com.blacksnow1002.realmmod.common.registry;

import com.blacksnow1002.realmmod.RealmMod;
import com.blacksnow1002.realmmod.system.assignment.npc.CustomNPCEntity;
import com.blacksnow1002.realmmod.system.spell.entity.PlayerCloneEntity;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

/**
 * 實體類型註冊類
 * 負責註冊所有自定義實體
 */
public class ModEntities {

    // 創建實體類型的延遲註冊器
    public static final DeferredRegister<EntityType<?>> ENTITY_TYPES =
            DeferredRegister.create(Registries.ENTITY_TYPE, RealmMod.MOD_ID);

    // 註冊玩家分身實體
    public static final RegistryObject<EntityType<PlayerCloneEntity>> PLAYER_CLONE =
            ENTITY_TYPES.register("player_clone", () ->
                    EntityType.Builder.of(PlayerCloneEntity::new, MobCategory.MISC)
                            .sized(0.6F, 1.8F)           // 實體碰撞箱大小（玩家大小）
                            .clientTrackingRange(10)      // 客戶端追蹤範圍（區塊）
                            .updateInterval(3)            // 更新間隔（tick）
                            .build(RealmMod.MOD_ID + ":player_clone")        // 構建實體類型
            );

    // 如果你有其他實體，可以在這裡繼續註冊
    // public static final RegistryObject<EntityType<OtherEntity>> OTHER_ENTITY = ...
    public static final RegistryObject<EntityType<CustomNPCEntity>> CUSTOM_NPC = ENTITY_TYPES.register("custom_npc",
            () -> EntityType.Builder.of(CustomNPCEntity::new, MobCategory.CREATURE)
                    .sized(0.6f, 1.95f)
                    .build(RealmMod.MOD_ID + ":custom_npc")
    );

    /**
     * 內部事件監聽器類
     * 用於註冊實體屬性
     */
    @Mod.EventBusSubscriber(modid = RealmMod.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
    public static class EntityAttributeRegistry {

        /**
         * 註冊實體屬性
         * 這個方法會在 Mod 加載時自動調用
         */
        @SubscribeEvent
        public static void registerAttributes(EntityAttributeCreationEvent event) {
            // 為玩家分身實體註冊屬性
            event.put(PLAYER_CLONE.get(), PlayerCloneEntity.createAttributes().build());

            // 如果有其他實體，在這裡註冊它們的屬性
            event.put(CUSTOM_NPC.get(), CustomNPCEntity.createAttributes().build());
            // event.put(OTHER_ENTITY.get(), OtherEntity.createAttributes().build());
        }
    }
}