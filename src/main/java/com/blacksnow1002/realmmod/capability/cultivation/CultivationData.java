package com.blacksnow1002.realmmod.capability.cultivation;

import com.blacksnow1002.realmmod.network.ModMessages;
import com.blacksnow1002.realmmod.network.packets.RealmSyncPacket;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;

import java.util.Map;

public class CultivationData implements ICultivationData {
    private CultivationRealm realm = CultivationRealm.first; // 當前大境界
    private int layer = 1; // 當前小境界層數
    private int cultivation = 0; // 當前修為值
    private float breakthroughSuccessPossibility = 0; //突破成功率

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
    public void setBreakthroughSuccessPossibility(float breakthroughSuccessPossibility) {this.breakthroughSuccessPossibility = breakthroughSuccessPossibility;}

    @Override
    public int getCultivation() { return cultivation; }

    @Override
    public void setCultivation(int cultivation) { this.cultivation = cultivation; }

    @Override
    public void addCultivation(Player player, int amount) {
        this.cultivation += amount;
        if (layer != realm.getMaxLayer() && cultivation >= realm.getRequiredPerLayer()) {
            player.sendSystemMessage(Component.translatable("message.realmmod.breakthrough.can_breakthrough"));
        }
    }

    @Override
    public void saveNBTData(CompoundTag nbt) {
        nbt.putString("Stage", realm.name());
        nbt.putInt("Layer", layer);
        nbt.putFloat("BreakthroughSuccessPossibility", breakthroughSuccessPossibility);
        nbt.putInt("Cultivation", cultivation);
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

    @Override
    public void tryBreakthrough(Player player) {
        if (cultivation >= realm.getRequiredPerLayer()) {
            if(Math.random() < breakthroughSuccessPossibility) {
                cultivation -= realm.getRequiredPerLayer();
                layer++;

                if (layer > realm.getMaxLayer()) {
                    layer = 1;
                    realm = realm.getNextRealm();
                    breakthroughSuccessPossibility = realm.getBreakthroughSuccessPossibility();
                    player.sendSystemMessage(Component.translatable(
                            "message.realmmod.breakthrough.success.big",
                            realm.getDisplayName()
                    ));
                } else {
                    player.sendSystemMessage(Component.translatable(
                            "message.realmmod.breakthrough.success.small",
                            realm.getDisplayName(),
                            setDisplayLayer.get(layer)
                    ));
                }
                if(player instanceof  ServerPlayer) ModMessages.sendToPlayer(new RealmSyncPacket(realm.ordinal(), layer),(ServerPlayer) player);
            } else {
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
}
