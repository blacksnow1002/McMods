package com.blacksnow1002.realmmod.assignment;

import com.blacksnow1002.realmmod.assignment.BaseAssignment;
import com.blacksnow1002.realmmod.assignment.AssignmentSystem;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;

import java.util.Set;

/**
 * 任務系統命令
 */
public class AssignmentCommands {

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("assignment")
                .requires(source -> source.hasPermission(2))

                // /assignment accept <assignmentId> - 接取任務
                .then(Commands.literal("accept")
                        .then(Commands.argument("assignmentId", StringArgumentType.string())
                                .executes(context -> acceptAssignment(context))
                        )
                )

                // /assignment complete <assignmentId> - 完成任務
                .then(Commands.literal("complete")
                        .then(Commands.argument("assignmentId", StringArgumentType.string())
                                .executes(context -> completeAssignment(context))
                        )
                )

                // /assignment list - 列出所有任務
                .then(Commands.literal("list")
                        .executes(context -> listAssignments(context))
                )

                // /assignment progress - 查看已接取的任務進度
                .then(Commands.literal("progress")
                        .executes(context -> showProgress(context))
                )

                // /assignment forceComplete <assignmentId> - 強制完成任務（管理員用）
                .then(Commands.literal("forceComplete")
                        .requires(source -> source.hasPermission(4))
                        .then(Commands.argument("assignmentId", StringArgumentType.string())
                                .executes(context -> forceCompleteAssignment(context))
                        )
                )
        );
    }

    /**
     * 接取任務
     */
    private static int acceptAssignment(CommandContext<CommandSourceStack> context) {
        ServerPlayer player = context.getSource().getPlayer();
        if (player == null) return 0;

        String assignmentId = StringArgumentType.getString(context, "assignmentId");
        AssignmentSystem assignmentSystem = AssignmentSystem.getInstance();

        if (assignmentSystem.acceptAssignment(player, assignmentId)) {
            return 1;
        } else {
            player.sendSystemMessage(Component.literal("§c無法接取任務：" + assignmentId));
            return 0;
        }
    }

    /**
     * 完成任務
     */
    private static int completeAssignment(CommandContext<CommandSourceStack> context) {
        ServerPlayer player = context.getSource().getPlayer();
        if (player == null) return 0;

        String assignmentId = StringArgumentType.getString(context, "assignmentId");
        AssignmentSystem assignmentSystem = AssignmentSystem.getInstance();

        if (assignmentSystem.completeAssignment(player, assignmentId)) {
            return 1;
        } else {
            player.sendSystemMessage(Component.literal("§c無法完成任務：任務未達成或未接取"));
            return 0;
        }
    }

    /**
     * 列出所有任務
     */
    private static int listAssignments(CommandContext<CommandSourceStack> context) {
        ServerPlayer player = context.getSource().getPlayer();
        if (player == null) return 0;

        AssignmentSystem assignmentSystem = AssignmentSystem.getInstance();
        player.sendSystemMessage(Component.literal("§6=== 所有任務 ==="));

        for (BaseAssignment assignment : assignmentSystem.getAllAssignments()) {
            boolean accepted = assignmentSystem.isAssignmentAccepted(player, assignment.getId());
            boolean completed = assignmentSystem.isAssignmentCompleted(player, assignment.getId());

            String status = completed ? "§a[已完成]" : (accepted ? "§e[進行中]" : "§7[未接取]");
            player.sendSystemMessage(Component.literal(
                    status + " §f" + assignment.getDisplayName() + " §7(" + assignment.getId() + ")"
            ));
        }

        return 1;
    }

    /**
     * 顯示任務進度
     */
    private static int showProgress(CommandContext<CommandSourceStack> context) {
        ServerPlayer player = context.getSource().getPlayer();
        if (player == null) return 0;

        AssignmentSystem assignmentSystem = AssignmentSystem.getInstance();
        Set<String> acceptedAssignments = assignmentSystem.getAcceptedAssignments(player);

        if (acceptedAssignments.isEmpty()) {
            player.sendSystemMessage(Component.literal("§7你目前沒有進行中的任務"));
            return 0;
        }

        player.sendSystemMessage(Component.literal("§6=== 任務進度 ==="));

        for (String assignmentId : acceptedAssignments) {
            BaseAssignment assignment = assignmentSystem.getAssignment(assignmentId);
            if (assignment == null) continue;

            player.sendSystemMessage(Component.literal("§e" + assignment.getDisplayName()));

            assignment.getObjectives().forEach(objective -> {
                int progress = assignmentSystem.getObjectiveProgress(player, assignmentId, objective.getId());
                int required = objective.getRequiredCount();
                String status = progress >= required ? "§a✓" : "§7○";

                player.sendSystemMessage(Component.literal(
                        "  " + status + " " + objective.getDescription() +
                                " §7(" + progress + "/" + required + ")"
                ));
            });
        }

        return 1;
    }

    /**
     * 強制完成任務（管理員用）
     */
    private static int forceCompleteAssignment(CommandContext<CommandSourceStack> context) {
        ServerPlayer player = context.getSource().getPlayer();
        if (player == null) return 0;

        String assignmentId = StringArgumentType.getString(context, "assignmentId");
        AssignmentSystem assignmentSystem = AssignmentSystem.getInstance();

        BaseAssignment assignment = assignmentSystem.getAssignment(assignmentId);
        if (assignment == null) {
            player.sendSystemMessage(Component.literal("§c任務不存在：" + assignmentId));
            return 0;
        }

        // 接取任務（如果未接取）
        if (!assignmentSystem.isAssignmentAccepted(player, assignmentId)) {
            assignmentSystem.acceptAssignment(player, assignmentId);
        }

        // 完成所有目標
        assignment.getObjectives().forEach(objective -> {
            assignmentSystem.updateObjectiveProgress(
                    player, assignmentId, objective.getId(), objective.getRequiredCount()
            );
        });

        // 完成任務
        assignmentSystem.completeAssignment(player, assignmentId);

        player.sendSystemMessage(Component.literal("§a已強制完成任務：" + assignment.getDisplayName()));
        return 1;
    }
}