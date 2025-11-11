package com.blacksnow1002.realmmod.system.profession.reforge;

import com.blacksnow1002.realmmod.system.profession.reforge.block.ReforgeToolBlock;
import com.blacksnow1002.realmmod.system.profession.reforge.block.ReforgeToolBlockEntity;
import com.blacksnow1002.realmmod.core.capability.ModCapabilities;
import com.blacksnow1002.realmmod.core.registry.ModItems;
import com.blacksnow1002.realmmod.system.profession.reforge.item.ReforgeAuxiliaryItem;
import com.blacksnow1002.realmmod.system.profession.reforge.item.ReforgeMainAffixItem;
import com.blacksnow1002.realmmod.system.profession.reforge.item.ReforgeMainElementItem;
import com.blacksnow1002.realmmod.system.profession.reforge.item.ReforgeRecipeItem;
import com.blacksnow1002.realmmod.system.profession.reforge.capability.IProfessionReforgeData;
import com.blacksnow1002.realmmod.system.profession.reforge.capability.ProfessionReforgeData;
import com.blacksnow1002.realmmod.system.profession.reforge.recipe.BaseReforgeRecipe;
import com.blacksnow1002.realmmod.system.profession.base.capability.IProfessionHeartDemonData;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.ItemStackHandler;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

import static com.blacksnow1002.realmmod.system.profession.reforge.affix.AffixLogicHandler.handleAffix;

public class ReforgeLogicHandler {

    private static final Random rand = ThreadLocalRandom.current();

    // 同品級丹藥基礎機率
    private static final float SAME_RANK_MORTAL = 0.35f;
    private static final float SAME_RANK_MYSTIC = 0.20f;
    private static final float SAME_RANK_EARTH = 0.10f;
    private static final float SAME_RANK_HEAVEN = 0.05f;

    // 低品級丹藥基礎機率
    private static final float LOW_RANK_MORTAL = 0.50f;
    private static final float LOW_RANK_MYSTIC = 0.25f;
    private static final float LOW_RANK_EARTH = 0.15f;
    private static final float LOW_RANK_HEAVEN = 0.10f;

    private static int REFORGE_TIME = 1200;

    public static void startReforge(Player player, BlockEntity blockEntity, ItemStack recipeItem,
                                    ItemStack core, ItemStack mainMaterial1, ItemStack mainMaterial2,
                                    ItemStack auxiliaryMaterialStack, ItemStackHandler itemHandler) {
        
        if (!(blockEntity instanceof ReforgeToolBlockEntity furnace)) {
            return;
        }

        if(!(recipeItem.getItem() instanceof ReforgeRecipeItem)) {
            return;
        }

        ReforgeToolBlock.Grades grade = furnace.getGrade();
        ReforgeToolBlock.BonusEntry bonus = furnace.getBonus();
        BaseReforgeRecipe recipe = ((ReforgeRecipeItem) recipeItem.getItem()).getRecipe();

        LazyOptional<IProfessionReforgeData> capabilityOptional = player.getCapability(ModCapabilities.PROFESSION_REFORGE_CAP);
        LazyOptional<IProfessionHeartDemonData> demonOptional = player.getCapability(ModCapabilities.PROFESSION_HEART_DEMON_CAP);

        if (!capabilityOptional.isPresent() || !demonOptional.isPresent()) {
            player.sendSystemMessage(Component.literal("數據載入失敗，請重新登入"));
            System.err.println("[HarvestEvent] 玩家 " + player.getName().getString() + " 的 Capability 不存在！");
            return;
        }

        IProfessionReforgeData cap = capabilityOptional.resolve().orElse(null);
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
            player.sendSystemMessage(Component.literal("品級不足，無法煉製此器物"));
            return;
        }

        if (mainMaterial1.isEmpty() || !(mainMaterial1.getItem() instanceof ReforgeMainElementItem) ) {
            player.sendSystemMessage(Component.literal("主材1請放入屬性類主材"));
            return;
        }

        if (mainMaterial2.isEmpty() || !(mainMaterial2.getItem() instanceof ReforgeMainAffixItem) ) {
            player.sendSystemMessage(Component.literal("主材2請放入詞條類主材"));
            return;
        }

