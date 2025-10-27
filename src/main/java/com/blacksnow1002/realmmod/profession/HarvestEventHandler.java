package com.blacksnow1002.realmmod.profession;

import com.blacksnow1002.realmmod.RealmMod;
import com.blacksnow1002.realmmod.block.ModBlocks;
import com.blacksnow1002.realmmod.block.custom.HarvestableBlock;
import com.blacksnow1002.realmmod.broadcast.BroadcastManager;
import com.blacksnow1002.realmmod.capability.ModCapabilities;
import com.blacksnow1002.realmmod.item.custom.HarvestToolItem;
import com.blacksnow1002.realmmod.profession.harvest.IProfessionHarvestData;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.event.level.BlockEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

@Mod.EventBusSubscriber(modid = RealmMod.MOD_ID)
public class HarvestEventHandler {

    private static final Random RANDOM = ThreadLocalRandom.current();
    private static final int DAILY_TREASURE_LIMIT = 1; // 每日天材地寶上限

    @SubscribeEvent
    public static void onHarvest(BlockEvent.BreakEvent event) {
        Player player = event.getPlayer();
        Level level = (Level) event.getLevel();
        BlockPos pos = event.getPos();
        BlockState blockState = level.getBlockState(pos);
        Block block = blockState.getBlock();

        // 檢查是否為可採集方塊
        if (!(block instanceof HarvestableBlock harvestableBlock)) {
            return;
        }

        ItemStack held = player.getMainHandItem();
        if (!(held.getItem() instanceof HarvestToolItem tool)) {
            event.setCanceled(true);
            player.sendSystemMessage(Component.literal("§c你需要使用對應的採集工具!"));
            return;
        }

        // 處理採集邏輯
        LazyOptional<IProfessionHarvestData> capabilityOptional = player.getCapability(ModCapabilities.PROFESSION_HARVEST_CAP);

        if (!capabilityOptional.isPresent()) {
            event.setCanceled(true);
            player.sendSystemMessage(Component.literal("數據載入失敗，請重新登入"));
            System.err.println("[HarvestEvent] 玩家 " + player.getName().getString() + " 的 Capability 不存在！");
            return;
        }

        IProfessionHarvestData cap = capabilityOptional.resolve().orElse(null);

        int playerRank = cap.getRank();
        int toolRank = tool.getRank();
        int blockRank = harvestableBlock.getRank();

        // 檢查職業品階
        if (playerRank == 0) {
            event.setCanceled(true);
            player.sendSystemMessage(Component.literal("§c你尚未入門此職業!"));
            return;
        }

        if (playerRank < toolRank) {
            event.setCanceled(true);
            player.sendSystemMessage(Component.literal("§c職業品階不足，無法使用此工具!"));
            return;
        }

        // 檢查是否可以採集此品級
        int rankDiff = blockRank - playerRank;
        if (rankDiff > 1) {
            event.setCanceled(true);
            player.sendSystemMessage(Component.literal("§c品階差距過大，無法採集!"));
            return;
        }

        if (blockRank - toolRank > 1) {
            event.setCanceled(true);
            player.sendSystemMessage(Component.literal("§c工具品階不足!"));
            return;
        }

        // 計算成功率並處理採集
        HarvestResult result = calculateHarvest(cap, tool, held, harvestableBlock, rankDiff);

        // 取消原本的掉落
        event.setCanceled(true);

        if (result.success) {
            if (!level.isClientSide && level instanceof ServerLevel serverLevel) {
                serverLevel.setBlockAndUpdate(pos, Blocks.AIR.defaultBlockState());
                HarvestRespawnManager.recordHarvested(serverLevel, pos, block, blockState);
            }

            handleSuccess(player, cap, harvestableBlock, tool, held, result);
        } else {
            handleFailure(player, cap, harvestableBlock, tool, held, result);
        }
    }



