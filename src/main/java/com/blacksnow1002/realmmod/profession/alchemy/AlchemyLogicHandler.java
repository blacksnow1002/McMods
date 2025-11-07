package com.blacksnow1002.realmmod.profession.alchemy;

import com.blacksnow1002.realmmod.block.custom.AlchemyToolBlock;
import com.blacksnow1002.realmmod.block.entity.AlchemyToolBlockEntity;
import com.blacksnow1002.realmmod.capability.ModCapabilities;
import com.blacksnow1002.realmmod.item.ModDataComponents;
import com.blacksnow1002.realmmod.item.ModItems;
import com.blacksnow1002.realmmod.item.custom.AlchemyRecipeItem;
import com.blacksnow1002.realmmod.item.custom.AlchemyAuxiliaryItem;
import com.blacksnow1002.realmmod.profession.alchemy.capability.IProfessionAlchemyData;
import com.blacksnow1002.realmmod.profession.alchemy.capability.ProfessionAlchemyData;
import com.blacksnow1002.realmmod.profession.alchemy.recipe.BaseAlchemyRecipe;
import com.blacksnow1002.realmmod.profession.common.IProfessionHeartDemonData;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.ItemStackHandler;

import java.util.AbstractMap;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

public class AlchemyLogicHandler {

    private static final Random rand = ThreadLocalRandom.current();

    // 同品級丹藥基礎機率
    private static final float SAME_RANK_FLOATING = 0.35f;
    private static final float SAME_RANK_CLOUD = 0.20f;
    private static final float SAME_RANK_SPIRIT = 0.10f;
    private static final float SAME_RANK_DAO = 0.05f;

    // 低品級丹藥基礎機率
    private static final float LOW_RANK_FLOATING = 0.50f;
    private static final float LOW_RANK_CLOUD = 0.25f;
    private static final float LOW_RANK_SPIRIT = 0.15f;
    private static final float LOW_RANK_DAO = 0.10f;

    private static int ALCHEMY_TIME = 600;

