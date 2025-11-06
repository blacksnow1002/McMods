package com.blacksnow1002.realmmod.profession.alchemy;

import com.blacksnow1002.realmmod.block.custom.AlchemyToolBlock;
import com.blacksnow1002.realmmod.block.entity.AlchemyToolBlockEntity;
import com.blacksnow1002.realmmod.capability.ModCapabilities;
import com.blacksnow1002.realmmod.item.ModDataComponents;
import com.blacksnow1002.realmmod.item.custom.AlchemyRecipeItem;
import com.blacksnow1002.realmmod.item.custom.AlchemyAuxiliaryItem;
import com.blacksnow1002.realmmod.profession.alchemy.capability.IProfessionAlchemyData;
import com.blacksnow1002.realmmod.profession.alchemy.capability.ProfessionAlchemyData;
import com.blacksnow1002.realmmod.profession.alchemy.recipe.BaseAlchemyRecipe;
import com.blacksnow1002.realmmod.profession.common.IProfessionHeartDemonData;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.ItemStackHandler;

import java.util.Random;
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

    // 煉丹基礎時間（ticks）30秒 = 600 ticks
//    private static final int BASE_ALCHEMY_TIME = 600;
//
//    public static int startAlchemy(ItemStack furnace) {
//
//        AlchemyToolItem.BonusEntry bonus = AlchemyToolItem.getBonus(furnace);
//        int duration = BASE_ALCHEMY_TIME;
//
//        if (bonus == AlchemyToolItem.BonusEntry.ENTRY_6) {
//            duration /= 2 ;
//        }
//
//        return duration;
//    }

    // 完成煉丹
    public static Boolean startAlchemy(Player player, BlockEntity blockEntity, ItemStack recipeItem,
                                       ItemStack demonCore, ItemStack mainMaterial1, ItemStack mainMaterial2,
                                       ItemStack auxiliaryMaterialStack, ItemStackHandler itemHandler) {

        if (!(blockEntity instanceof AlchemyToolBlockEntity furnace)) {
            return false;
        }

        if(!(recipeItem.getItem() instanceof AlchemyRecipeItem)) {
            return false;
        }

        AlchemyToolBlock.Grades grade = furnace.getGrade();
        AlchemyToolBlock.BonusEntry bonus = furnace.getBonus();
        BaseAlchemyRecipe recipe = ((AlchemyRecipeItem) recipeItem.getItem()).getRecipe();

        LazyOptional<IProfessionAlchemyData> capabilityOptional = player.getCapability(ModCapabilities.PROFESSION_ALCHEMY_CAP);
        LazyOptional<IProfessionHeartDemonData> demonOptional = player.getCapability(ModCapabilities.PROFESSION_HEART_DEMON_CAP);

        if (!capabilityOptional.isPresent() || !demonOptional.isPresent()) {
            player.sendSystemMessage(Component.literal("數據載入失敗，請重新登入"));
            System.err.println("[HarvestEvent] 玩家 " + player.getName().getString() + " 的 Capability 不存在！");
            return false;
        }

        IProfessionAlchemyData cap = capabilityOptional.resolve().orElse(null);
        IProfessionHeartDemonData demonCap = demonOptional.resolve().orElse(null);

        int playerRank = cap.getRank();
        int furnaceRank = furnace.getRank();
        int recipeRank = recipe.getRank();
        int rankDiff = recipeRank - furnaceRank;

        if (playerRank > furnaceRank) {
            player.sendSystemMessage(Component.literal("等級不足，無法使用此器具"));
            return false;
        }

        if (rankDiff > 0) {
            player.sendSystemMessage(Component.literal("品級不足，無法煉製此丹藥"));
            return false;
        }

        if (!recipe.matches(demonCore, mainMaterial1, mainMaterial2)) {
            player.sendSystemMessage(Component.literal("丹方與丹材不匹配，無法煉製此丹藥"));
            return false;
        }

        if (!auxiliaryMaterialStack.isEmpty() && !(auxiliaryMaterialStack.getItem() instanceof AlchemyAuxiliaryItem)) {
            player.sendSystemMessage(Component.literal("輔材格請放入輔材"));
            return false;
        }

        furnace.consumeDurability();

        recipe.shrinkMaterial(itemHandler);

        PillQuality quality = calculateQuality(cap, recipe, rankDiff, demonCap.isHeartDemon(), auxiliaryMaterialStack);

        if (quality.isWaste()) {
            handleWaste(player, cap, recipe, grade, bonus, demonCap, itemHandler);
            return true;
        } else {
            handleSuccess(player, cap, recipe, grade, bonus, quality, rankDiff, itemHandler);
            return true;
        }
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

    private static void handleWaste(Player player, IProfessionAlchemyData cap,
                                    BaseAlchemyRecipe recipe, AlchemyToolBlock.Grades grade,
                                    AlchemyToolBlock.BonusEntry bonus,
                                    IProfessionHeartDemonData demonCap,
                                    ItemStackHandler itemHandler) {

        player.sendSystemMessage(Component.literal("§c煉製失敗，產生廢丹!"));

        // 詞條一：必定不產生心魔
        if (bonus != AlchemyToolBlock.BonusEntry.ENTRY_1) {
            //20%機率產生心魔
            if (rand.nextDouble() < 0.2) {
                demonCap.setHeartDemon(true);
                player.sendSystemMessage(Component.literal("§4你產生了心魔!"));
            }
        }

        // 詞條二：返還其中一種主材
        if (bonus == AlchemyToolBlock.BonusEntry.ENTRY_2) {
            if (rand.nextBoolean()) {
                recipe.returnMaterial(3, itemHandler);
            } else {
                recipe.returnMaterial(4, itemHandler);
            }
            player.sendSystemMessage(Component.literal("§a返還了部分材料"));
        }

        String pillId = recipe.getId();
        ProfessionAlchemyData.AlchemyQualityRate increase = new ProfessionAlchemyData.AlchemyQualityRate(
                grade.getFloatingIncrease(),
                grade.getCloudIncrease(),
                grade.getSpiritIncrease(),
                grade.getDaoIncrease()
        );
        cap.addQualityRateBonus(pillId, increase);

    }

    private static void handleSuccess(Player player, IProfessionAlchemyData cap, BaseAlchemyRecipe recipe,
                                               AlchemyToolBlock.Grades grade, AlchemyToolBlock.BonusEntry bonus,
                                               PillQuality quality, int rankDiff, ItemStackHandler itemHandler) {

        player.sendSystemMessage(Component.literal(
                "§a煉製成功! 獲得 §d" + quality.getDisplayName() + "§a 丹藥!"
        ));

        int outputCount = 1;

        // 詞條四：30%機率雙倍產量
        if (bonus == AlchemyToolBlock.BonusEntry.ENTRY_4) {
            if (rand.nextFloat() < 0.3) {
                outputCount *= 2;
                player.sendSystemMessage(Component.literal("§d§l雙倍產出!"));
            }
        }

        // 詞條五：8%機率提升一階品質
        if (bonus == AlchemyToolBlock.BonusEntry.ENTRY_5) {
            if (rand.nextFloat() < 0.08) {
                quality = quality.upgrade();
                player.sendSystemMessage(Component.literal("觸發詞條五，丹藥品質提升"));
            }
        }



        int baseExp = 10;
        if (rankDiff == 0) {
            float expMultiplier = 1.0f + grade.getExpBonus();

            // 詞條七：額外100%
            if (bonus == AlchemyToolBlock.BonusEntry.ENTRY_7) expMultiplier += 1.0f;

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
            player.sendSystemMessage(Component.literal("§b獲得 " + baseExp + " 點職業經驗"));
        }

        ItemStack outputPill = new ItemStack(recipe.getOutputItem(), outputCount);
        outputPill.set(ModDataComponents.PILL_QUALITY.get(), quality.ordinal());
        itemHandler.setStackInSlot(6, outputPill);
    }
}
