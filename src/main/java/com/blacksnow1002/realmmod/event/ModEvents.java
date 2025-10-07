package com.blacksnow1002.realmmod.event;

import com.blacksnow1002.realmmod.RealmMod;
import com.blacksnow1002.realmmod.capability.ModCapabilities;
import com.blacksnow1002.realmmod.capability.CultivationProvider;
import com.blacksnow1002.realmmod.network.ModMessages;
import com.blacksnow1002.realmmod.network.packets.RealmSyncPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = RealmMod.MOD_ID)
public class ModEvents {

    // 給玩家附加 Capability - 修正泛型參數
    @SubscribeEvent
    public static void onAttachCapabilities(AttachCapabilitiesEvent<Entity> event) {
        if (event.getObject() instanceof Player player) {
            // 確保不會重複添加
            if (!player.getCapability(ModCapabilities.CULTIVATION_CAP).isPresent()) {
                event.addCapability(
                        ResourceLocation.fromNamespaceAndPath(RealmMod.MOD_ID, CultivationProvider.IDENTIFIER),
                        new CultivationProvider()
                );
            }
        }
    }

    // 玩家死亡/重生時複製數據
    @SubscribeEvent
    public static void onPlayerClone(PlayerEvent.Clone event) {
        // 只在死亡重生時處理 (不處理從末地返回的情況)

        System.out.println("========================================");
        System.out.println("=== PlayerEvent.Clone 觸發 (死亡重生) ===");

        Player originalPlayer = event.getOriginal();
        Player newPlayer = event.getEntity();

        // 強制刷新舊玩家的 Capability (重要!)
        originalPlayer.reviveCaps();

        try {
            // 從舊玩家讀取並複製到新玩家
            originalPlayer.getCapability(ModCapabilities.CULTIVATION_CAP).ifPresent(oldData -> {
                System.out.println(">>> 成功取得舊資料");
                System.out.println(">>> 舊資料 - 境界: " + oldData.getRealm().getDisplayName());
                System.out.println(">>> 舊資料 - 層數: " + oldData.getLayer());
                System.out.println(">>> 舊資料 - 修為: " + oldData.getCultivation());

                // 複製到新玩家
                newPlayer.getCapability(ModCapabilities.CULTIVATION_CAP).ifPresent(newData -> {
                    System.out.println(">>> 開始複製資料...");

                    newData.setRealm(oldData.getRealm());
                    newData.setLayer(oldData.getLayer());
                    newData.setCultivation(oldData.getCultivation());
                    newData.setBreakthroughSuccessPossibility(oldData.getBreakthroughSuccessPossibility());

                    System.out.println(">>> 複製完成: " + newData.getRealm().getDisplayName() + " " + newData.getLayer() + "層");
                    System.out.println(">>> 修為值: " + newData.getCultivation());

                    // 同步到客戶端
                    if (newPlayer instanceof ServerPlayer serverPlayer) {
                        ModMessages.sendToPlayer(
                                new RealmSyncPacket(
                                        newData.getRealm().ordinal(),
                                        newData.getLayer()
                                ),
                                serverPlayer
                        );
                        System.out.println(">>> 已發送同步封包");
                    }
                });
            });
        } finally {
            // 清理舊玩家的 Capability
            originalPlayer.invalidateCaps();
        }

        System.out.println("========================================");
    }

    // 額外保險:玩家登入時同步數據
    @SubscribeEvent
    public static void onPlayerLoggedIn(PlayerEvent.PlayerLoggedInEvent event) {
        if (event.getEntity() instanceof ServerPlayer serverPlayer) {
            serverPlayer.getCapability(ModCapabilities.CULTIVATION_CAP).ifPresent(data -> {
                ModMessages.sendToPlayer(
                        new RealmSyncPacket(
                                data.getRealm().ordinal(),
                                data.getLayer()
                        ),
                        serverPlayer
                );
                System.out.println(">>> 玩家登入時同步資料: " + data.getRealm().getDisplayName() + " " + data.getLayer() + "層");
            });
        }
    }

    // 追蹤玩家重生事件(用於調試)
    @SubscribeEvent
    public static void onPlayerRespawn(PlayerEvent.PlayerRespawnEvent event) {
        System.out.println("========================================");
        System.out.println("=== PlayerRespawnEvent 觸發 ===");

        if (event.getEntity() instanceof ServerPlayer serverPlayer) {
            // 重生後立即檢查並同步
            serverPlayer.getCapability(ModCapabilities.CULTIVATION_CAP).ifPresent(data -> {
                System.out.println(">>> 重生後數據檢查: " + data.getRealm().getDisplayName() + " " + data.getLayer() + "層");

                ModMessages.sendToPlayer(
                        new RealmSyncPacket(
                                data.getRealm().ordinal(),
                                data.getLayer()
                        ),
                        serverPlayer
                );
            });
        }

        System.out.println("========================================");
    }

    @SubscribeEvent
    public static void onPlayerChangeDimension(PlayerEvent.PlayerChangedDimensionEvent event) {
        if (event.getEntity() instanceof ServerPlayer serverPlayer) {
            serverPlayer.getCapability(ModCapabilities.CULTIVATION_CAP).ifPresent(data -> {
                ModMessages.sendToPlayer(
                        new RealmSyncPacket(
                                data.getRealm().ordinal(),
                                data.getLayer()
                        ),
                        serverPlayer
                );
                System.out.println(">>> 維度切換時同步資料: "
                        + data.getRealm().getDisplayName() + " " + data.getLayer() + "層");
            });
        }
    }

}