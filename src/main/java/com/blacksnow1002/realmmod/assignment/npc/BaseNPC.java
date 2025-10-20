package com.blacksnow1002.realmmod.assignment.npc;

import com.blacksnow1002.realmmod.assignment.AssignmentSystem;
import com.blacksnow1002.realmmod.assignment.BaseAssignment;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.HoverEvent;
import net.minecraft.network.chat.Style;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.network.chat.Component;
import java.util.*;

/**
 * NPC 基礎類
 */
public abstract class BaseNPC {

    private final String npcId;
    private final String npcName;
    private final List<String> assignmentIds;

    public BaseNPC(String npcId, String npcName) {
        this.npcId = npcId;
        this.npcName = npcName;
        this.assignmentIds = new ArrayList<>();
    }

    // ==================== 基本信息 ====================

    public String getNpcId() {
        return npcId;
    }

    public String getNpcName() {
        return npcName;
    }

    public List<String> getAssignmentIds() {
        return new ArrayList<>(assignmentIds);
    }

    /**
     * 添加任務到該 NPC
     */
    protected void addAssignment(String assignmentId) {
        if (!assignmentIds.contains(assignmentId)) {
            assignmentIds.add(assignmentId);
        }
    }

    // ==================== 玩家交互 ====================

    /**
     * 玩家右鍵點擊 NPC 時觸發
     */
    public void onInteract(ServerPlayer player) {
        System.out.println("[NPC] " + this.npcName + " 被點擊");
        sendDialogue(player);
        showAvailableAssignments(player);
    }

    /**
     * 發送對話文本
     */
    public void sendDialogue(ServerPlayer player) {
        player.sendSystemMessage(Component.literal(
                "§e[" + npcName + "] " + getDialogueText()
        ));
    }

    /**
     * 顯示可用的任務列表
     */
    public void showAvailableAssignments(ServerPlayer player) {
        AssignmentSystem system = AssignmentSystem.getInstance();
        boolean hasAnyAssignment = false;

        for (String assignmentId : assignmentIds) {
            BaseAssignment assignment = system.getAssignment(assignmentId);
            if (assignment == null) continue;

            boolean accepted = system.isAssignmentAccepted(player, assignmentId);
            boolean completed = system.isAssignmentCompleted(player, assignmentId);
            boolean canAccept = system.canAccept(player, assignmentId);

            // 已完成的任務
            if (completed) {
                hasAnyAssignment = true;
                player.sendSystemMessage(Component.literal(
                        "§7  §a[已完成] §7" + assignment.getDisplayName()
                ));
            }
            // 進行中的任務
            else if (accepted) {
                hasAnyAssignment = true;
                Component message = Component.literal("§7  §b[進行中] §f" + assignment.getDisplayName());

                // 檢查是否可以交付
                if (system.canComplete(player, assignmentId)) {
                    Component completeButton = Component.literal(" §6[交付]")
                            .withStyle(Style.EMPTY
                                    .withColor(ChatFormatting.GOLD)
                                    .withClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/assignment complete " + assignmentId))
                                    .withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, Component.literal(assignment.getDescription())))
                            );
                    player.sendSystemMessage(message.copy().append(completeButton));
                } else {
                    player.sendSystemMessage(message);
                }
            }
            // 可接取的任務（前置任務已完成）
            else if (canAccept) {
                hasAnyAssignment = true;
                Component message = Component.literal("§7  §6[可接取] §f" + assignment.getDisplayName());
                Component acceptButton = Component.literal(" §a[接取]")
                        .withStyle(Style.EMPTY
                                .withColor(ChatFormatting.GREEN)
                                .withClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/assignment accept " + assignmentId))
                                .withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, Component.literal(assignment.getDescription())))
                        );
                player.sendSystemMessage(message.copy().append(acceptButton));
            }
            // 前置任務未完成（顯示為鎖定）
            else if (!completed) {
                hasAnyAssignment = true;
                List<String> uncompletedPrereqs = assignment.getUncompletedPrerequisites(
                        player,
                        system.getPlayerData(player)
                );

                if (!uncompletedPrereqs.isEmpty()) {
                    player.sendSystemMessage(Component.literal(
                            "§7  §8[已鎖定] §8" + assignment.getDisplayName()
                    ));

                    // 顯示需要完成的前置任務
                    player.sendSystemMessage(Component.literal("§7    §8需要完成："));
                    for (String prereqId : uncompletedPrereqs) {
                        BaseAssignment prereq = system.getAssignment(prereqId);
                        if (prereq != null) {
                            player.sendSystemMessage(Component.literal(
                                    "§7      §8- " + prereq.getDisplayName()
                            ));
                        }
                    }
                }
            }
        }

        if (!hasAnyAssignment) {
            player.sendSystemMessage(Component.literal("§7  目前沒有可用的任務"));
        }
    }

    /**
     * 玩家接取任務
     */
    public void acceptAssignment(ServerPlayer player, String assignmentId) {
        if (!assignmentIds.contains(assignmentId)) {
            player.sendSystemMessage(Component.literal("§c該 NPC 沒有此任務"));
            return;
        }

        AssignmentSystem system = AssignmentSystem.getInstance();
        if (system.acceptAssignment(player, assignmentId)) {
            onAssignmentAccepted(player, assignmentId);
        }
    }

    /**
     * 玩家完成任務
     */
    public void completeAssignment(ServerPlayer player, String assignmentId) {
        if (!assignmentIds.contains(assignmentId)) {
            player.sendSystemMessage(Component.literal("§c該 NPC 沒有此任務"));
            return;
        }

        AssignmentSystem system = AssignmentSystem.getInstance();
        if (system.completeAssignment(player, assignmentId)) {
            onAssignmentCompleted(player, assignmentId);
        } else {
            player.sendSystemMessage(Component.literal("§c任務尚未完成"));
        }
    }

    // ==================== 回調方法 ====================

    /**
     * 獲取 NPC 對話文本 - 子類覆寫
     */
    protected abstract String getDialogueText();

    /**
     * 玩家接取任務時觸發 - 子類可覆寫
     */
    protected void onAssignmentAccepted(ServerPlayer player, String assignmentId) {
    }

    /**
     * 玩家完成任務時觸發 - 子類可覆寫
     */
    protected void onAssignmentCompleted(ServerPlayer player, String assignmentId) {
        player.sendSystemMessage(Component.literal(
                "§e[" + npcName + "] 非常感謝你！"
        ));
    }
}