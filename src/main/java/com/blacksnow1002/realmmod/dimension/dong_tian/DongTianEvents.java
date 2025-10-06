package com.blacksnow1002.realmmod.dimension.dong_tian;

import com.blacksnow1002.realmmod.RealmMod;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.AABB;
import net.minecraft.core.BlockPos;
import net.minecraftforge.event.entity.EntityJoinLevelEvent;
import net.minecraftforge.event.level.BlockEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.List;
import java.util.UUID;

/**
 * 洞天系統事件監聽器
 * 處理權限檢查、實體限制等
 */
@Mod.EventBusSubscriber(modid = RealmMod.MOD_ID)
public class DongTianEvents {

    /**
     * 防止玩家破壞其他人的洞天
     */
    @SubscribeEvent
    public static void onBlockBreak(BlockEvent.BreakEvent event) {
        if (!(event.getPlayer() instanceof ServerPlayer player)) return;

        ServerLevel world = (ServerLevel) event.getLevel();

        // 只在洞天維度檢查
        if (world.dimension() != DongTianConfig.DONG_TIAN_DIMENSION) {
            return;
        }

        // 檢查是否是玩家自己的洞天
        if (!DongTianManager.isPlayerOwnDongTian(world, event.getPos(), player)) {
            event.setCanceled(true);
            player.sendSystemMessage(Component.literal("§c你不能破壞其他人的洞天！"));
        }
    }

    /**
     * 防止玩家在其他人的洞天放置方塊
     */
    @SubscribeEvent
    public static void onBlockPlace(BlockEvent.EntityPlaceEvent event) {
        if (!(event.getEntity() instanceof ServerPlayer player)) return;

        ServerLevel world = (ServerLevel) event.getLevel();

        if (world.dimension() != DongTianConfig.DONG_TIAN_DIMENSION) {
            return;
        }

        if (!DongTianManager.isPlayerOwnDongTian(world, event.getPos(), player)) {
            event.setCanceled(true);
            player.sendSystemMessage(Component.literal("§c你不能在其他人的洞天建造！"));
        }
    }

    /**
     * 實體生成限制（優化版本：只檢查該洞天範圍內的實體）
     */
    @SubscribeEvent
    public static void onEntityJoinWorld(EntityJoinLevelEvent event) {
        if (event.getLevel().isClientSide()) return;
        if (event.getEntity() instanceof ServerPlayer) return; // 不檢查玩家本身

        ServerLevel world = (ServerLevel) event.getLevel();
        if (world.dimension() != DongTianConfig.DONG_TIAN_DIMENSION) return;

        DongTianChunkAllocator allocator = DongTianChunkAllocator.get(world);
        ChunkPos entityChunkPos = new ChunkPos(event.getEntity().blockPosition());
        UUID owner = allocator.getOwnerAtPosition(entityChunkPos);

        if (owner != null) {
            ServerPlayer ownerPlayer = world.getServer().getPlayerList().getPlayer(owner);
            if (ownerPlayer != null) {
                PlayerDongTianData data = PlayerDongTianData.get(ownerPlayer);
                int limit = data.getEntityLimit();

                // 獲取洞天的範圍邊界
                ChunkPos caveCenter = allocator.allocateChunkForPlayer(owner);
                AABB caveBounds = getCaveBoundingBox(caveCenter);

                // 只檢查該洞天範圍內的實體
                List<Entity> entitiesInCave = world.getEntities(null, caveBounds);

                // 過濾掉玩家
                long count = entitiesInCave.stream()
                        .filter(e -> !(e instanceof ServerPlayer))
                        .count();

                if (count > limit) {
                    event.setCanceled(true);
                    ownerPlayer.sendSystemMessage(
                            Component.literal("§c洞天實體數量已達上限！當前：" + count + "/" + limit)
                    );
                }
            }
        }
    }

    /**
     * 根據洞天中心區塊計算邊界框
     */
    private static AABB getCaveBoundingBox(ChunkPos center) {
        int halfSize = DongTianConfig.DONG_TIAN_SIZE_IN_CHUNKS / 2;

        // 計算邊界（區塊座標轉方塊座標，1區塊 = 16方塊）
        int minX = (center.x - halfSize) * 16;
        int maxX = (center.x + halfSize) * 16;
        int minZ = (center.z - halfSize) * 16;
        int maxZ = (center.z + halfSize) * 16;

        int minY = 0;
        int maxY = DongTianConfig.DONG_TIAN_HEIGHT;

        return new AABB(minX, minY, minZ, maxX, maxY, maxZ);
    }
}