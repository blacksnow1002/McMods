package com.blacksnow1002.realmmod.command;

import com.blacksnow1002.realmmod.title.BaseTitle;
import com.blacksnow1002.realmmod.title.ITitleDataManager;
import com.blacksnow1002.realmmod.title.TitleSystem;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;

import java.util.Collection;

public class TitleCommand {

    /**
     * 註冊稱號指令
     */
    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(
                Commands.literal("title")
                        // /title list - 列出所有稱號
                        .then(Commands.literal("list")
                                .executes(TitleCommand::listAllTitles)
                        )
                        // /title my - 查看自己的稱號
                        .then(Commands.literal("my")
                                .executes(TitleCommand::listMyTitles)
                        )
                        // /title equip <稱號ID> - 裝備稱號
                        .then(Commands.literal("equip")
                                .then(Commands.argument("titleId", StringArgumentType.string())
                                        .suggests(OWNED_TITLE_SUGGESTIONS)
                                        .executes(TitleCommand::equipTitle)
                                )
                        )
                        // /title unequip - 卸下稱號
                        .then(Commands.literal("unequip")
                                .executes(TitleCommand::unequipTitle)
                        )
                        // /title give <玩家> <稱號ID> - 給予玩家稱號 (管理員)
                        .then(Commands.literal("give")
                                .requires(source -> source.hasPermission(2)) // OP等級2
                                .then(Commands.argument("player", EntityArgument.player())
                                        .then(Commands.argument("titleId", StringArgumentType.string())
                                                .suggests(ALL_TITLE_SUGGESTIONS)
                                                .executes(TitleCommand::giveTitle)
                                        )
                                )
                        )
                        // /title check <玩家> - 查看玩家稱號 (管理員)
                        .then(Commands.literal("check")
                                .requires(source -> source.hasPermission(2))
                                .then(Commands.argument("player", EntityArgument.player())
                                        .executes(TitleCommand::checkPlayerTitles)
                                )
                        )
        );
    }

    // ==================== 自動補全建議 ====================

    /**
     * 建議玩家已擁有的稱號
     */
    private static final SuggestionProvider<CommandSourceStack> OWNED_TITLE_SUGGESTIONS = (context, builder) -> {
        if (context.getSource().getEntity() instanceof ServerPlayer player) {
            ITitleDataManager dataManager = TitleSystem.getInstance().getTitleData(player);
            for (String titleId : dataManager.getHaveTitles()) {
                builder.suggest(titleId);
            }
        }
        return builder.buildFuture();
    };

    /**
     * 建議所有已註冊的稱號
     */
    private static final SuggestionProvider<CommandSourceStack> ALL_TITLE_SUGGESTIONS = (context, builder) -> {
        Collection<BaseTitle> titles = TitleSystem.getInstance().getAllTitles();
        for (BaseTitle title : titles) {
            builder.suggest(title.getTitleId());
        }
        return builder.buildFuture();
    };

    // ==================== 指令實作 ====================

    /**
     * /title list - 列出所有已註冊的稱號
     */
    private static int listAllTitles(CommandContext<CommandSourceStack> context) {
        CommandSourceStack source = context.getSource();
        Collection<BaseTitle> titles = TitleSystem.getInstance().getAllTitles();

        if (titles.isEmpty()) {
            source.sendSuccess(() -> Component.literal("§c目前沒有任何稱號"), false);
            return 0;
        }

        source.sendSuccess(() -> Component.literal("§6========== 所有稱號 =========="), false);
        for (BaseTitle title : titles) {
            source.sendSuccess(() -> Component.literal(
                    "§e" + title.getTitleId() + " §7- §f" + title.getDisplayName()
            ), false);
        }
        source.sendSuccess(() -> Component.literal("§6總計: §f" + titles.size() + " §6個稱號"), false);

        return titles.size();
    }

    /**
     * /title my - 查看自己擁有的稱號
     */
    private static int listMyTitles(CommandContext<CommandSourceStack> context) {
        CommandSourceStack source = context.getSource();

        if (!(source.getEntity() instanceof ServerPlayer player)) {
            source.sendFailure(Component.literal("§c此指令只能由玩家執行"));
            return 0;
        }

        ITitleDataManager dataManager = TitleSystem.getInstance().getTitleData(player);
        String equippedTitle = dataManager.getEquipTitle();

        source.sendSuccess(() -> Component.literal("§6========== 我的稱號 =========="), false);

        if (dataManager.getHaveTitles().isEmpty()) {
            source.sendSuccess(() -> Component.literal("§7你還沒有任何稱號"), false);
            return 0;
        }

        for (String titleId : dataManager.getHaveTitles()) {
            BaseTitle title = TitleSystem.getInstance().getTitle(titleId);
            if (title != null) {
                boolean isEquipped = titleId.equals(equippedTitle);
                String prefix = isEquipped ? "§a✔ " : "§7• ";
                String suffix = isEquipped ? " §a(已裝備)" : "";

                source.sendSuccess(() -> Component.literal(
                        prefix + title.getDisplayName() + " §8[" + titleId + "]" + suffix
                ), false);
            }
        }

        source.sendSuccess(() -> Component.literal("§6總計: §f" + dataManager.getHaveTitles().size() + " §6個稱號"), false);
        return dataManager.getHaveTitles().size();
    }

    /**
     * /title equip <稱號ID> - 裝備稱號
     */
    private static int equipTitle(CommandContext<CommandSourceStack> context) {
        CommandSourceStack source = context.getSource();

        if (!(source.getEntity() instanceof ServerPlayer player)) {
            source.sendFailure(Component.literal("§c此指令只能由玩家執行"));
            return 0;
        }

        String titleId = StringArgumentType.getString(context, "titleId");
        TitleSystem titleSystem = TitleSystem.getInstance();

        if (titleSystem.equip(player, titleId)) {
            return 1;
        }

        return 0;
    }

    /**
     * /title unequip - 卸下稱號
     */
    private static int unequipTitle(CommandContext<CommandSourceStack> context) {
        CommandSourceStack source = context.getSource();

        if (!(source.getEntity() instanceof ServerPlayer player)) {
            source.sendFailure(Component.literal("§c此指令只能由玩家執行"));
            return 0;
        }

        ITitleDataManager dataManager = TitleSystem.getInstance().getTitleData(player);

        if (dataManager.getEquipTitle().isEmpty()) {
            source.sendFailure(Component.literal("§c你目前沒有裝備任何稱號"));
            return 0;
        }

        TitleSystem.getInstance().unequip(player);
        source.sendSuccess(() -> Component.literal("§a已卸下稱號"), false);
        return 1;
    }

    /**
     * /title give <玩家> <稱號ID> - 給予玩家稱號 (管理員)
     */
    private static int giveTitle(CommandContext<CommandSourceStack> context) {
        CommandSourceStack source = context.getSource();

        try {
            ServerPlayer targetPlayer = EntityArgument.getPlayer(context, "player");
            String titleId = StringArgumentType.getString(context, "titleId");
            TitleSystem titleSystem = TitleSystem.getInstance();

            BaseTitle title = titleSystem.getTitle(titleId);
            if (title == null) {
                source.sendFailure(Component.literal("§c稱號不存在: " + titleId));
                return 0;
            }

            if (titleSystem.unlock(targetPlayer, titleId)) {
                source.sendSuccess(() -> Component.literal(
                        "§a已給予 " + targetPlayer.getName().getString() +
                                " 稱號: " + title.getDisplayName()
                ), true);
                return 1;
            }

            return 0;

        } catch (Exception e) {
            source.sendFailure(Component.literal("§c執行指令時發生錯誤: " + e.getMessage()));
            return 0;
        }
    }

    /**
     * /title check <玩家> - 查看玩家稱號 (管理員)
     */
    private static int checkPlayerTitles(CommandContext<CommandSourceStack> context) {
        CommandSourceStack source = context.getSource();

        try {
            ServerPlayer targetPlayer = EntityArgument.getPlayer(context, "player");
            ITitleDataManager dataManager = TitleSystem.getInstance().getTitleData(targetPlayer);
            String equippedTitle = dataManager.getEquipTitle();

            source.sendSuccess(() -> Component.literal(
                    "§6========== " + targetPlayer.getName().getString() + " 的稱號 =========="
            ), false);

            if (dataManager.getHaveTitles().isEmpty()) {
                source.sendSuccess(() -> Component.literal("§7該玩家還沒有任何稱號"), false);
                return 0;
            }

            for (String titleId : dataManager.getHaveTitles()) {
                BaseTitle title = TitleSystem.getInstance().getTitle(titleId);
                if (title != null) {
                    boolean isEquipped = titleId.equals(equippedTitle);
                    String prefix = isEquipped ? "§a✔ " : "§7• ";
                    String suffix = isEquipped ? " §a(已裝備)" : "";

                    source.sendSuccess(() -> Component.literal(
                            prefix + title.getDisplayName() + " §8[" + titleId + "]" + suffix
                    ), false);
                }
            }

            source.sendSuccess(() -> Component.literal(
                    "§6總計: §f" + dataManager.getHaveTitles().size() + " §6個稱號"
            ), false);

            return dataManager.getHaveTitles().size();

        } catch (Exception e) {
            source.sendFailure(Component.literal("§c執行指令時發生錯誤: " + e.getMessage()));
            return 0;
        }
    }
}