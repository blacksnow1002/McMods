package com.blacksnow1002.realmmod.block.custom;

import com.blacksnow1002.realmmod.block.entity.ReforgeToolBlockEntity;
import com.blacksnow1002.realmmod.item.custom.ReforgeToolBlockItem;
import com.blacksnow1002.realmmod.profession.reforge.screen.ReforgeFurnaceMenu;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.Nullable;

public class ReforgeToolBlock extends Block implements EntityBlock {

    public enum Grades {
        HEAVEN("天", 1.0f, -1.0f, 0.3f, 0.3f, 0.2f, 0.2f, 0),
        EARTH("地", 0.6f, -1.0f, 0.5f, 0.2f, 0.2f, 0.1f, 0),
        MYSTIC("玄", 0.2f, -0.5f, 0.2f, 0.2f, 0.1f, 0.0f, 100),
        MORTAL("黃", 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 50);

        private final String displayName;
        private final float expBonus;
        private final float wasteReduction;
        private final float mortalIncrease;
        private final float mysticIncrease;
        private final float earthIncrease;
        private final float heavenIncrease;
        private final int maxDurability;

        Grades(String displayName, float expBonus, float wasteReduction, float mortalIncrease,
               float mysticIncrease, float earthIncrease, float heavenIncrease, int maxDurability) {
            this.displayName = displayName;
            this.expBonus = expBonus;
            this.wasteReduction = wasteReduction;
            this.mortalIncrease = mortalIncrease;
            this.mysticIncrease = mysticIncrease;
            this.earthIncrease = earthIncrease;
            this.heavenIncrease = heavenIncrease;
            this.maxDurability = maxDurability;
        }

        public String getDisplayName() { return displayName; }
        public float getExpBonus() { return expBonus; }
        public float getWasteReduction() { return wasteReduction; }
        public float getMortalIncrease() { return mortalIncrease; }
        public float getMysticIncrease() { return mysticIncrease; }
        public float getEarthIncrease() { return earthIncrease; }
        public float getHeavenIncrease() { return heavenIncrease; }
        public int getMaxDurability() { return maxDurability; }
        public boolean hasUnbreakable() { return maxDurability == 0; }

        public static Grades fromName(String name) {
            for (Grades grade : values()) {
                if (grade.displayName.equals(name)) return grade;
            }
            return MORTAL;
        }
    }

    public enum BonusEntry {
        ENTRY_1("詞條一", "煉丹失敗時，必定不會產生心魔"),
        ENTRY_2("詞條二", "煉丹失敗時，返還其中一種主材"),
        ENTRY_3("詞條三", "煉製低品級丹藥時，可獲得50%基本職業經驗(不額外套用基本丹爐加成)"),
        ENTRY_4("詞條四", "50%機率觸發，不套用原材料效果，隨機抽取詞條及屬性，但必附帶一條限定詞條"),
        ENTRY_5("詞條五", "煉丹成功後，有8%機率直接提升一階丹藥品質"),
        ENTRY_6("詞條六", "煉丹持續時間減少1/2"),
        ENTRY_7("詞條七", "煉丹成功時，給予額外100%職業經驗加乘");

        private final String displayName;
        private final String description;

        BonusEntry(String displayName, String description) {
            this.displayName = displayName;
            this.description = description;
        }

        public String getDisplayName() { return displayName; }
        public String getDescription() { return description; }
    }

    private final Grades grade;
    private final int rank;

    public ReforgeToolBlock(Properties properties, Grades grade, int rank) {
        super(properties);
        this.grade = grade;
        this.rank = rank;
    }

    public Grades getGrade() { return grade; }
    public int getRank() { return rank; }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new ReforgeToolBlockEntity(pos, state, grade, rank);
    }

    /**
     * 當方塊被放置時調用
     * 從物品 NBT 複製詞條到 BlockEntity
     */
    @Override
    public void setPlacedBy(Level level, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack stack) {
        super.setPlacedBy(level, pos, state, placer, stack);

        if (!level.isClientSide && stack.getItem() instanceof ReforgeToolBlockItem) {
            BlockEntity be = level.getBlockEntity(pos);
            if (be instanceof ReforgeToolBlockEntity entity) {
                // 從物品複製詞條到方塊實體
                BonusEntry bonus = ReforgeToolBlockItem.getBonus(stack);
                if (bonus != null) {
                    entity.setBonus(bonus);
                }
            }
        }
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
        return level.isClientSide ? null : (lvl, pos, st, be) -> {
            if (be instanceof ReforgeToolBlockEntity entity) {
                entity.tick();
            }
        };
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos,
                                               Player player, BlockHitResult hitResult) {
        if (!level.isClientSide && player instanceof ServerPlayer serverPlayer) {
            BlockEntity be = level.getBlockEntity(pos);
            if (be instanceof ReforgeToolBlockEntity entity) {
                // 打開GUI
                serverPlayer.openMenu(new SimpleMenuProvider(
                        (id, playerInventory, p) -> new ReforgeFurnaceMenu(
                                id, playerInventory, be, entity.getItemHandler()
                        ),
                        Component.literal("煉器鼎")
                ), pos);
            }
        }
        return InteractionResult.SUCCESS;
    }
}