package com.blacksnow1002.realmmod.item.custom;

import com.blacksnow1002.realmmod.capability.ModCapabilities;
import com.blacksnow1002.realmmod.capability.realm_breakthrough.RealmBreakthroughData;
import com.blacksnow1002.realmmod.item.ModItems;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

import java.util.List;
import java.util.Map;

public class ElixirItem extends Item {

    public ElixirItem(Properties properties) {
        super(properties);
    }

    @Override
    public ItemStack finishUsingItem(ItemStack stack, Level level, LivingEntity entity) {
        // 只在伺服器端執行
        if (!level.isClientSide && entity instanceof Player) {
            Player player = (Player) entity;

            boolean isFoundationPill = stack.getItem() == ModItems.FOUNDATION_BUILD_ELIXIR.get();

            if(isFoundationPill) {
                player.getCapability(ModCapabilities.BREAKTHROUGH_CAPABILITY_CAP).ifPresent(data -> {
                    if (!data.getRealmConditionFinished(1, 3)) {
                        data.updateCondition(1, 3, true);
                        player.sendSystemMessage(Component.literal("首次服用築基丹"));
                        player.sendSystemMessage(Component.literal("完成築基任務-服用築基丹"));

                        // 顯示詳細的任務狀態
                        displayBreakthroughStatus(player, data, 1);
                    }
                });
            }
        }
        return stack;
    }

    /**
     * 顯示突破任務的詳細狀態
     * @param player 玩家
     * @param data 突破數據
     * @param realmIndex 境界索引
     */
    private void displayBreakthroughStatus(Player player, com.blacksnow1002.realmmod.capability.realm_breakthrough.IRealmBreakthroughData data, int realmIndex) {
        // 確保 data 是 RealmBreakthroughData 類型
        if (!(data instanceof RealmBreakthroughData)) {
            return;
        }

        RealmBreakthroughData breakthroughData = (RealmBreakthroughData) data;

        // 顯示進度
        player.sendSystemMessage(Component.literal("目前完成進度：" + data.getBreakthroughProgress(realmIndex)));

        // 獲取任務狀態
        Map<String, List<RealmBreakthroughData.BreakthroughCondition>> status =
                breakthroughData.getConditionStatus(realmIndex);

        List<RealmBreakthroughData.BreakthroughCondition> completed = status.get("completed");
        List<RealmBreakthroughData.BreakthroughCondition> incomplete = status.get("incomplete");

        // 顯示已完成的任務
        if (!completed.isEmpty()) {
            for (RealmBreakthroughData.BreakthroughCondition condition : completed) {
                player.sendSystemMessage(Component.literal("§a[已完成] " + condition.getDescription()));
            }
        }

        // 顯示未完成的任務
        if (!incomplete.isEmpty()) {
            for (RealmBreakthroughData.BreakthroughCondition condition : incomplete) {
                player.sendSystemMessage(Component.literal("§c[未完成] " + condition.getDescription()));
            }
        }
    }
}