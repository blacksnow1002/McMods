package com.blacksnow1002.realmmod.profession.reforge.screen;

import com.blacksnow1002.realmmod.screen.ModMenuTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.items.ItemStackHandler;
import net.minecraftforge.items.SlotItemHandler;

public class ReforgeFurnaceMenu extends AbstractContainerMenu {
    private final BlockEntity blockEntity;
    private final ItemStackHandler itemHandler;
    private final BlockPos blockPos; // 新增：保存方塊位置

    // 槽位索引
    public static final int RECIPE_SLOT = 0;      // 圖紙槽
    public static final int IGNORED_SLOT = 1;     // 不使用的槽位
    public static final int CORE_SLOT = 2;  // 核心槽
    public static final int MAIN_1_SLOT = 3;      // 主材1槽
    public static final int MAIN_2_SLOT = 4;      // 主材2槽
    public static final int AUX_SLOT = 5;         // 輔材槽
    public static final int OUTPUT_SLOT = 6;      // 輸出槽

    // 客戶端構造函數 - 給 MenuType 註冊使用
    public ReforgeFurnaceMenu(int id, Inventory playerInv, FriendlyByteBuf data) {
        super(ModMenuTypes.REFORGE_FURNACE_MENU.get(), id);

        this.blockPos = data.readBlockPos();
        this.blockEntity = playerInv.player.level().getBlockEntity(blockPos);

        // 創建臨時的 handler（客戶端會通過同步更新）
        this.itemHandler = new ItemStackHandler(7);

        // 添加所有槽位
        addSlots(playerInv);
    }

    // 服務端構造函數
    public ReforgeFurnaceMenu(int id, Inventory playerInv, BlockEntity blockEntity, ItemStackHandler itemHandler) {
        super(ModMenuTypes.REFORGE_FURNACE_MENU.get(), id);
        this.blockEntity = blockEntity;
        this.blockPos = blockEntity.getBlockPos();
        this.itemHandler = itemHandler;

        // 添加所有槽位
        addSlots(playerInv);
    }

    // 統一的槽位添加方法，避免代碼重複
    private void addSlots(Inventory playerInv) {
        // 添加方塊實體的槽位
        // 第一排：丹方和占位槽
        this.addSlot(new SlotItemHandler(itemHandler, RECIPE_SLOT, 98, 17));        // 圖紙
        this.addSlot(new SlotItemHandler(itemHandler, IGNORED_SLOT, 134, 17));      // 占位槽（不使用）

        // 第二排：材料槽位
        this.addSlot(new SlotItemHandler(itemHandler, CORE_SLOT, 62, 44));          // 核心
        this.addSlot(new SlotItemHandler(itemHandler, MAIN_1_SLOT, 89, 44));        // 主材1
        this.addSlot(new SlotItemHandler(itemHandler, MAIN_2_SLOT, 116, 44));       // 主材2
        this.addSlot(new SlotItemHandler(itemHandler, AUX_SLOT, 143, 44));          // 輔材

        // 輸出槽（只能取出，不能放入）
        this.addSlot(new SlotItemHandler(itemHandler, OUTPUT_SLOT, 188, 44) {
            @Override
            public boolean mayPlace(ItemStack stack) {
                return false;
            }
        });

        // 添加玩家背包槽位
        layoutPlayerInventorySlots(playerInv, 26, 84);
    }

    private void layoutPlayerInventorySlots(Inventory playerInv, int x, int y) {
        // 玩家背包主要部分 (3x9)
        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 9; col++) {
                this.addSlot(new Slot(playerInv, col + row * 9 + 9, x + col * 18, y + row * 18));
            }
        }

        // 玩家快捷欄 (1x9)
        for (int col = 0; col < 9; col++) {
            this.addSlot(new Slot(playerInv, col, x + col * 18, y + 58));
        }
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        ItemStack itemstack = ItemStack.EMPTY;
        Slot slot = this.slots.get(index);

        if (slot.hasItem()) {
            ItemStack stack = slot.getItem();
            itemstack = stack.copy();

            if (index < 7) {
                // 從方塊實體移到玩家背包
                if (!this.moveItemStackTo(stack, 7, this.slots.size(), true)) {
                    return ItemStack.EMPTY;
                }
            } else {
                // 從玩家背包移到方塊實體
                if (!this.moveItemStackTo(stack, 0, 7, false)) {
                    return ItemStack.EMPTY;
                }
            }

            if (stack.isEmpty()) {
                slot.set(ItemStack.EMPTY);
            } else {
                slot.setChanged();
            }
        }

        return itemstack;
    }

    @Override
    public boolean stillValid(Player player) {
        // 使用 blockPos 而不是 blockEntity 來檢查
        if (this.blockPos == null) {
            return false;
        }

        return player.distanceToSqr(
                this.blockPos.getX() + 0.5D,
                this.blockPos.getY() + 0.5D,
                this.blockPos.getZ() + 0.5D
        ) <= 64.0D;
    }

    public BlockEntity getBlockEntity() {
        return this.blockEntity;
    }

    public BlockPos getBlockPos() {
        return this.blockPos;
    }

    public ItemStackHandler getItemHandler() {
        return this.itemHandler;
    }
}