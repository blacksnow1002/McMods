package com.blacksnow1002.realmmod.system.cultivation.realm.capability;

import com.blacksnow1002.realmmod.core.capability.ModCapabilities;
import com.blacksnow1002.realmmod.core.network.ModMessages;
import com.blacksnow1002.realmmod.system.cultivation.network.S2C.RealmSyncPacket;
import com.blacksnow1002.realmmod.system.cultivation.CultivationRealm;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;

import java.util.Map;

public class CultivationData implements ICultivationData {
    private CultivationRealm realm = CultivationRealm.first;
    private int layer = 1;
    private int cultivation = 0;
    private float breakthroughSuccessPossibility = 0;

    private Map<Integer, String> setDisplayLayer =
            Map.of(
                    1, "一層",
                    2, "二層",
                    3, "三層",
                    4, "四層",
                    5, "五層",
                    6, "六層",
                    7, "七層",
                    8, "八層",
                    9, "九層",
                    10, "大圓滿"
            );

    @Override
    public CultivationRealm getRealm() { return realm; }

    @Override
    public void setRealm(CultivationRealm realm) { this.realm = realm; }

    @Override
    public int getLayer() { return layer; }

    @Override
    public void setLayer(int layer) { this.layer = layer; }

    @Override
    public float getBreakthroughSuccessPossibility() {return breakthroughSuccessPossibility;}

    @Override
    public void setBreakthroughSuccessPossibility(float breakthroughSuccessPossibility) {
        this.breakthroughSuccessPossibility = breakthroughSuccessPossibility;
    }

    @Override
    public int getCultivation() { return cultivation; }

    @Override
    public void setCultivation(int cultivation) { this.cultivation = cultivation; }

    @Override
    public void addCultivation(Player player, int amount) {
        this.cultivation += amount;
        // 如果不是大圓滿層(第10層),檢查是否可以突破小境界
        if (layer < realm.getMaxLayer() && cultivation >= realm.getRequiredPerLayer()) {
            player.sendSystemMessage(Component.translatable("message.realmmod.breakthrough.can_breakthrough"));
        }
        // 如果是大圓滿層,提示需要滿足條件才能突破大境界
        else if (layer == realm.getMaxLayer() && canBreakthroughToNextRealm(player)) {
            player.sendSystemMessage(Component.translatable("message.realmmod.breakthrough.can_breakthrough_realm"));
        }
    }

    @Override
    public CompoundTag saveNBTData() {
        CompoundTag nbt = new CompoundTag();
        nbt.putString("Stage", realm.name());
        nbt.putInt("Layer", layer);
        nbt.putFloat("BreakthroughSuccessPossibility", breakthroughSuccessPossibility);
        nbt.putInt("Cultivation", cultivation);
        return nbt;
    }

    @Override
    public void loadNBTData(CompoundTag nbt) {
        if (nbt.contains("Stage")) {
            this.realm = CultivationRealm.valueOf(nbt.getString("Stage"));
        }
        if (nbt.contains("Layer")) {
            this.layer = nbt.getInt("Layer");
        }
        if (nbt.contains("BreakthroughSuccessPossibility")) {
            this.breakthroughSuccessPossibility = nbt.getFloat("BreakthroughSuccessPossibility");
        }
        if (nbt.contains("Cultivation")) {
            this.cultivation = nbt.getInt("Cultivation");
        }
    }

    /**
     * 檢查是否可以從大圓滿突破到下一個大境界
     * 這裡你可以添加自定義的條件,例如:
     * - 擁有特定物品
     * - 達到特定經驗等級
     * - 完成特定任務
     * - 擊殺特定怪物
     */
    private boolean canBreakthroughToNextRealm(Player player) {
        final boolean[] result = {false};
        player.getCapability(ModCapabilities.CULTIVATION_CAP).ifPresent(cap -> {
            CultivationRealm realm = cap.getRealm();
            int realmOrdinal = realm.ordinal();

            player.getCapability(ModCapabilities.BREAKTHROUGH_CAPABILITY_CAP).ifPresent(data -> {
                if (data.canBreakthrough(realmOrdinal)) result[0] = true;
            });

        });
        return result[0];
    }

    @Override
    public void tryBreakthrough(Player player) {
        // 如果是大圓滿層,嘗試突破到下一個大境界
        if (layer == realm.getMaxLayer()) {
            tryBreakthroughToNextRealm(player);
        }
        // 否則嘗試突破小境界
        else {
            tryBreakthroughLayer(player);
        }
    }

    /**
     * 嘗試突破小境界(1-9層)
     */
    private void tryBreakthroughLayer(Player player) {
        if (cultivation >= realm.getRequiredPerLayer()) {
            if(Math.random() < breakthroughSuccessPossibility) {
                // 突破成功
                cultivation -= realm.getRequiredPerLayer();
                layer++;

                player.sendSystemMessage(Component.translatable(
                        "message.realmmod.breakthrough.success.small",
                        realm.getDisplayName(),
                        setDisplayLayer.get(layer)
                ));

                if(player instanceof ServerPlayer) {
                    ModMessages.sendToPlayer(new RealmSyncPacket(realm.ordinal(), layer), (ServerPlayer) player);
                }
            } else {
                // 突破失敗
                cultivation = (int)(cultivation * 0.9f);
                breakthroughSuccessPossibility += 0.03f;
                player.sendSystemMessage(Component.translatable("message.realmmod.breakthrough.fail"));
                player.sendSystemMessage(Component.translatable(
                        "message.realmmod.breakthrough.fail.cultivation",
                        cultivation,
                        realm.getRequiredPerLayer()
                ));
                player.sendSystemMessage(Component.translatable(
                        "message.realmmod.breakthrough.fail.possibility",
                        (breakthroughSuccessPossibility * 100)
                ));
            }
        } else {
            player.sendSystemMessage(Component.translatable(
                    "message.realmmod.breakthrough.de_cultivation",
                    cultivation,
                    realm.getRequiredPerLayer()
            ));
        }
    }

    /**
     * 嘗試從大圓滿突破到下一個大境界
     * 不需要消耗靈力,但需要滿足特定條件
     */
    private void tryBreakthroughToNextRealm(Player player) {
        if (!canBreakthroughToNextRealm(player)) {
            player.sendSystemMessage(Component.translatable("message.realmmod.breakthrough.condition_not_met"));
            return;
        }

        // 從大圓滿突破到下一個大境界
        // 不消耗 cultivation
        layer = 1;
        realm = realm.getNextRealm();
        breakthroughSuccessPossibility = realm.getBreakthroughSuccessPossibility();

        player.sendSystemMessage(Component.translatable(
                "message.realmmod.breakthrough.success.big",
                realm.getDisplayName()
        ));

        if(player instanceof ServerPlayer) {
            ModMessages.sendToPlayer(new RealmSyncPacket(realm.ordinal(), layer), (ServerPlayer) player);
        }
    }
}