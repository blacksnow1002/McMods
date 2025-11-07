package com.blacksnow1002.realmmod.profession.alchemy.network.C2S;

import com.blacksnow1002.realmmod.RealmMod;
import com.blacksnow1002.realmmod.profession.alchemy.AlchemyLogicHandler;
import com.blacksnow1002.realmmod.profession.alchemy.screen.AlchemyFurnaceMenu;
import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.event.network.CustomPayloadEvent;
import net.minecraftforge.items.ItemStackHandler;

import java.util.ArrayList;
import java.util.List;

public record StartAlchemyPacket(BlockPos pos, List<ItemStack> items) implements CustomPacketPayload {
    public static final Type<StartAlchemyPacket> TYPE =
            new Type<>(ResourceLocation.fromNamespaceAndPath(RealmMod.MOD_ID, "start_alchemy"));

    public StartAlchemyPacket(RegistryFriendlyByteBuf buf) {
        this(
                buf.readBlockPos(),
                readItemList(buf)
        );
    }

    private static List<ItemStack> readItemList(RegistryFriendlyByteBuf buf) {
        int size = buf.readVarInt();
        List<ItemStack> items = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            boolean hasItem = buf.readBoolean();  // 新增
            if (hasItem) {  // 新增
                items.add(ItemStack.STREAM_CODEC.decode(buf));
            } else {  // 新增
                items.add(ItemStack.EMPTY);
            }
        }
        return items;
    }

    public void encode(RegistryFriendlyByteBuf buf) {
        buf.writeBlockPos(pos);
        buf.writeVarInt(items.size());
        for (ItemStack item : items) {
            buf.writeBoolean(!item.isEmpty());  // 新增
            if (!item.isEmpty()) {  // 新增
                ItemStack.STREAM_CODEC.encode(buf, item);
            }
        }
    }

    @Override
    public Type<StartAlchemyPacket> type() {
        return TYPE;
    }

    public void handle(CustomPayloadEvent.Context context) {
        context.enqueueWork(() -> {
            ServerPlayer player = context.getSender();
            if (player != null && player.containerMenu instanceof AlchemyFurnaceMenu menu) {
                BlockEntity blockEntity = menu.getBlockEntity();
                ItemStackHandler itemHandler = menu.getItemHandler();
                AlchemyLogicHandler.startAlchemy(player, blockEntity,
                        items.get(0), items.get(2), items.get(3), items.get(4), items.get(5),
                        itemHandler
                );
                player.closeContainer();
            }
        });
        context.setPacketHandled(true);
    }
}