    // 完成煉丹
    public static void startAlchemy(Player player, BlockEntity blockEntity, ItemStack recipeItem,
                                       ItemStack demonCore, ItemStack mainMaterial1, ItemStack mainMaterial2,
                                       ItemStack auxiliaryMaterialStack, ItemStackHandler itemHandler) {

        if (!(blockEntity instanceof AlchemyToolBlockEntity furnace)) {
            return;
        }

        if(!(recipeItem.getItem() instanceof AlchemyRecipeItem)) {
            return;
        }

        AlchemyToolBlock.Grades grade = furnace.getGrade();
        AlchemyToolBlock.BonusEntry bonus = furnace.getBonus();
        BaseAlchemyRecipe recipe = ((AlchemyRecipeItem) recipeItem.getItem()).getRecipe();

        LazyOptional<IProfessionAlchemyData> capabilityOptional = player.getCapability(ModCapabilities.PROFESSION_ALCHEMY_CAP);
        LazyOptional<IProfessionHeartDemonData> demonOptional = player.getCapability(ModCapabilities.PROFESSION_HEART_DEMON_CAP);

        if (!capabilityOptional.isPresent() || !demonOptional.isPresent()) {
            player.sendSystemMessage(Component.literal("數據載入失敗，請重新登入"));
            System.err.println("[HarvestEvent] 玩家 " + player.getName().getString() + " 的 Capability 不存在！");
            return;
        }

        IProfessionAlchemyData cap = capabilityOptional.resolve().orElse(null);
        IProfessionHeartDemonData demonCap = demonOptional.resolve().orElse(null);

        int playerRank = cap.getRank();
        int furnaceRank = furnace.getRank();
        int recipeRank = recipe.getRank();
        int rankDiff = recipeRank - furnaceRank;

        if (playerRank > furnaceRank) {
            player.sendSystemMessage(Component.literal("等級不足，無法使用此器具"));
            return;
        }

        if (rankDiff > 0) {
            player.sendSystemMessage(Component.literal("品級不足，無法煉製此丹藥"));
            return;
        }

        if (!recipe.matches(demonCore, mainMaterial1, mainMaterial2)) {
            player.sendSystemMessage(Component.literal("丹方與丹材不匹配，無法煉製此丹藥"));
            return;
        }

        if (!auxiliaryMaterialStack.isEmpty() && !(auxiliaryMaterialStack.getItem() instanceof AlchemyAuxiliaryItem)) {
            player.sendSystemMessage(Component.literal("輔材格請放入輔材"));
            return;
        }

        if (!itemHandler.getStackInSlot(6).isEmpty()) {
            player.sendSystemMessage(Component.literal("請先取出已煉成丹藥"));
            return;
        }

        ServerLevel serverLevel = (ServerLevel) player.level();
        AlchemyTimerManager manager = AlchemyTimerManager.get(serverLevel);

        BlockPos blockPos = blockEntity.getBlockPos();
        if (manager.isAlchemyInProgress(blockPos)) {
            player.sendSystemMessage(Component.literal("此煉丹爐正在煉製中，無法使用"));
            return;
        }

        UUID playerUUID = player.getUUID();
        if (manager.isPlayerInAlchemy(playerUUID)) {
            player.sendSystemMessage(Component.literal("你正在煉丹中，請勿分心"));
            return;
        }

        int count = recipe.evaluateCount(demonCore, mainMaterial1, mainMaterial2, auxiliaryMaterialStack);

        furnace.consumeDurability(count);

        recipe.shrinkMaterial(itemHandler, count);

        PillQuality quality = calculateQuality(cap, recipe, rankDiff, demonCap.isHeartDemon(), auxiliaryMaterialStack);

        ItemStack outputPill;
        String returnText;
        if (quality.isWaste()) {
            returnText = handleWaste(cap, recipe, count, grade, bonus, demonCap, itemHandler);

            outputPill = new ItemStack(ModItems.ALCHEMY_WASTE_ITEM.get(), 1);
        } else {
            AbstractMap.SimpleEntry<Integer, String> result = handleSuccess(cap, recipe, count, grade, bonus, quality, rankDiff);
            int outputCount = result.getKey();
            returnText = result.getValue();

            outputPill = new ItemStack(recipe.getOutputItem(), outputCount);
            outputPill.set(ModDataComponents.PILL_QUALITY.get(), quality.ordinal());
        }

        AlchemyTask task;
        ALCHEMY_TIME *= count;
        if (bonus == AlchemyToolBlock.BonusEntry.ENTRY_6){
            task = new AlchemyTask(blockPos, playerUUID, serverLevel, serverLevel.getServer().overworld().getGameTime() + ALCHEMY_TIME / 2, outputPill, returnText);
            player.sendSystemMessage(Component.literal("開始煉丹，預計" + (ALCHEMY_TIME / 40) +  "秒後煉丹結束"));
        } else {
            task = new AlchemyTask(blockPos, playerUUID, serverLevel, serverLevel.getServer().overworld().getGameTime() + ALCHEMY_TIME, outputPill, returnText);
            player.sendSystemMessage(Component.literal("開始煉丹，預計" + (ALCHEMY_TIME / 20) + "秒後煉丹結束"));
        }

        manager.startAlchemyTask(task);

    }

    private static PillQuality calculateQuality(IProfessionAlchemyData cap, BaseAlchemyRecipe recipe,
                                                int rankDiff, boolean hasHeartDemon, ItemStack auxiliaryMaterialStack) {
        String pillId = recipe.getId();
        ProfessionAlchemyData.AlchemyQualityRate quantityRateBonus = cap.getQualityRateBonus(pillId);

        float floatingChance, cloudChance, spiritChance, daoChance;

        if (rankDiff == 0) {
            floatingChance = SAME_RANK_FLOATING;
            cloudChance = SAME_RANK_CLOUD;
            spiritChance = SAME_RANK_SPIRIT;
            daoChance = SAME_RANK_DAO;
        } else {
            floatingChance = LOW_RANK_FLOATING;
            cloudChance = LOW_RANK_CLOUD;
            spiritChance = LOW_RANK_SPIRIT;
            daoChance = LOW_RANK_DAO;
        }

        floatingChance += quantityRateBonus.floating();
        cloudChance += quantityRateBonus.cloud();
        spiritChance += quantityRateBonus.spirit();
        daoChance += quantityRateBonus.dao();

        if (hasHeartDemon) {
            floatingChance /= 2;
            cloudChance /= 2;
            spiritChance /= 2;
            daoChance /= 2;
        }

        float roll = rand.nextFloat();
        float cumulative = daoChance;

        if (roll < cumulative) {
            return PillQuality.DAO;
        } else if (roll < (cumulative += spiritChance)) {
            return PillQuality.SPIRIT;
        } else if (roll < (cumulative += cloudChance)) {
            return PillQuality.CLOUD;
        } else if  (roll < (cumulative + floatingChance)) {
            return PillQuality.FLOATING;
        } else {
            return PillQuality.WASTE;
        }
        //TODO: 加上輔材適用邏輯(還沒想好)
    }

