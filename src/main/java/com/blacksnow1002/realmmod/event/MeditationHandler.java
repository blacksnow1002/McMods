package com.blacksnow1002.realmmod.event;

import com.blacksnow1002.realmmod.RealmMod;
import com.blacksnow1002.realmmod.capability.ModCapabilities;
import com.blacksnow1002.realmmod.item.custom.SpiritStoneItem;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = RealmMod.MOD_ID)
public class MeditationHandler {
    private static final int MEDITATION_GAIN_INTERVAL = 20; // 每隔多少 tick 獲得修為
    private static final int CULTIVATION_GAIN = 1;

    @SubscribeEvent
    public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
        if(event.phase != TickEvent.Phase.END) return;
        Player player = event.player;

        if(player == null || player.level().isClientSide) return;

        var data = player.getPersistentData();
        boolean isMeditating = data.getBoolean("realmmod.Meditation.IsMeditating");
        int stillTicks = data.getInt("realmmod.Meditation.StillTicks");

        if (isMeditating) {
            if (player.isSprinting() || player.isCrouching() || player.isSwimming()) {
                stopMeditation(player);
                return;
            }
            stillTicks ++;
            data.putInt("realmmod.Meditation.StillTicks", stillTicks);

            if (stillTicks % MEDITATION_GAIN_INTERVAL == 0) {
                player.getCapability(ModCapabilities.CULTIVATION_CAP).ifPresent(cap -> {
                    int gain = CULTIVATION_GAIN;
                    var stack = player.getMainHandItem();

                    if (stack.getItem() instanceof SpiritStoneItem stone) {
                        gain = stone.getCultivationValue();
                        stack.shrink(1);
                        player.displayClientMessage(Component.translatable(
                            "message.realmmod.meditation.gain.spirit_stone",
                            stack.getHoverName().getString(),
                            gain,
                            cap.getCultivation() + gain,
                            cap.getRealm().getRequiredPerLayer()),
                            true);
                    } else {
                        player.displayClientMessage(Component.translatable(
                            "message.realmmod.meditation.gain.default",
                            gain,
                            cap.getCultivation(),
                            cap.getRealm().getRequiredPerLayer()),
                            true);
                    }
                    cap.addCultivation(player, gain);
                });
            }

            if(player.getPose() != Pose.SITTING) { player.setPose(Pose.SITTING); }
        } else {
            if (player.getPose() == Pose.SITTING) { player.setPose(Pose.STANDING); }
            data.putInt("realmmod.Meditation.StillTicks", 0);
        }
    }

    private static void stopMeditation(Player player) {
        var data = player.getPersistentData();
        data.putBoolean("realmmod.Meditation.IsMeditating", false);
        data.putInt("realmmod.Meditation.StillTicks", 0);
        player.setPose(Pose.STANDING);
        player.sendSystemMessage(Component.translatable("message.realmmod.meditation.end"));
    }
}