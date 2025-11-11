package com.blacksnow1002.realmmod.system.profession.harvest.command;

import com.blacksnow1002.realmmod.core.capability.ModCapabilities;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;

public class ProfessionHarvestCommand {

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("harvest")
                .requires(source -> source.hasPermission(2))

                // 查詢玩家職業信息
                .then(Commands.literal("info")
                        .then(Commands.argument("player", EntityArgument.player())
                                .executes(context -> showInfo(context, EntityArgument.getPlayer(context, "player")))
                        )
                        .executes(context -> showInfo(context, context.getSource().getPlayerOrException()))
                )

                // 設置職業等級
                .then(Commands.literal("setrank")
                        .then(Commands.argument("player", EntityArgument.player())
                                .then(Commands.argument("rank", IntegerArgumentType.integer(0, 9))
                                        .executes(context -> setRank(
                                                context,
                                                EntityArgument.getPlayer(context, "player"),
                                                IntegerArgumentType.getInteger(context, "rank")
                                        ))
                                )
                        )
                )

                // 添加職業經驗
                .then(Commands.literal("addexp")
                        .then(Commands.argument("player", EntityArgument.player())
                                .then(Commands.argument("exp", IntegerArgumentType.integer(0))
                                        .executes(context -> addExp(
                                                context,
                                                EntityArgument.getPlayer(context, "player"),
                                                IntegerArgumentType.getInteger(context, "exp")
                                        ))
                                )
                        )
                )

                // 清除心魔
                .then(Commands.literal("cleardemon")
                        .then(Commands.argument("player", EntityArgument.player())
                                .executes(context -> clearHeartDemon(
                                        context,
                                        EntityArgument.getPlayer(context, "player")
                                ))
                        )
                        .executes(context -> clearHeartDemon(context, context.getSource().getPlayerOrException()))
                )

                // 重置職業數據
                .then(Commands.literal("reset")
                        .then(Commands.argument("player", EntityArgument.player())
                                .executes(context -> resetData(
                                        context,
                                        EntityArgument.getPlayer(context, "player")
                                ))
                        )
                )
        );
    }

    private static int showInfo(CommandContext<CommandSourceStack> context, ServerPlayer player) {
        player.getCapability(ModCapabilities.PROFESSION_HARVEST_CAP).ifPresent(cap -> {
            int rank = cap.getRank();
            int exp = cap.getExp();
            int required = cap.getRequiredExp();

            context.getSource().sendSuccess(() -> Component.literal("§6=== " + player.getName().getString() + " 的採集職業信息 ==="), false);
            context.getSource().sendSuccess(() -> Component.literal("§e品級: §f" + (rank == 0 ? "未入門" : rank + "品")), false);
            context.getSource().sendSuccess(() -> Component.literal("§e經驗: §f" + exp + " / " + required), false);
        });
        return 1;
    }

    private static int setRank(CommandContext<CommandSourceStack> context, ServerPlayer player, int rank) {
        player.getCapability(ModCapabilities.PROFESSION_HARVEST_CAP).ifPresent(cap -> {
            cap.setRank(rank);
            context.getSource().sendSuccess(() -> Component.literal(
                    "§a已將 " + player.getName().getString() + " 的職業品級設置為 " + rank + "品"
            ), true);
        });
        return 1;
    }

    private static int addExp(CommandContext<CommandSourceStack> context, ServerPlayer player, int exp) {
        player.getCapability(ModCapabilities.PROFESSION_HARVEST_CAP).ifPresent(cap -> {
            cap.addExp(exp);
            context.getSource().sendSuccess(() -> Component.literal(
                    "§a已為 " + player.getName().getString() + " 添加 " + exp + " 點職業經驗"
            ), true);
            player.sendSystemMessage(Component.literal("§b獲得 " + exp + " 點職業經驗"));
        });
        return 1;
    }

    private static int clearHeartDemon(CommandContext<CommandSourceStack> context, ServerPlayer player) {
        player.getCapability(ModCapabilities.PROFESSION_HEART_DEMON_CAP).ifPresent(cap -> {
            cap.setHeartDemon(false);
            context.getSource().sendSuccess(() -> Component.literal(
                    "§a已清除 " + player.getName().getString() + " 的心魔狀態"
            ), true);
            player.sendSystemMessage(Component.literal("§a你的心魔已被清除!"));
        });
        return 1;
    }

    private static int resetData(CommandContext<CommandSourceStack> context, ServerPlayer player) {
        player.getCapability(ModCapabilities.PROFESSION_HARVEST_CAP).ifPresent(cap -> {
            cap.setRank(0);
            cap.setExp(0);
            context.getSource().sendSuccess(() -> Component.literal(
                    "§a已重置 " + player.getName().getString() + " 的採集職業數據"
            ), true);
        });
        return 1;
    }
}
