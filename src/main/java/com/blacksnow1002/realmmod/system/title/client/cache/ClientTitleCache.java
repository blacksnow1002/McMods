package com.blacksnow1002.realmmod.system.title.client.cache;

import net.minecraft.resources.ResourceLocation;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * 客戶端稱號快取
 * 使用 UUID 來區分不同玩家的稱號
 */
public class ClientTitleCache {

    // 使用 Map 存儲每個玩家的稱號 ID
    private static final Map<UUID, String> playerTitles = new HashMap<>();

    // 使用 Map 存儲每個玩家的材質路徑（快取）
    private static final Map<UUID, String> playerTexturePaths = new HashMap<>();

    private static final Map<UUID, ResourceLocation> textureCache = new HashMap<>();

    // 設置指定玩家的稱號
    public static void setTitleId(UUID playerUUID, String titleId) {
        if (titleId == null || titleId.isEmpty()) {
            // 如果稱號為空，移除該玩家的稱號
            playerTitles.remove(playerUUID);
            playerTexturePaths.remove(playerUUID);
        } else {
            // 存儲稱號 ID
            playerTitles.put(playerUUID, titleId);

            // 生成並快取材質路徑
            String texturePath = "realmmod:textures/title/" + titleId + ".png";
            playerTexturePaths.put(playerUUID, texturePath);
        }
    }


    // 獲取指定玩家的材質路徑
    public static String getTexturePath(UUID playerUUID) {
        return playerTexturePaths.get(playerUUID);
    }

    public static ResourceLocation getTexture(UUID playerUUID) {
        return textureCache.computeIfAbsent(playerUUID, uuid -> {
            String path = playerTexturePaths.get(uuid);
            return path != null ? ResourceLocation.parse(path) : null;
        });
    }
}