    private static HarvestResult calculateHarvest(IProfessionHarvestData cap, HarvestToolItem tool,
                                                  ItemStack toolStack, HarvestableBlock block,
                                                  int rankDiff) {
        HarvestResult result = new HarvestResult();
        HarvestableBlock.HarvestType blockType = block.getType();
        String blockId = block.toString();
        HarvestToolItem.BonusEntry bonus = HarvestToolItem.getBonus(toolStack);
        HarvestToolItem.Grades grade = tool.getGrade();

        double baseRate = blockType.getBaseSameSuccessRate();
        double finalRate = baseRate;

        // 同品級
        if (rankDiff == 0) {
            // 累積成功率(稀有產物)
            if (blockType == HarvestableBlock.HarvestType.RARE) {
                double accumulated = cap.getSuccessRateBonus(blockId);
                finalRate += accumulated;
            }

            // 普通產物工具加成(天地玄)
            if (blockType == HarvestableBlock.HarvestType.COMMON) {
                finalRate += grade.getSuccessBonus();
            }
        }
        // 低品級(100%成功)
        else if (rankDiff < 0) {
            finalRate = 1.0;
            result.noExp = true;
        }
        // 高一品級
        else if (rankDiff == 1) {
            if (blockType == HarvestableBlock.HarvestType.COMMON) {
                finalRate = 0.5;
                result.heartDemonChance = 0.4;

                // 詞條四：高一階普通產物必定成功
                if (bonus == HarvestToolItem.BonusEntry.ENTRY_4) {
                    finalRate = 1.0;
                }

                // 詞條六：套用基礎成功率
                if (bonus == HarvestToolItem.BonusEntry.ENTRY_6) {
                    finalRate = baseRate;
                }
            } else if (blockType == HarvestableBlock.HarvestType.RARE) {
                finalRate = 0.1;
                result.heartDemonChance = 0.8;
                result.killOnFail = true;
                result.noTreasure = true;

                // 詞條六：套用基礎成功率
                if (bonus == HarvestToolItem.BonusEntry.ENTRY_6) {
                    finalRate = baseRate;
                    result.noFirstBonus = true;
                }
            }

            // 詞條一：高一階+10%且不觸發心魔
            if (bonus == HarvestToolItem.BonusEntry.ENTRY_1) {
                finalRate += 0.1;
                result.heartDemonChance = 0.0;
            }
        }

        // 心魔狀態：成功率減半
        if (cap.isHeartDemon()) {
            finalRate *= 0.5;
            result.noTreasure = true;
        }

        // 判定成功
        result.success = RANDOM.nextDouble() < finalRate;
        result.finalRate = finalRate;

        return result;
    }

    private static void handleSuccess(Player player, IProfessionHarvestData cap,
                                      HarvestableBlock block, HarvestToolItem tool,
                                      ItemStack toolStack, HarvestResult result) {
        HarvestableBlock.HarvestType blockType = block.getType();
        HarvestToolItem.Grades grade = tool.getGrade();
        HarvestToolItem.BonusEntry bonus = HarvestToolItem.getBonus(toolStack);
        int blockRank = block.getRank();
        int toolRank = tool.getRank();
        int rankDiff = blockRank - toolRank;

        // 掉落物品
        int dropCount = 1;
        if (bonus == HarvestToolItem.BonusEntry.ENTRY_5 && rankDiff == 0 &&
                blockType != HarvestableBlock.HarvestType.TREASURE) {
            if (RANDOM.nextDouble() < 0.3) {
                dropCount = 2;
            }
        }

        player.addItem(new ItemStack(block,  dropCount));
        player.sendSystemMessage(Component.literal("§a採集成功! 獲得 " + dropCount + " 個物品"));

        // 經驗計算
        if (!result.noExp && rankDiff >= 0) {
            int baseExp = blockType.getBaseExp();
            if (rankDiff == 1) {
                baseExp = blockType == HarvestableBlock.HarvestType.COMMON ? 20 : 40;
            }

            double expMultiplier = 1.0 + grade.getExpBonus();

            // 詞條三：額外100%經驗
            if (bonus == HarvestToolItem.BonusEntry.ENTRY_3) {
                expMultiplier += 1.0;
            }

            // 首次成功獎勵
            boolean isFirstSuccess = !cap.hasFirstSuccess(blockRank);
            if (isFirstSuccess && blockType == HarvestableBlock.HarvestType.RARE &&
                    rankDiff == 0 && !result.noFirstBonus) {
                baseExp += 160;
                cap.setFirstSuccess(blockRank);

                // TODO: 給予天材地寶、成就、稱號
                player.addItem(new ItemStack(getTreasureByRank(blockRank), 1));
                BroadcastManager.broadcast("玩家" + player.getName().getString() + "不懼生死，跨階獲得" + block + "品產物" + block.getName().getString());
            }

            int finalExp = (int)(baseExp * expMultiplier);

            cap.addExp(finalExp);
            player.sendSystemMessage(Component.literal("§b獲得 " + finalExp + " 點職業經驗"));
        }

        // 天材地寶判定
        if (!result.noTreasure && blockType == HarvestableBlock.HarvestType.RARE) {
            double treasureChance = 0.01;

            // 詞條二：提升至5%
            if (bonus == HarvestToolItem.BonusEntry.ENTRY_2 && rankDiff == 0) {
                treasureChance = 0.05;
            }

            if (RANDOM.nextDouble() < treasureChance) {
                // 低品級採集需要檢查每日上限
                if (rankDiff < 0) {
                    if (cap.getDailyTreasureCount(blockRank) < DAILY_TREASURE_LIMIT) {
                        cap.incrementDailyTreasure(blockRank);

                        player.addItem(new ItemStack(getTreasureByRank(blockRank), 1));
                        player.sendSystemMessage(Component.literal("§d§l恭喜! 獲得天材地寶!"));
                    }
                } else {
                    player.addItem(new ItemStack(getTreasureByRank(blockRank),  1));
                    player.sendSystemMessage(Component.literal("§d§l恭喜! 獲得天材地寶!"));
                }
            }
        }

        // 詞條七：恢復氣血
        if (bonus == HarvestToolItem.BonusEntry.ENTRY_7) {
            player.heal(4.0f); // TODO: 回復數值需修改
        }

        // 消耗耐久
        if (!HarvestToolItem.consumeDurability(toolStack, grade)) {
            toolStack.shrink(1);
            player.sendSystemMessage(Component.literal("§c工具已損壞!"));
        }
    }

