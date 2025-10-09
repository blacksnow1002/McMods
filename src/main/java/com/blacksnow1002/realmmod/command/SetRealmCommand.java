package com.blacksnow1002.realmmod.command;

import com.blacksnow1002.realmmod.capability.cultivation.CultivationRealm;
import com.blacksnow1002.realmmod.capability.ModCapabilities;
import com.blacksnow1002.realmmod.network.ModMessages;
import com.blacksnow1002.realmmod.network.packets.RealmSyncPacket;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;

public class SetRealmCommand {

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("setrealm")
                .requires(source -> source.hasPermission(2)) // 需要 OP 權限
                .then(Commands.argument("player", EntityArgument.player())
                        .then(Commands.argument("realm", IntegerArgumentType.integer(1, 9))
                                .then(Commands.argument("layer", IntegerArgumentType.integer(1, 10))
                                        .executes(context -> setRealm(context,
                                                EntityArgument.getPlayer(context, "player"),
                                                IntegerArgumentType.getInteger(context, "realm"),
                                                IntegerArgumentType.getInteger(context, "layer")
                                        ))
                                )
                                .executes(context -> setRealm(context,
                                        EntityArgument.getPlayer(context, "player"),
                                        IntegerArgumentType.getInteger(context, "realm"),
                                        1 // 預設第一層
                                ))
                        )
                )
        );
    }

    private static int setRealm(CommandContext<CommandSourceStack> context, ServerPlayer player, int realmLevel, int layer) {
        var capOptional = player.getCapability(ModCapabilities.CULTIVATION_CAP);
        if (!capOptional.isPresent()) {
            context.getSource().sendFailure(Component.literal("§c無法獲取修仙能力資料!"));
            return 0;
        }

        var cap = capOptional.orElseThrow(() -> new IllegalStateException("Cultivation capability missing"));
        CultivationRealm targetRealm = CultivationRealm.values()[realmLevel - 1];

        if (layer > targetRealm.getMaxLayer()) {
            context.getSource().sendFailure(Component.literal(
                    "§c錯誤: " + targetRealm.getDisplayName() + " 最多只有 " + targetRealm.getMaxLayer() + " 層!"
            ));
            return 0;
        }

        cap.setRealm(targetRealm);
        cap.setLayer(layer);
        cap.setCultivation(0);
        cap.setBreakthroughSuccessPossibility(targetRealm.getBreakthroughSuccessPossibility());

        context.getSource().sendSuccess(() -> Component.literal(
                "§a已將 " + player.getName().getString() + " 的境界設為: §6" +
                        targetRealm.getDisplayName() + " §a第 §6" + layer + " §a層"
        ), true);

        player.sendSystemMessage(Component.literal(
                "§6你的境界已被設定為: §b" + targetRealm.getDisplayName() + " §6第 §b" + layer + " §6層"
        ));
        ModMessages.sendToPlayer(new RealmSyncPacket(targetRealm.ordinal(), layer),(ServerPlayer) player);
        return 1;
    }
}