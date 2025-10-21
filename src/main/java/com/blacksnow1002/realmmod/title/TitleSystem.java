package com.blacksnow1002.realmmod.title;

import com.blacksnow1002.realmmod.capability.ModCapabilities;
import com.blacksnow1002.realmmod.network.ModMessages;
import com.blacksnow1002.realmmod.network.packets.S2C.TitleSyncPacket;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class TitleSystem {

    private static TitleSystem INSTANCE;

    private final Map<String, BaseTitle>  titles = new HashMap<>();

    private TitleSystem() {
    }

    public static TitleSystem getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new TitleSystem();
        }
        return INSTANCE;
    }

    public ITitleDataManager getTitleData(ServerPlayer player) {
        return player.getCapability(ModCapabilities.TITLE_CAP)
                .orElseThrow(() -> new IllegalStateException("Title data manager has not been initialized!"));

    }

    // ==================== 稱號註冊 =====================

    public void registerTitle(BaseTitle title) {
        titles.put(title.getTitleId(), title);
    }

    public BaseTitle getTitle(String titleId) {
        return titles.get(titleId);
    }

    public Collection<BaseTitle> getAllTitles() {
        return titles.values();
    }

    // ==================== 稱號解鎖 =====================

    public  boolean canUnlock(ServerPlayer player, String titleId) {
        BaseTitle title = getTitle(titleId);
        if (title == null) return false;

        ITitleDataManager dataManager = getTitleData(player);

        if (dataManager.hasTitle(title.getTitleId())) {
            player.sendSystemMessage(Component.literal("已擁有此稱號"));
            return false;
        }

        return true;
    }

    public boolean unlock(ServerPlayer player, String titleId) {
        if (!canUnlock(player, titleId)) {
            return false;
        }

        BaseTitle title = getTitle(titleId);
        ITitleDataManager dataManager = getTitleData(player);

        dataManager.unlockTitle(title.getTitleId());

        player.sendSystemMessage(Component.literal("§a成功解鎖稱號: " + titles.get(titleId).getDisplayName()));
        return true;
    }

    // ==================== 稱號裝備 =====================

    public boolean canEquip(ServerPlayer player, String titleId) {
        BaseTitle title = titles.get(titleId);
        if (title == null) return false;

        ITitleDataManager dataManager = getTitleData(player);
        if (!dataManager.hasTitle(title.getTitleId())) {
            player.sendSystemMessage(Component.literal("尚未擁有此稱號"));
            return false;
        }

        if (dataManager.getEquipTitle().equals(titleId)) {
            player.sendSystemMessage(Component.literal("已配戴此稱號"));
            return false;
        }

        return true;
    }

    public boolean equip(ServerPlayer player, String titleId) {
        if (!canEquip(player, titleId)) return false;

        BaseTitle title = titles.get(titleId);
        ITitleDataManager dataManager = getTitleData(player);
        dataManager.equipTitle(titleId);

        player.sendSystemMessage(Component.literal("§a配戴稱號: " + title.getDisplayName()));
        broadcastTitle(player, titleId);
        return true;
    }

    public void unequip(ServerPlayer player) {
        ITitleDataManager dataManager = getTitleData(player);
        dataManager.unequipTitle();

        broadcastTitle(player, "");
    }

    // ==================== 稱號渲染 =====================

    // 同步單個玩家的稱號給指定客戶端
    public void syncEquippedTitleTo(ServerPlayer player, ServerPlayer target) {
        ITitleDataManager dataManager = getTitleData(player);
        String equippedTitle = dataManager.getEquipTitle();
        ModMessages.sendToPlayer(new TitleSyncPacket(player.getUUID(), equippedTitle), target);
    }

    // 同步玩家稱號給自己
    public void syncEquippedTitle(ServerPlayer player) {
        ITitleDataManager dataManager = getTitleData(player);
        String equippedTitle = dataManager.getEquipTitle();
        ModMessages.sendToPlayer(new TitleSyncPacket(player.getUUID(), equippedTitle), player);
    }

    public void broadcastTitle(ServerPlayer player, String titleId) {
        TitleSyncPacket packet = new TitleSyncPacket(player.getUUID(), titleId);

        ModMessages.sendToPlayersTrackingEntityAndSelf(packet, player);

    }

    public void syncAllVisibleTitles(ServerPlayer loginPlayer) {
        syncEquippedTitle(loginPlayer);

        for (ServerPlayer otherPlayer : loginPlayer.serverLevel().players()) {
            if (!otherPlayer.equals(loginPlayer)) {
                if (loginPlayer.distanceToSqr(otherPlayer) <= 64 * 64) {
                    syncEquippedTitleTo(otherPlayer, loginPlayer);
                }
            }

        }
    }
}
