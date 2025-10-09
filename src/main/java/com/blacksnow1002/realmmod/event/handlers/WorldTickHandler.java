package com.blacksnow1002.realmmod.event.handlers;

import com.blacksnow1002.realmmod.RealmMod;
import com.blacksnow1002.realmmod.capability.ModCapabilities;
import com.blacksnow1002.realmmod.capability.age.AgeProvider;
import com.blacksnow1002.realmmod.data.OfflinePlayerCapabilityManager;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.storage.LevelResource;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.io.File;
import java.util.UUID;

@Mod.EventBusSubscriber(modid = RealmMod.MOD_ID)
public class WorldTickHandler {

    // 一道年 = 80 分鐘 = 80 * 60 * 20 tick = 96000 tick
    private static final int TICKS_PER_YEAR = 96000;
    private static final int TICKS_PER_SEASON = TICKS_PER_YEAR / 4; // 每季 20 分鐘 = 24000 tick
    private static long lastProcessedTime = -1;

    @SubscribeEvent
    public static void onWorldTick(TickEvent.LevelTickEvent event) {
        if (event.phase != TickEvent.Phase.END) return; // 僅在結尾階段執行
        if (event.level.isClientSide()) return; // 只在伺服器運行

        ServerLevel level = (ServerLevel) event.level;
        if (level.dimension() != ServerLevel.OVERWORLD) return;

        level.getCapability(ModCapabilities.WORLD_CAP).ifPresent(worldData -> {
            long currentWorldTime = worldData.getWorldTime() + 1;
            worldData.setWorldTime(currentWorldTime);

            if (currentWorldTime == lastProcessedTime) return;

            // 每 24000 tick 換季
            if (currentWorldTime % TICKS_PER_SEASON == 0) {
                lastProcessedTime = currentWorldTime;
                int newSeason = (worldData.getSeason() + 1) % 4;
                worldData.setSeason(newSeason);
                System.out.println("[道年系統] 季節變更為: " + getSeasonName(newSeason));
                MinecraftServer server = level.getServer(); // level 是 ServerLevel
                server.getPlayerList().broadcastSystemMessage(
                        Component.literal("§d[道年系統] §r季節變更為：§e" + getSeasonName(newSeason) + "§r！"), false
                );
                // TODO: 你可以在這裡呼叫世界同步封包或事件（例如靈氣變化、世界公告等）
            }

            // 每 96000 tick 增加一年
            if (currentWorldTime % TICKS_PER_YEAR == 0) {
                int newYear = worldData.getYear() + 1;
                worldData.setYear(newYear);
                System.out.println("[道年系統] 新的一年開始！目前為第 " + newYear + " 年");
                increaseAllPlayersAge(level.getServer());
            }
        });
    }

    private static void increaseAllPlayersAge(MinecraftServer server) {
        System.out.println("[壽元系統] 正在為所有玩家增加 1 歲...");

        // ✅ 線上玩家
        for (ServerPlayer player : server.getPlayerList().getPlayers()) {
            player.getCapability(ModCapabilities.AGE_CAP).ifPresent(data -> {
                data.addCurrentAge(1);
                System.out.println("  > 線上玩家 " + player.getName().getString() + " 現在 " + data.getCurrentAge() + " 歲");
                player.sendSystemMessage(Component.literal("年齡增加一歲，現在" + data.getCurrentAge() + "歲"));
            });
        }

        // ✅ 離線玩家
        ResourceLocation capabilityKey = ResourceLocation.fromNamespaceAndPath(RealmMod.MOD_ID, AgeProvider.IDENTIFIER);
        File playerDataFolder = server.getWorldPath(LevelResource.PLAYER_DATA_DIR).toFile();
        File[] playerFiles = playerDataFolder.listFiles((dir, name) -> name.endsWith(".dat"));

        if (playerFiles != null) {
            int updatedCount = 0;
            for (File file : playerFiles) {
                try {
                    String fileName = file.getName().replace(".dat", "");
                    UUID playerUUID = UUID.fromString(fileName);

                    if (server.getPlayerList().getPlayer(playerUUID) != null) {
                        continue;
                    }

                    // 一次性修改
                    boolean success = OfflinePlayerCapabilityManager.modifyOffLinePlayerCapability(
                            server, playerUUID, capabilityKey,
                            ageData -> {
                                int currentAge = ageData.getInt("age");
                                ageData.putInt("age", currentAge + 1);
                                System.out.println("  > 離線玩家 " + fileName + " 現在 " + (currentAge + 1) + " 歲");
                            }
                    );

                    if (success) {
                        updatedCount++;
                    }

                } catch (IllegalArgumentException e) {
                    // 忽略無效檔名
                }
            }
            System.out.println("  > 已更新 " + updatedCount + " 位離線玩家");
        }
    }

    private static String getSeasonName(int season) {
        return switch (season) {
            case 0 -> "春季";
            case 1 -> "夏季";
            case 2 -> "秋季";
            case 3 -> "冬季";
            default -> "未知季節";
        };
    }
}
