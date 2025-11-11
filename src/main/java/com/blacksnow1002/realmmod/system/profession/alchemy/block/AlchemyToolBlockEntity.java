package com.blacksnow1002.realmmod.system.profession.alchemy.block;

import com.blacksnow1002.realmmod.core.registry.block.ModBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.items.ItemStackHandler;
import org.jetbrains.annotations.Nullable;

public class AlchemyToolBlockEntity extends BlockEntity {

    private AlchemyToolBlock.Grades grade;
    private int rank;
    private int bonusEntryIndex = -1;
    private int currentDurability;

    // 添加 ItemStackHandler 用於存儲物品
    private final ItemStackHandler itemHandler = new ItemStackHandler(7) {
        @Override
        protected void onContentsChanged(int slot) {
            setChanged();
            if (level != null && !level.isClientSide) {
                level.sendBlockUpdated(getBlockPos(), getBlockState(), getBlockState(), 3);
            }
        }
    };


    public AlchemyToolBlockEntity(BlockPos pos, BlockState state, AlchemyToolBlock.Grades grade, int rank) {
        super(ModBlockEntities.ALCHEMY_TOOL_BE.get(), pos, state);
        this.grade = grade;
        this.rank = rank;
        this.currentDurability = grade.getMaxDurability();
    }

    public AlchemyToolBlock.Grades getGrade() { return grade; }
    public int getRank() { return rank; }

    /**
     * 獲取物品處理器
     */
    public ItemStackHandler getItemHandler() {
        return itemHandler;
    }

    /**
     * 獲取詞條
     */
    @Nullable
    public AlchemyToolBlock.BonusEntry getBonus() {
        if (bonusEntryIndex >= 0 && bonusEntryIndex < AlchemyToolBlock.BonusEntry.values().length) {
            return AlchemyToolBlock.BonusEntry.values()[bonusEntryIndex];
        }
        return null;
    }

    /**
     * 設置詞條（從物品 NBT 複製過來）
     */
    public void setBonus(AlchemyToolBlock.BonusEntry entry) {
        this.bonusEntryIndex = entry.ordinal();
        setChanged();
        if (level != null && !level.isClientSide) {
            level.sendBlockUpdated(getBlockPos(), getBlockState(), getBlockState(), 3);
        }
    }

    /**
     * 檢查是否有詞條
     */
    public boolean hasBonus() {
        return bonusEntryIndex >= 0 && bonusEntryIndex < AlchemyToolBlock.BonusEntry.values().length;
    }

    public int getCurrentDurability() {
        return currentDurability;
    }

    /**
     * 消耗耐久度
     * @return true 如果工具還能使用，false 如果工具已損壞
     */
    public boolean consumeDurability(int count) {
        if (grade.hasUnbreakable()) return true;

        currentDurability -= count;
        setChanged();

        if (currentDurability <= 0) {
            // 移除方塊
            if (level != null) {
                level.removeBlock(getBlockPos(), false);
            }
            return false;
        }

        if (level != null && !level.isClientSide) {
            level.sendBlockUpdated(getBlockPos(), getBlockState(), getBlockState(), 3);
        }

        return true;
    }

    public void tick() {
        // 如果需要的話可以在這裡添加tick邏輯
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        tag.putString("Grade", grade.name());
        tag.putInt("Rank", rank);
        tag.putInt("BonusEntry", bonusEntryIndex);
        tag.putInt("CurrentDurability", currentDurability);
        tag.put("Inventory", itemHandler.serializeNBT(registries));
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        if (tag.contains("Grade")) {
            this.grade = AlchemyToolBlock.Grades.valueOf(tag.getString("Grade"));
        }
        if (tag.contains("Rank")) {
            this.rank = tag.getInt("Rank");
        }
        if (tag.contains("BonusEntry")) {
            this.bonusEntryIndex = tag.getInt("BonusEntry");
        }
        if (tag.contains("CurrentDurability")) {
            this.currentDurability = tag.getInt("CurrentDurability");
        }
        if (tag.contains("Inventory")) {
            itemHandler.deserializeNBT(registries, tag.getCompound("Inventory"));
        }
    }

    @Override
    public CompoundTag getUpdateTag(HolderLookup.Provider registries) {
        CompoundTag tag = super.getUpdateTag(registries);
        saveAdditional(tag, registries);
        return tag;
    }

    @Nullable
    @Override
    public Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }
}