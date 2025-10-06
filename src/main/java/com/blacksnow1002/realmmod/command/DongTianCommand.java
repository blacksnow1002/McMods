package com.blacksnow1002.realmmod.command;

import com.blacksnow1002.realmmod.capability.CultivationRealm;
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
            source.sendFailure(Component.literal("§c此指令只能由玩家執行！"));
            return 0;
        }

        var capOptional = player.getCapability(ModCapabilities.CULTIVATION_CAP).resolve();
        if (capOptional.isEmpty()) {
            player.sendSystemMessage(Component.literal("§c修為數據讀取失敗"));
            return 0;
        }

        var cap = capOptional.get();
        if (cap.getRealm().ordinal() < REQUIRED_REALM.ordinal()) {
            player.sendSystemMessage(Component.literal("§c境界不足，需要達到 " + REQUIRED_REALM.getDisplayName() + " 才能進入洞天"));
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
            source.sendFailure(Component.literal("§c此指令只能由玩家執行！"));
            return 0;
        }

        // 執行離開邏輯
        boolean success = DongTianManager.exitDongTian(player);
        return success ? 1 : 0;
    }

    /**
     * /dongTian unlock - 解鎖洞天（管理員指令）
     */
    private static int executeUnlock(CommandContext<CommandSourceStack> context) {
        CommandSourceStack source = context.getSource();

        // 檢查是否為玩家
        if (!(source.getEntity() instanceof ServerPlayer player)) {
            source.sendFailure(Component.literal("§c此指令只能由玩家執行！"));
            return 0;
        }

        var capOptional = player.getCapability(ModCapabilities.CULTIVATION_CAP).resolve();
        if (capOptional.isEmpty()) {
            player.sendSystemMessage(Component.literal("§c修為數據讀取失敗"));
            return 0;
        }

        var cap = capOptional.get();
        if (cap.getRealm().ordinal() < REQUIRED_REALM.ordinal()) {
            player.sendSystemMessage(Component.literal("§c境界不足，需要達到 " + REQUIRED_REALM.getDisplayName() + " 才能解鎖洞天"));
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

        source.sendSuccess(() -> Component.literal("§6========== 洞天指令幫助 =========="), false);
        source.sendSuccess(() -> Component.literal("§e/dongTian enter §7- 進入你的洞天福地"), false);
        source.sendSuccess(() -> Component.literal("§e/dongTian exit §7- 離開洞天，返回主世界"), false);
        source.sendSuccess(() -> Component.literal("§e/dongTian unlock §7- §c[管理員] §7解鎖洞天功能"), false);
        source.sendSuccess(() -> Component.literal("§e/dongTian help §7- 顯示此幫助信息"), false);
        source.sendSuccess(() -> Component.literal("§6================================"), false);

        return 1;
    }
}