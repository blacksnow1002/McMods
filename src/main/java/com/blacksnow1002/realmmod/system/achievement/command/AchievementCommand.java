package com.blacksnow1002.realmmod.system.achievement.command;

import com.blacksnow1002.realmmod.system.achievement.AchievementManager;
import com.blacksnow1002.realmmod.system.achievement.CustomAchievements;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;

import java.util.List;

public class AchievementCommand {

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(
                Commands.literal("achievement")
                        .requires(source -> source.hasPermission(2))

                        // /achievement grant <player> <achievement>
                        .then(Commands.literal("grant")
                                .then(Commands.argument("player", EntityArgument.player())
                                        .then(Commands.argument("achievement", StringArgumentType.greedyString())
                                                .suggests((context, builder) -> {
                                                    // 顯示完整路徑
                                                    for (CustomAchievements achievement : CustomAchievements.values()) {
                                                        builder.suggest(achievement.getPath(),
                                                                Component.literal(achievement.getPath()));
                                                    }
                                                    return builder.buildFuture();
                                                })
                                                .executes(context -> {
                                                    ServerPlayer player = EntityArgument.getPlayer(context, "player");
                                                    String achievementId = StringArgumentType.getString(context, "achievement");
                                                    CustomAchievements achievement = CustomAchievements.getById(achievementId);

                                                    if (achievement == null) {
                                                        context.getSource().sendFailure(
                                                                Component.literal("未知的成就: " + achievementId)
                                                                        .append(Component.literal("\n可用的成就:"))
                                                        );

                                                        // 列出所有可用成就
                                                        for (CustomAchievements a : CustomAchievements.values()) {
                                                            context.getSource().sendFailure(
                                                                    Component.literal("  - " + a.getPath())
                                                            );
                                                        }
                                                        return 0;
                                                    }

                                                    if (AchievementManager.grantAchievement(player, achievement)) {
                                                        context.getSource().sendSuccess(() ->
                                                                        Component.literal("✓ 已授予 " + player.getName().getString() +
                                                                                " 成就: " + achievementId),
                                                                true
                                                        );
                                                        return 1;
                                                    } else {
                                                        // 檢查具體原因
                                                        if (AchievementManager.hasAchievement(player, achievement)) {
                                                            context.getSource().sendFailure(
                                                                    Component.literal("✗ 玩家已擁有此成就")
                                                            );
                                                        } else if (achievement.getParent() != null &&
                                                                !AchievementManager.hasAchievement(player, achievement.getParent())) {
                                                            context.getSource().sendFailure(
                                                                    Component.literal("✗ 缺少前置成就: " + achievement.getParent().getPath())
                                                            );
                                                        } else {
                                                            context.getSource().sendFailure(
                                                                    Component.literal("✗ 無法授予成就（未知錯誤）")
                                                            );
                                                        }
                                                        return 0;
                                                    }
                                                })
                                        )
                                )
                        )

                        // /achievement revoke <player> <achievement>
                        .then(Commands.literal("revoke")
                                .then(Commands.argument("player", EntityArgument.player())
                                        .then(Commands.argument("achievement", StringArgumentType.greedyString())
                                                .suggests((context, builder) -> {
                                                    for (CustomAchievements achievement : CustomAchievements.values()) {
                                                        builder.suggest(achievement.getPath());
                                                    }
                                                    return builder.buildFuture();
                                                })
                                                .executes(context -> {
                                                    ServerPlayer player = EntityArgument.getPlayer(context, "player");
                                                    String achievementId = StringArgumentType.getString(context, "achievement");
                                                    CustomAchievements achievement = CustomAchievements.getById(achievementId);

                                                    if (achievement == null) {
                                                        context.getSource().sendFailure(Component.literal("未知的成就: " + achievementId));
                                                        return 0;
                                                    }

                                                    if (AchievementManager.revokeAchievement(player, achievement)) {
                                                        context.getSource().sendSuccess(() ->
                                                                        Component.literal("✓ 已撤銷 " + player.getName().getString() +
                                                                                " 的成就: " + achievementId),
                                                                true
                                                        );
                                                        return 1;
                                                    } else {
                                                        context.getSource().sendFailure(Component.literal("✗ 玩家沒有此成就"));
                                                        return 0;
                                                    }
                                                })
                                        )
                                )
                        )

                        // /achievement check <player> <achievement>
                        .then(Commands.literal("check")
                                .then(Commands.argument("player", EntityArgument.player())
                                        .then(Commands.argument("achievement", StringArgumentType.greedyString())
                                                .suggests((context, builder) -> {
                                                    for (CustomAchievements achievement : CustomAchievements.values()) {
                                                        builder.suggest(achievement.getPath());
                                                    }
                                                    return builder.buildFuture();
                                                })
                                                .executes(context -> {
                                                    ServerPlayer player = EntityArgument.getPlayer(context, "player");
                                                    String achievementId = StringArgumentType.getString(context, "achievement");
                                                    CustomAchievements achievement = CustomAchievements.getById(achievementId);

                                                    if (achievement == null) {
                                                        context.getSource().sendFailure(Component.literal("未知的成就: " + achievementId));
                                                        return 0;
                                                    }

                                                    boolean has = AchievementManager.hasAchievement(player, achievement);
                                                    context.getSource().sendSuccess(() ->
                                                                    Component.literal(player.getName().getString() +
                                                                            (has ? " ✓ 已獲得 " : " ✗ 未獲得 ") + achievementId),
                                                            false
                                                    );
                                                    return has ? 1 : 0;
                                                })
                                        )
                                )
                        )

                        // /achievement list <player>
                        .then(Commands.literal("list")
                                .then(Commands.argument("player", EntityArgument.player())
                                        .executes(context -> {
                                            ServerPlayer player = EntityArgument.getPlayer(context, "player");
                                            List<CustomAchievements> completed = AchievementManager.getCompletedAchievements(player);

                                            context.getSource().sendSuccess(() ->
                                                            Component.literal("=== " + player.getName().getString() + " 的成就 ===\n" +
                                                                    "已完成: " + completed.size() + "/" + CustomAchievements.values().length),
                                                    false
                                            );

                                            if (!completed.isEmpty()) {
                                                context.getSource().sendSuccess(() ->
                                                                Component.literal("\n已完成的成就:"),
                                                        false
                                                );
                                                for (CustomAchievements achievement : completed) {
                                                    context.getSource().sendSuccess(() ->
                                                                    Component.literal("  ✓ " + achievement.getPath()),
                                                            false
                                                    );
                                                }
                                            }

                                            return completed.size();
                                        })
                                )
                        )

                        // /achievement grantall <player> - 授予所有成就（測試用）
                        .then(Commands.literal("grantall")
                                .then(Commands.argument("player", EntityArgument.player())
                                        .executes(context -> {
                                            ServerPlayer player = EntityArgument.getPlayer(context, "player");
                                            int count = 0;

                                            for (CustomAchievements achievement : CustomAchievements.values()) {
                                                if (AchievementManager.grantAchievement(player, achievement)) {
                                                    count++;
                                                }
                                            }

                                            int finalCount = count;
                                            context.getSource().sendSuccess(() ->
                                                            Component.literal("✓ 已授予 " + player.getName().getString() +
                                                                    " " + finalCount + " 個成就"),
                                                    true
                                            );

                                            return count;
                                        })
                                )
                        )
        );
    }
}
