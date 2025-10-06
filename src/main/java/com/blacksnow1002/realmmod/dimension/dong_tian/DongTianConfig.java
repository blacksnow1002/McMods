package com.blacksnow1002.realmmod.dimension.dong_tian;

import com.blacksnow1002.realmmod.RealmMod;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.dimension.DimensionType;

/**
 * 洞天系統配置類
 * 管理所有洞天相關的常量和配置
 */
public class DongTianConfig {

    // ========== 維度相關 ==========
    public static final String MOD_ID = RealmMod.MOD_ID;

    // 洞天維度的資源位置
    public static final ResourceLocation DONG_TIAN_DIM_LOCATION =
            ResourceLocation.fromNamespaceAndPath(MOD_ID, "dong_tian_dimension");

    // 洞天維度的ResourceKey
    public static ResourceKey<Level> DONG_TIAN_DIMENSION;
    public static ResourceKey<DimensionType> DONG_TIAN_DIM_TYPE;

    // ========== 空間大小設定 ==========
    // 每個玩家洞天的區塊數量（16x16方塊為一個區塊）
    public static final int DONG_TIAN_SIZE_IN_CHUNKS = 4; // 4x4區塊 = 64x64方塊

    // 洞天高度（方塊）
    public static final int DONG_TIAN_HEIGHT = 64;

    // 洞天之間的間隔（區塊）- 防止玩家走到其他人的洞天
    public static final int DONG_TIAN_SPACING = 100; // 每兩個洞天之間間隔10區塊

    // ========== 實體限制 ==========
    // 根據修為境界的實體數量限制
    public static final int ENTITY_LIMIT_LIANXU = 20;      // 煉虛期
    public static final int ENTITY_LIMIT_HETI = 50;        // 合體期
    public static final int ENTITY_LIMIT_DUJIE = 100;      // 渡劫期
    public static final int ENTITY_LIMIT_DACHENG = 200;    // 大乘期

    // ========== 方塊實體限制 ==========
    // 功能性方塊（如丹爐、煉器台）的數量限制
    public static final int TILE_ENTITY_LIMIT_LIANXU = 30;
    public static final int TILE_ENTITY_LIMIT_HETI = 60;
    public static final int TILE_ENTITY_LIMIT_DUJIE = 100;
    public static final int TILE_ENTITY_LIMIT_DACHENG = 150;

    // ========== 效能優化設定 ==========
    // 洞天區塊的tick更新間隔（越大越省效能）
    public static final int TICK_INTERVAL = 10; // 每10 tick更新一次

    // 玩家離開洞天後，延遲多久卸載區塊（秒）
    public static final int UNLOAD_DELAY_SECONDS = 30;

    // 靈田生長速度加成（倍數）
    public static final double SPIRIT_FIELD_GROWTH_MULTIPLIER = 2.0;

    // 洞天內打坐靈力恢復加成（倍數）
    public static final double MEDITATION_RECOVERY_MULTIPLIER = 3.0;

    // ========== 洞天出生點 ==========
    public static final int SPAWN_Y = 65; // 出生點高度

    /**
     * 初始化維度相關的ResourceKey
     * 應該在mod初始化階段調用
     */
    public static void initDimensionKeys() {
        DONG_TIAN_DIMENSION = ResourceKey.create(
                Registries.DIMENSION,
                DONG_TIAN_DIM_LOCATION
        );

        DONG_TIAN_DIM_TYPE = ResourceKey.create(
                Registries.DIMENSION_TYPE,
                DONG_TIAN_DIM_LOCATION
        );
    }

    /**
     * 根據境界獲取實體限制
     */
    public static int getEntityLimitForLevel(String cultivationLevel) {
        return switch (cultivationLevel) {
            case "煉虛期" -> ENTITY_LIMIT_LIANXU;
            case "合體期" -> ENTITY_LIMIT_HETI;
            case "渡劫期" -> ENTITY_LIMIT_DUJIE;
            case "大乘期" -> ENTITY_LIMIT_DACHENG;
            default -> ENTITY_LIMIT_LIANXU;
        };
    }

    /**
     * 根據境界獲取方塊實體限制
     */
    public static int getTileEntityLimitForLevel(String cultivationLevel) {
        return switch (cultivationLevel) {
            case "煉虛期" -> TILE_ENTITY_LIMIT_LIANXU;
            case "合體期" -> TILE_ENTITY_LIMIT_HETI;
            case "渡劫期" -> TILE_ENTITY_LIMIT_DUJIE;
            case "大乘期" -> TILE_ENTITY_LIMIT_DACHENG;
            default -> TILE_ENTITY_LIMIT_LIANXU;
        };
    }
}