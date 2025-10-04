package com.blacksnow1002.realmmod.event;

import com.blacksnow1002.realmmod.RealmMod;
import com.blacksnow1002.realmmod.capability.ModCapabilities;
import com.blacksnow1002.realmmod.capability.CultivationProvider;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;


@Mod.EventBusSubscriber(modid = RealmMod.MOD_ID)
public class ModEvents {

    // 給玩家附加 Capability
    @SubscribeEvent
    public static void onAttachCapabilities(AttachCapabilitiesEvent event) {
        if (event.getObject() instanceof net.minecraft.world.entity.player.Player player) {
            event.addCapability(net.minecraft.resources.ResourceLocation.fromNamespaceAndPath(RealmMod.MOD_ID, CultivationProvider.IDENTIFIER),
                    new CultivationProvider());
        }
    }

    // 玩家死亡/重生時複製數據
    @SubscribeEvent
    public static void onPlayerClone(PlayerEvent.Clone event) {
        event.getOriginal().getCapability(ModCapabilities.CULTIVATION_CAP).ifPresent(oldData -> {
            event.getEntity().getCapability(ModCapabilities.CULTIVATION_CAP).ifPresent(newData -> {
                newData.setRealm(oldData.getRealm());
                newData.setLayer(oldData.getLayer());
                newData.setBreakthroughSuccessPossibility(oldData.getBreakthroughSuccessPossibility());
                newData.setCultivation(oldData.getCultivation());
            });
        });
    }
}
