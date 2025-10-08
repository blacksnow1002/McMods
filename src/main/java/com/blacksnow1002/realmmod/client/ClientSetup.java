package com.blacksnow1002.realmmod.client;

import com.blacksnow1002.realmmod.ModEntities;
import com.blacksnow1002.realmmod.RealmMod;
import com.blacksnow1002.realmmod.client.renderer.PlayerCloneRenderer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

/**
 * 客戶端設置類
 * 僅在客戶端運行（不會在伺服器上運行）
 */
@Mod.EventBusSubscriber(
        modid = RealmMod.MOD_ID,                    // Mod ID
        bus = Mod.EventBusSubscriber.Bus.MOD,      // 使用 MOD 事件總線
        value = Dist.CLIENT                         // 僅客戶端
)
public class ClientSetup {

    /**
     * 註冊實體渲染器
     * 這個方法會在客戶端啟動時自動調用
     */
    @SubscribeEvent
    public static void registerRenderers(EntityRenderersEvent.RegisterRenderers event) {
        // 為玩家分身實體註冊渲染器
        event.registerEntityRenderer(
                ModEntities.PLAYER_CLONE.get(),     // 實體類型
                PlayerCloneRenderer::new             // 渲染器工廠
        );

        System.out.println("實體渲染器註冊完成");

        // 如果有其他實體需要渲染器，在這裡註冊
        // event.registerEntityRenderer(ModEntities.OTHER_ENTITY.get(), OtherRenderer::new);
    }

    /**
     * 註冊實體模型層（如果需要自定義模型）
     * 對於使用標準 PlayerModel 的實體，這個方法不是必須的
     */
    @SubscribeEvent
    public static void registerLayerDefinitions(EntityRenderersEvent.RegisterLayerDefinitions event) {
        // 如果你有自定義模型層，在這裡註冊
        // event.registerLayerDefinition(ModModelLayers.CUSTOM_LAYER, CustomModel::createBodyLayer);

        System.out.println("模型層註冊完成");
    }
}