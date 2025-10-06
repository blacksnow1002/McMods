package com.blacksnow1002.realmmod.dimension.dong_tian;

import com.blacksnow1002.realmmod.RealmMod;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = RealmMod.MOD_ID)
public class DongTianPlayerEvents {

    @SubscribeEvent
    public static void onPlayerClone(PlayerEvent.Clone event) {
        if (!(event.getEntity() instanceof ServerPlayer newPlayer)) return;
        if (!(event.getOriginal() instanceof ServerPlayer oldPlayer)) return;
        if (newPlayer.level().isClientSide) return;

        CompoundTag oldTag = oldPlayer.getPersistentData();
        CompoundTag newTag = newPlayer.getPersistentData();

        if (oldTag.contains("DongTianData")) {
            newTag.put("DongTianData", oldTag.getCompound("DongTianData").copy());
            System.out.println("✅ 洞天資料已從舊玩家複製到新玩家");
        } else {
            System.out.println("⚠️ 舊玩家沒有 DongTianData");
        }
    }

    @SubscribeEvent
    public static void onPlayerRespawn(PlayerEvent.PlayerRespawnEvent event) {
        if (!(event.getEntity() instanceof ServerPlayer player)) return;

        PlayerDongTianData data = PlayerDongTianData.get(player);
        System.out.println("🔁 重生後洞天狀態: 已解鎖=" + data.isDongTianUnlocked());
    }
}