    private static void handleFailure(Player player, IProfessionHarvestData cap,
                                      HarvestableBlock block, HarvestToolItem tool,
                                      ItemStack toolStack, HarvestResult result) {
        HarvestableBlock.HarvestType blockType = block.getType();
        HarvestToolItem.Grades grade = tool.getGrade();
        int blockRank = block.getRank();
        int playerRank = cap.getRank();
        int rankDiff = blockRank - playerRank;
        String blockId = block.toString();

        player.sendSystemMessage(Component.literal("§c採集失敗!"));

        // 扣除生命
        if (rankDiff >= 0) {
            float damage = rankDiff == 0 ? 4.0f : 8.0f; //TODO: 設定扣除數值
            player.hurt(player.damageSources().magic(), damage);
        }

        // 高一階稀有產物失敗：殺死角色並扣除修為
        if (result.killOnFail) {
            player.kill();
            // TODO: 扣除修為(需要你的修為系統)
            player.sendSystemMessage(Component.literal("§4§l採集失敗，走火入魔而亡!"));
            return;
        }

        // 心魔判定
        if (result.heartDemonChance > 0 && RANDOM.nextDouble() < result.heartDemonChance) {
            cap.setHeartDemon(true);
            player.sendSystemMessage(Component.literal("§4你產生了心魔! 採集成功率減半!"));
        }

        // 稀有產物失敗時增加成功率(使用玄階以上工具)
        if (blockType == HarvestableBlock.HarvestType.RARE && rankDiff == 0) {
            double increment = 0.0;
            if (grade == HarvestToolItem.Grades.MYSTIC) {
                increment = 0.005; // 0.5%
            } else if (grade == HarvestToolItem.Grades.EARTH || grade == HarvestToolItem.Grades.HEAVEN) {
                increment = 0.01; // 1%
            }

            if (increment > 0) {
                cap.addSuccessRateBonus(blockId, increment);
                double newRate = cap.getSuccessRateBonus(blockId);
                player.sendSystemMessage(Component.literal(
                        String.format("§7該產物成功率提升至 %.1f%%", (blockType.getBaseHighSuccessRate() + newRate + 0.5) * 100)
                ));
            }
        }

        // 消耗耐久
        if (!HarvestToolItem.consumeDurability(toolStack, grade)) {
            toolStack.shrink(1);
            player.sendSystemMessage(Component.literal("§c工具已損壞!"));
        }
    }

    // 採集結果類
    private static class HarvestResult {
        boolean success = false;
        boolean noExp = false;
        boolean noTreasure = false;
        boolean killOnFail = false;
        boolean noFirstBonus = false;
        double heartDemonChance = 0.0;
        double finalRate = 0.0;
    }

    private static Block getTreasureByRank(int rank) {
        return switch (rank) {
            case 9 -> ModBlocks.HARVESTABLE_BLOCK_9_TREASURE_1.get();
            default -> Blocks.AIR;
        };
    }
}