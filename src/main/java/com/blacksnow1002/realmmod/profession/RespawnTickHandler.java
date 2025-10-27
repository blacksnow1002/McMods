package com.blacksnow1002.realmmod.profession;

import com.blacksnow1002.realmmod.RealmMod;
import net.minecraft.server.level.ServerLevel;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.server.ServerStoppingEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

/**
 * 處理採集方塊的重生 Tick
 */
@Mod.EventBusSubscriber(modid = RealmMod.MOD_ID)
public class RespawnTickHandler {

    @SubscribeEvent
    public static void onServerTick(TickEvent.ServerTickEvent event) {
        // 只在 tick 結束階段處理
        if (event.phase != TickEvent.Phase.END) {
            return;
        }

        // 獲取主世界作為計時基準
        ServerLevel overworld = event.getServer().overworld();
        if (overworld != null) {
            HarvestRespawnManager.tick(overworld);
        }
    }

    @SubscribeEvent
    public static void onServerStopping(ServerStoppingEvent event) {
        // 伺服器關閉時清空重生隊列
        HarvestRespawnManager.clear();
    }
}