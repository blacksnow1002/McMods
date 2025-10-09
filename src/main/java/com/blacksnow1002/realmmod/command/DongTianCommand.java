package com.blacksnow1002.realmmod.command;

import com.blacksnow1002.realmmod.capability.cultivation.CultivationRealm;
import com.blacksnow1002.realmmod.capability.ModCapabilities;
import com.blacksnow1002.realmmod.dimension.dong_tian.DongTianManager;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;

/**
 * 洞天指令系統
 * 提供 /dongTian enter, exit, unlock 指令
 */
public class DongTianCommand {

    /**
     * 註冊洞天指令
     * 在你的 mod 主類或指令註冊事件中調用
     */
    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(
                Commands.literal("dongTian")
                        .then(Commands.literal("enter")
                                .executes(DongTianCommand::executeEnter)
                        )
                        .then(Commands.literal("exit")
                                .executes(DongTianCommand::executeExit)
                        )
                        .then(Commands.literal("unlock")
                                .executes(DongTianCommand::executeUnlock)
                        )
                        .then(Commands.literal("help")
                                .executes(DongTianCommand::executeHelp)
                        )
        );
    }

    private static CultivationRealm REQUIRED_REALM = CultivationRealm.seventh;

    /**
     * /dongTian enter - 進入洞天
     */
    private static int executeEnter(CommandContext<CommandSourceStack> context) {
        CommandSourceStack source = context.getSource();

        // 檢查是否為玩家
        if (!(source.getEntity() instanceof ServerPlayer player)) {
            source.sendFailure(Component.translatable("message.realmmod.dong_tian.command.error.not_player"));
            return 0;
        }

        var capOptional = player.getCapability(ModCapabilities.CULTIVATION_CAP).resolve();
        if (capOptional.isEmpty()) {
            player.sendSystemMessage(Component.translatable("message.realmmod.dong_tian.command.error.get_realm_fail"));
            return 0;
        }

        var cap = capOptional.get();
        if (cap.getRealm().ordinal() < REQUIRED_REALM.ordinal()) {
            player.sendSystemMessage(Component.translatable(
                    "message.realmmod.dong_tian.command.error.low_ordinal",
                    REQUIRED_REALM.getDisplayName()));
            return 0;
        }

        // 執行進入邏輯
        boolean success = DongTianManager.enterDongTian(player);
        return success ? 1 : 0;
    }

    /**
     * /dongTian exit - 離開洞天
     */
    private static int executeExit(CommandContext<CommandSourceStack> context) {
        CommandSourceStack source = context.getSource();

        // 檢查是否為玩家
        if (!(source.getEntity() instanceof ServerPlayer player)) {
            source.sendFailure(Component.translatable("message.realmmod.dong_tian.command.error.not_player"));
            return 0;
        }

        // 執行離開邏輯
        boolean success = DongTianManager.exitDongTian(player);
        return success ? 1 : 0;
    }

    /**
     * /dongTian unlock - 解鎖洞天
     */
    private static int executeUnlock(CommandContext<CommandSourceStack> context) {
        CommandSourceStack source = context.getSource();

        // 檢查是否為玩家
        if (!(source.getEntity() instanceof ServerPlayer player)) {
            source.sendFailure(Component.translatable("message.realmmod.dong_tian.command.error.not_player"));
            return 0;
        }

        var capOptional = player.getCapability(ModCapabilities.CULTIVATION_CAP).resolve();
        if (capOptional.isEmpty()) {
            player.sendSystemMessage(Component.translatable("message.realmmod.dong_tian.command.error.get_realm_fail"));
            return 0;
        }

        var cap = capOptional.get();
        if (cap.getRealm().ordinal() < REQUIRED_REALM.ordinal()) {
            player.sendSystemMessage(Component.translatable(
                    "message.realmmod.dong_tian.command.error.low_ordinal",
                    REQUIRED_REALM.getDisplayName()));
            return 0;
        }

        // 執行解鎖邏輯
        DongTianManager.unlockDongTian(player);
        return 1;
    }

    /**
     * /dongTian help - 顯示幫助信息
     */
    private static int executeHelp(CommandContext<CommandSourceStack> context) {
        CommandSourceStack source = context.getSource();

        source.sendSuccess(() -> Component.translatable("message.realmmod.dong_tian.command.help.first"), false);
        source.sendSuccess(() -> Component.translatable("message.realmmod.dong_tian.command.help.second"), false);
        source.sendSuccess(() -> Component.translatable("message.realmmod.dong_tian.command.help.third"), false);
        source.sendSuccess(() -> Component.translatable("message.realmmod.dong_tian.command.help.fourth"), false);
        source.sendSuccess(() -> Component.translatable("message.realmmod.dong_tian.command.help.fifth"), false);
        source.sendSuccess(() -> Component.translatable("message.realmmod.dong_tian.command.help.sixth"), false);

        return 1;
    }
}