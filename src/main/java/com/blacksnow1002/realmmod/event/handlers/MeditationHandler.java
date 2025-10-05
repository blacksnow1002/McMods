package com.blacksnow1002.realmmod.event.handlers;

import com.blacksnow1002.realmmod.RealmMod;
import com.blacksnow1002.realmmod.capability.ModCapabilities;
import com.blacksnow1002.realmmod.item.custom.SpiritStoneItem;
import com.blacksnow1002.realmmod.util.ArmorStandUtils;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.decoration.ArmorStand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Mod.EventBusSubscriber(modid = RealmMod.MOD_ID)
public class MeditationHandler {
    private static final int MEDITATION_GAIN_INTERVAL = 20;
    private static final int CULTIVATION_GAIN = 1;
    private static final Map<UUID, Integer> playerSeats = new HashMap<>();

    @SubscribeEvent
    public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
        if(event.phase != TickEvent.Phase.END) return;
        Player player = event.player;

        if(player.level().isClientSide) {
            if(player.getVehicle() instanceof ArmorStand armorStand && armorStand.isInvisible()) {
                spawnMeditationParticles(player);
            }
        }
        if(player == null || player.level().isClientSide) return;

        var data = player.getPersistentData();
        boolean isMeditating = data.getBoolean("realmmod.Meditation.IsMeditating");
        int stillTicks = data.getInt("realmmod.Meditation.StillTicks");

        if (isMeditating) {
            UUID uuid = player.getUUID();
            Integer seatId = playerSeats.get(uuid);

            // 創建座椅
            if(seatId == null) {
                startMeditation(player);
                return;
            }

            // 檢查座椅是否還存在
            Entity seat = player.level().getEntity(seatId);
            if(seat == null || !player.isPassenger()) {
                stopMeditation(player);
                return;
            }

            // 檢查玩家是否做了會中斷打坐的動作
            if (player.isSprinting() || player.isSwimming()) {
                stopMeditation(player);
                return;
            }

            // 保持座椅位置固定
            if(seat instanceof ArmorStand armorStand) {
                armorStand.setDeltaMovement(Vec3.ZERO);
                armorStand.setPos(armorStand.getX(), armorStand.getY(), armorStand.getZ());
            }

            stillTicks++;
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
        } else {
            data.putInt("realmmod.Meditation.StillTicks", 0);
        }
    }

    private static void spawnMeditationParticles(Player player) {
        var level = player.level();
        var random = level.random;

        // 每 tick 生成 2-3 個粒子
        int particleCount = 2 + random.nextInt(3);

        for (int i = 0; i < particleCount; i++) {
            // 在玩家周圍隨機生成粒子
            double angle = random.nextDouble() * Math.PI * 2;
            double radius = 0.5 + random.nextDouble() * 1; // 1.5-3 格距離
            double height = random.nextDouble() * 1.5; // 0-1.5 格高度

            double offsetX = Math.cos(angle) * radius;
            double offsetZ = Math.sin(angle) * radius;

            double startX = player.getX() + offsetX;
            double startY = player.getY() + height;
            double startZ = player.getZ() + offsetZ;

            // 計算朝向玩家的速度（聚集效果）
            double targetX = player.getX();
            double targetY = player.getY() + 0.8; // 玩家胸部位置
            double targetZ = player.getZ();

            // 增加速度讓聚集效果更明顯
            double velocityX = (targetX - startX) * 0.25;
            double velocityY = (targetY - startY) * 0.25;
            double velocityZ = (targetZ - startZ) * 0.25;

            // 使用附魔台粒子效果
            level.addParticle(
                    net.minecraft.core.particles.ParticleTypes.ENCHANT,
                    startX, startY, startZ,
                    velocityX, velocityY, velocityZ
            );
        }
    }

    private static void startMeditation(Player player) {
        var level = player.level();

        // 創建 ArmorStand 作為座椅,位置稍微低一點讓玩家看起來坐在地上
        ArmorStand seat = new ArmorStand(level, player.getX(), player.getY() - 1.9, player.getZ());

        // 設置 ArmorStand 屬性
        seat.setInvisible(true);
        seat.setInvulnerable(true);
        seat.setNoGravity(true);
        seat.setNoBasePlate(true);
        seat.setSilent(true);
        seat.setHealth(0.0001f);
        seat.setDeltaMovement(Vec3.ZERO);

        seat.setYRot(player.getYRot());
        seat.setYHeadRot(player.getYHeadRot());

        level.addFreshEntity(seat);

        // 讓玩家坐上去
        player.startRiding(seat, true);

        // 記錄座椅ID
        playerSeats.put(player.getUUID(), seat.getId());
        player.getPersistentData().putInt("realmmod.Meditation.SeatId", seat.getId());
    }

    private static void stopMeditation(Player player) {
        var data = player.getPersistentData();
        UUID uuid = player.getUUID();
        Integer seatId = playerSeats.get(uuid);

        if(seatId != null) {
            Entity seat = player.level().getEntity(seatId);
            if(seat != null) {
                player.stopRiding();
                seat.discard();
            }
            playerSeats.remove(uuid);
        }

        data.putBoolean("realmmod.Meditation.IsMeditating", false);
        data.putInt("realmmod.Meditation.SeatId", 0);
        data.putInt("realmmod.Meditation.StillTicks", 0);
        player.sendSystemMessage(Component.translatable("message.realmmod.meditation.end"));
    }
}