        if (!recipe.matches(core, mainMaterial1, mainMaterial2)) {
            player.sendSystemMessage(Component.literal("圖紙與耗材不匹配或數量，無法煉製此器物"));
            return;
        }

        if (!auxiliaryMaterialStack.isEmpty() && !(auxiliaryMaterialStack.getItem() instanceof ReforgeAuxiliaryItem)) {
            player.sendSystemMessage(Component.literal("輔材格請放入輔材"));
            return;
        }

        if (!itemHandler.getStackInSlot(6).isEmpty()) {
            player.sendSystemMessage(Component.literal("請先取出已煉成器物"));
            return;
        }

        ServerLevel serverLevel = (ServerLevel) player.level();
        ReforgeTimerManager manager = ReforgeTimerManager.get(serverLevel);

        BlockPos blockPos = blockEntity.getBlockPos();
        if (manager.isReforgeInProgress(blockPos)) {
            player.sendSystemMessage(Component.literal("此煉器鼎正在煉製中，無法使用"));
            return;
        }

        UUID playerUUID = player.getUUID();
        if (manager.isPlayerInReforge(playerUUID)) {
            player.sendSystemMessage(Component.literal("你正在煉器中，請勿分心"));
            return;
        }

        furnace.consumeDurability();

        recipe.shrinkMaterial(itemHandler);

        ArtifactQuality quality = calculateQuality(cap, recipe, rankDiff, demonCap.isHeartDemon(), auxiliaryMaterialStack);

        // 詞條五：8%機率提升一階品質
        boolean upgrade = false;
        if (bonus == ReforgeToolBlock.BonusEntry.ENTRY_5) {
            if (rand.nextFloat() < 0.08) {
                upgrade = true;
                quality = quality.upgrade();
            }
        }


        String returnText;
        ItemStack outputArtifact;
        if (quality.isWaste()) {
            returnText = handleWaste(cap, recipe, grade, bonus, demonCap, itemHandler);

            outputArtifact = new ItemStack(ModItems.ALCHEMY_WASTE_ITEM.get(), 1);
        } else {
            returnText = handleSuccess(cap, recipe, grade, bonus, quality, rankDiff);

            if (upgrade) returnText += "\n觸發詞條五：法寶品質提升";
            outputArtifact = handleAffix(recipe, quality, mainMaterial1, mainMaterial2);
        }

        ReforgeTask task;
        if (bonus == ReforgeToolBlock.BonusEntry.ENTRY_6){
            task = new ReforgeTask(blockPos, playerUUID, serverLevel, serverLevel.getServer().overworld().getGameTime() + REFORGE_TIME / 2, outputArtifact, returnText);
            player.sendSystemMessage(Component.literal("開始煉器，預計" + (REFORGE_TIME / 40) +  "秒後煉器結束"));
        } else {
            task = new ReforgeTask(blockPos, playerUUID, serverLevel, serverLevel.getServer().overworld().getGameTime() + REFORGE_TIME, outputArtifact, returnText);
            player.sendSystemMessage(Component.literal("開始煉器，預計" + (REFORGE_TIME / 20) + "秒後煉器結束"));
        }