    private static String handleWaste(IProfessionAlchemyData cap, BaseAlchemyRecipe recipe, int count,
                                      AlchemyToolBlock.Grades grade, AlchemyToolBlock.BonusEntry bonus,
                                      IProfessionHeartDemonData demonCap, ItemStackHandler itemHandler) {

        String returnText = "§c煉丹失敗，產生廢丹!";

        // 詞條一：必定不產生心魔
        if (bonus != AlchemyToolBlock.BonusEntry.ENTRY_1) {
            //20%機率產生心魔
            if (rand.nextDouble() < 0.2) {
                demonCap.setHeartDemon(true);
                returnText += "\n§4你產生了心魔!";
            }
        }

        // 詞條二：返還其中一種主材
        if (bonus == AlchemyToolBlock.BonusEntry.ENTRY_2) {
            if (rand.nextBoolean()) {
                recipe.returnMaterial(3, count, itemHandler);
            } else {
                recipe.returnMaterial(4, count, itemHandler);
            }
            returnText += "\n§a觸發詞條二：返還了部分材料";
        }

        String pillId = recipe.getId();
        ProfessionAlchemyData.AlchemyQualityRate increase = new ProfessionAlchemyData.AlchemyQualityRate(
                grade.getFloatingIncrease() * count,
                grade.getCloudIncrease()  * count,
                grade.getSpiritIncrease()  * count,
                grade.getDaoIncrease() * count
        );
        cap.addQualityRateBonus(pillId, increase);

        return returnText;
    }

    private static AbstractMap.SimpleEntry<Integer, String> handleSuccess(
            IProfessionAlchemyData cap, BaseAlchemyRecipe recipe, int count,
            AlchemyToolBlock.Grades grade, AlchemyToolBlock.BonusEntry bonus,
            PillQuality quality, int rankDiff) {

        String returnText = "§a煉丹成功! 獲得 §d" + quality.getDisplayName() + "§a" + recipe.getDisplayName();

        int outputCount = count;

        // 詞條四：30%機率雙倍產量
        if (bonus == AlchemyToolBlock.BonusEntry.ENTRY_4) {
            if (rand.nextFloat() < 0.3) {
                outputCount *= 2;
                returnText += "\n觸發詞條四：§d§l雙倍產出!";
            }
        }

        // 詞條五：8%機率提升一階品質
        if (bonus == AlchemyToolBlock.BonusEntry.ENTRY_5) {
            if (rand.nextFloat() < 0.08) {
                quality = quality.upgrade();
                returnText += "\n觸發詞條五：丹藥品質提升";
            }
        }



        int baseExp = 10 * count;
        if (rankDiff == 0) {
            float expMultiplier = 1.0f + grade.getExpBonus();

            // 詞條七：額外100%
            if (bonus == AlchemyToolBlock.BonusEntry.ENTRY_7) {
                expMultiplier += 1.0f;
                returnText += "\n觸發詞條七：獲得雙倍煉丹經驗";
            }

            baseExp =(int)(baseExp * expMultiplier);
        } else {
            // 低品級：詞條三才給50%基礎經驗
            if (bonus == AlchemyToolBlock.BonusEntry.ENTRY_3) {
                baseExp /= 2;
            } else {
                baseExp = 0;
            }
        }

        if (baseExp > 0) {
            cap.addExp(baseExp);
            returnText += "\n§b獲得 " + baseExp + " 點煉丹經驗";
        }

        return new AbstractMap.SimpleEntry<>(outputCount, returnText);
    }

    public static void finishAlchemy(ItemStack outputPull, BlockPos pos, ServerLevel serverLevel) {
        BlockEntity blockEntity = serverLevel.getBlockEntity(pos);
        if (!(blockEntity instanceof AlchemyToolBlockEntity furnace)) {
            return;
        }
        ItemStackHandler itemHandler = furnace.getItemHandler();
        itemHandler.setStackInSlot(6, outputPull);
    }


}
