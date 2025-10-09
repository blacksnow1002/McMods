package com.blacksnow1002.realmmod.event;

import com.blacksnow1002.realmmod.RealmMod;
import com.blacksnow1002.realmmod.capability.ModCapabilities;
import com.blacksnow1002.realmmod.capability.world.WorldProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import static net.minecraftforge.fml.common.Mod.*;

@EventBusSubscriber(modid = RealmMod.MOD_ID)
public class WorldCapabilityEvents {
    @SubscribeEvent
    public static void onAttachWorldCapability(AttachCapabilitiesEvent<Level> event) {
        Level level = event.getObject();

        // 只在伺服器世界註冊（不在客戶端 Level）
        if (!level.isClientSide()) {
            if (!level.getCapability(ModCapabilities.WORLD_CAP).isPresent()) {
                ResourceLocation id = ResourceLocation.parse("realmmod:world");
                event.addCapability(
                        id,
                        new WorldProvider()
                );
            }
        }
    }
}

