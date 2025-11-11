package com.blacksnow1002.realmmod.player.age.data;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtIo;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.storage.LevelResource;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.UUID;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

public class OfflinePlayerCapabilityManager {
    public static CompoundTag getOffLinePlayerCapability(MinecraftServer server, UUID uuid, ResourceLocation capabilityKey) {
        try {
            Path playerDataFolder = server.getWorldPath(LevelResource.PLAYER_DATA_DIR);
            Path playerFile = playerDataFolder.resolve(uuid.toString() + ".dat");

            if (!Files.exists(playerFile)) { return null; }

            CompoundTag playerTag;
            try (FileInputStream fis = new FileInputStream(playerFile.toFile());
                 GZIPInputStream gzis = new GZIPInputStream(fis);
                 DataInputStream dis = new DataInputStream(gzis)) {
                playerTag = NbtIo.read(dis);
            }

            if (playerTag.contains("ForgeCaps", Tag.TAG_COMPOUND)) {
                CompoundTag forgeCaps = playerTag.getCompound("ForgeCaps");
                String capKey = capabilityKey.toString();

                if (forgeCaps.contains(capKey, Tag.TAG_COMPOUND)) {
                    return forgeCaps.getCompound(capKey);
                }
            }
            return null;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    };

    public static boolean setOffLinePlayerCapability(MinecraftServer server, UUID uuid, ResourceLocation capabilityKey, CompoundTag data) {
        try {
            Path playerDataFolder = server.getWorldPath(LevelResource.PLAYER_DATA_DIR);
            Path playerFile = playerDataFolder.resolve(uuid.toString() + ".dat");

            if (!Files.exists(playerFile)) { return false; }
            CompoundTag playerTag;
            try (FileInputStream fis = new FileInputStream(playerFile.toFile());
                 GZIPInputStream gzis = new GZIPInputStream(fis);
                 DataInputStream dis = new DataInputStream(gzis)) {
                playerTag = NbtIo.read(dis);
            }
            // 步驟 2：只修改 ForgeCaps 中的特定 Capability
            if (!playerTag.contains("ForgeCaps", Tag.TAG_COMPOUND)) {
                playerTag.put("ForgeCaps", new CompoundTag());
            }
            CompoundTag forgeCaps = playerTag.getCompound("ForgeCaps");
            forgeCaps.put(capabilityKey.toString(), data);

            // 步驟 3：把完整數據（包含修改）寫回
            Path tempFile = playerDataFolder.resolve(uuid.toString() + ".dat.tmp");
            try (FileOutputStream fos = new FileOutputStream(tempFile.toFile());
                 GZIPOutputStream gzos = new GZIPOutputStream(fos);
                 DataOutputStream dos = new DataOutputStream(gzos)) {
                NbtIo.write(playerTag, dos);
            }

            // 安全替換檔案
            Path backupFile = playerDataFolder.resolve(uuid.toString() + ".dat_old");
            Files.deleteIfExists(backupFile);
            Files.move(playerFile, backupFile);
            Files.move(tempFile, playerFile);

            return true;

        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    };

    // 在 OfflinePlayerCapabilityManager 中添加
    public static boolean modifyOffLinePlayerCapability(
            MinecraftServer server,
            UUID uuid,
            ResourceLocation capabilityKey,
            java.util.function.Consumer<CompoundTag> modifier
    ) {
        try {
            Path playerDataFolder = server.getWorldPath(LevelResource.PLAYER_DATA_DIR);
            Path playerFile = playerDataFolder.resolve(uuid.toString() + ".dat");

            if (!Files.exists(playerFile)) {
                return false;
            }

            // 讀取
            CompoundTag playerTag;
            try (FileInputStream fis = new FileInputStream(playerFile.toFile());
                 GZIPInputStream gzis = new GZIPInputStream(fis);
                 DataInputStream dis = new DataInputStream(gzis)) {
                playerTag = NbtIo.read(dis);
            }

            // 修改
            if (!playerTag.contains("ForgeCaps", Tag.TAG_COMPOUND)) {
                playerTag.put("ForgeCaps", new CompoundTag());
            }
            CompoundTag forgeCaps = playerTag.getCompound("ForgeCaps");
            String capKey = capabilityKey.toString();

            if (!forgeCaps.contains(capKey, Tag.TAG_COMPOUND)) {
                forgeCaps.put(capKey, new CompoundTag());
            }
            CompoundTag capData = forgeCaps.getCompound(capKey);

            // 執行修改邏輯
            modifier.accept(capData);

            // 寫回
            Path tempFile = playerDataFolder.resolve(uuid.toString() + ".dat.tmp");
            try (FileOutputStream fos = new FileOutputStream(tempFile.toFile());
                 GZIPOutputStream gzos = new GZIPOutputStream(fos);
                 DataOutputStream dos = new DataOutputStream(gzos)) {
                NbtIo.write(playerTag, dos);
            }

            Path backupFile = playerDataFolder.resolve(uuid.toString() + ".dat_old");
            Files.deleteIfExists(backupFile);
            Files.move(playerFile, backupFile);
            Files.move(tempFile, playerFile);

            return true;

        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }
}
