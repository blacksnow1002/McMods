package com.blacksnow1002.realmmod.system.profession.alchemy;

import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

import java.util.UUID;


public class AlchemyTask {
    public final BlockPos pos;
    public final UUID playerUUID;
    public final ResourceKey<Level> dimension;
    public final long endTime;
    public final ItemStack outputPill;
    public final String returnText;

    public AlchemyTask(BlockPos pos, UUID playerUUID, ServerLevel serverLevel, long endTime, ItemStack outputPill, String returnText) {
        this.pos = pos;
        this.playerUUID = playerUUID;
        this.dimension = serverLevel.dimension();
        this.endTime = endTime;
        this.outputPill = outputPill;
        this.returnText = returnText;
    }


    public BlockPos getPos() {
        return pos;
    }

    public UUID getPlayerUUID() {
        return playerUUID;
    }

    public ResourceKey<Level> getDimension() {
        return dimension;
    }

    public long getEndTime() {
        return endTime;
    }

    public ItemStack getOutputPill() {
        return outputPill;
    }

    public boolean isFinished(long now) {
        return now >= endTime;
    }

    public String getReturnText() {
        return returnText;
    }

    public CompoundTag saveNBTData(HolderLookup.Provider provider) {
        CompoundTag tag = new CompoundTag();
        tag.putInt("posX", pos.getX());
        tag.putInt("posY", pos.getY());
        tag.putInt("posZ", pos.getZ());
        tag.putUUID("playerUUID", playerUUID);
        tag.putString("dimension", dimension.toString());
        tag.putLong("endTime", endTime);
        tag.put("outputPill", outputPill.save(provider));
        tag.putString("returnText", returnText);

        return tag;
    }

    public static AlchemyTask loadNBTData(CompoundTag tag, HolderLookup.Provider provider, MinecraftServer server) {

        String dimensionString = tag.getString("dimension");
        ResourceLocation dimensionLocation = ResourceLocation.parse(dimensionString);
        ResourceKey<Level> dimensionKey = ResourceKey.create(Registries.DIMENSION, dimensionLocation);
        ServerLevel serverLevel = server.getLevel(dimensionKey);

        if (serverLevel == null) {
            // 如果維度不存在，默認使用主世界
            serverLevel = server.overworld();
        }

        return new AlchemyTask(
                new BlockPos(tag.getInt("posX"), tag.getInt("posY"), tag.getInt("posZ")),
                tag.getUUID("playerUUID"),
                serverLevel,
                tag.getLong("endTime"),
                ItemStack.parseOptional(provider, tag.getCompound("outputPill")),
                tag.getString("returnText")
        );
    }

}

