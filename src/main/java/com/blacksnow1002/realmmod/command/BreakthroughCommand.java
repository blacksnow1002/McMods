package com.blacksnow1002.realmmod.command;

import com.blacksnow1002.realmmod.capability.ModCapabilities;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;

public class BreakthroughCommand {

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(
                Commands.literal("breakthrough")
                        .requires(source -> source.hasPermission(0)) // 玩家可用，無需 OP
                        .executes(context -> {
                            CommandSourceStack source = context.getSource();
                            if (source.getEntity() instanceof Player player) {
                                player.getCapability(ModCapabilities.CULTIVATION_CAP).ifPresent(data -> {
                                    data.tryBreakthrough(player);
                                });
                                return Command.SINGLE_SUCCESS;
                            } else {
                                source.sendFailure(Component.literal("這個指令只能由玩家使用！"));
                                return 0;
                            }
                        })
        );
    }
}