        manager.startReforgeTask(task);

    }

    private static ArtifactQuality calculateQuality(IProfessionReforgeData cap, BaseReforgeRecipe recipe,
                                                int rankDiff, boolean hasHeartDemon, ItemStack auxiliaryMaterialStack) {
        String ArtifactId = recipe.getId();
        ProfessionReforgeData.ReforgeQualityRate quantityRateBonus = cap.getQualityRateBonus(ArtifactId);

        float mortalChance, mysticChance, earthChance, heavenChance;

        if (rankDiff == 0) {
            mortalChance = SAME_RANK_MORTAL;
            mysticChance = SAME_RANK_MYSTIC;
            earthChance = SAME_RANK_EARTH;
            heavenChance = SAME_RANK_HEAVEN;
        } else {
            mortalChance = LOW_RANK_MORTAL;
            mysticChance = LOW_RANK_MYSTIC;
            earthChance = LOW_RANK_EARTH;
            heavenChance = LOW_RANK_HEAVEN;
        }

        mortalChance += quantityRateBonus.mortal();
        mysticChance += quantityRateBonus.mystic();
        earthChance += quantityRateBonus.earth();
        heavenChance += quantityRateBonus.heaven();

        if (hasHeartDemon) {
            mortalChance /= 2;
            mysticChance /= 2;
            earthChance /= 2;
            heavenChance /= 2;
        }

        float roll = rand.nextFloat();
        float cumulative = heavenChance;

        if (roll < cumulative) {
            return ArtifactQuality.HEAVEN;
        } else if (roll < (cumulative += earthChance)) {
            return ArtifactQuality.EARTH;
        } else if (roll < (cumulative += mysticChance)) {
            return ArtifactQuality.MYSTIC;
        } else if  (roll < (cumulative + mortalChance)) {
            return ArtifactQuality.MORTAL;
        } else {
            return ArtifactQuality.WASTE;
        }
        //TODO: 加上輔材適用邏輯(還沒想好)
    }

    private static String handleWaste(
            IProfessionReforgeData cap, BaseReforgeRecipe recipe,
            ReforgeToolBlock.Grades grade, ReforgeToolBlock.BonusEntry bonus,
            IProfessionHeartDemonData demonCap, ItemStackHandler itemHandler) {

        String returnText = "§c煉丹失敗，產生廢丹!";

        // 詞條一：必定不產生心魔
        if (bonus != ReforgeToolBlock.BonusEntry.ENTRY_1) {
            //20%機率產生心魔
            if (rand.nextDouble() < 0.2) {
                demonCap.setHeartDemon(true);
                returnText += "\n§4你產生了心魔!";
            }
        }

        // 詞條二：返還其中一種主材
        if (bonus == ReforgeToolBlock.BonusEntry.ENTRY_2) {
            if (rand.nextBoolean()) {
                recipe.returnMaterial(3, itemHandler);
            } else {
                recipe.returnMaterial(4, itemHandler);
            }
            returnText += "\n§a觸發詞條二：返還了部分材料";
        }

        String artifactId = recipe.getId();
        ProfessionReforgeData.ReforgeQualityRate increase = new ProfessionReforgeData.ReforgeQualityRate(
                grade.getMortalIncrease(),
                grade.getMysticIncrease(),
                grade.getEarthIncrease(),
                grade.getHeavenIncrease()
        );
        cap.addQualityRateBonus(artifactId, increase);

        return returnText;
    }

    private static  String handleSuccess(
            IProfessionReforgeData cap, BaseReforgeRecipe recipe,
            ReforgeToolBlock.Grades grade, ReforgeToolBlock.BonusEntry bonus,
            ArtifactQuality quality, int rankDiff) {

        String returnText = "§a煉器成功! 獲得 §d" + quality.getDisplayName() + "§a" + recipe.getDisplayName();

        // 詞條四：抽取特殊詞條
        if (bonus == ReforgeToolBlock.BonusEntry.ENTRY_4) {
            if (rand.nextFloat() < 0.3) {
                returnText += "\n觸發詞條四：§d§l雙倍產出!";
            }
        }

        int baseExp = 10;
        if (rankDiff == 0) {
            float expMultiplier = 1.0f + grade.getExpBonus();

            // 詞條七：額外100%
            if (bonus == ReforgeToolBlock.BonusEntry.ENTRY_7) {
                expMultiplier += 1.0f;
                returnText += "\n觸發詞條七：獲得雙倍煉器經驗";
            }

            baseExp =(int)(baseExp * expMultiplier);
        } else {
            // 低品級：詞條三才給50%基礎經驗
            if (bonus == ReforgeToolBlock.BonusEntry.ENTRY_3) {
                baseExp /= 2;
            } else {
                baseExp = 0;
            }
        }

        if (baseExp > 0) {
            cap.addExp(baseExp);
            returnText += "\n§b獲得 " + baseExp + " 點煉器經驗";
        }

        return returnText;
    }
}
