package com.blacksnow1002.realmmod.event;

import com.blacksnow1002.realmmod.RealmMod;
import com.blacksnow1002.realmmod.achievement.AchievementManager;
import com.blacksnow1002.realmmod.achievement.CustomAchievements;
import com.blacksnow1002.realmmod.assignment.AssignmentProvider;
import com.blacksnow1002.realmmod.capability.ModCapabilities;
import com.blacksnow1002.realmmod.capability.attribute.PlayerTotalAttributeProvider;
import com.blacksnow1002.realmmod.capability.attribute.allocate.AllocateAttributeProvider;
import com.blacksnow1002.realmmod.capability.attribute.equipment.EquipmentAttributeProvider;
import com.blacksnow1002.realmmod.capability.attribute.realm.RealmAttributeProvider;
import com.blacksnow1002.realmmod.capability.attribute.technique.TechniqueAttributeProvider;
import com.blacksnow1002.realmmod.capability.mana.ManaProvider;
import com.blacksnow1002.realmmod.capability.money.MoneyProvider;
import com.blacksnow1002.realmmod.capability.realm_breakthrough.RealmBreakthroughProvider;
import com.blacksnow1002.realmmod.capability.cultivation.CultivationProvider;
import com.blacksnow1002.realmmod.capability.age.AgeProvider;
import com.blacksnow1002.realmmod.capability.spiritroot.SpiritRootProvider;
import com.blacksnow1002.realmmod.mailbox.ClientMailCache;
import com.blacksnow1002.realmmod.mailbox.Mail;
import com.blacksnow1002.realmmod.mailbox.MailboxStorage;
import com.blacksnow1002.realmmod.network.ModMessages;
import com.blacksnow1002.realmmod.network.packets.S2C.MailSyncPacket;
import com.blacksnow1002.realmmod.network.packets.S2C.RealmSyncPacket;
import com.blacksnow1002.realmmod.technique.TechniqueProvider;
import com.blacksnow1002.realmmod.title.TitleProvider;
import com.blacksnow1002.realmmod.title.TitleSystem;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.List;

@Mod.EventBusSubscriber(modid = RealmMod.MOD_ID)
public class ModEvents {

    // 給玩家附加 Capability - 修正泛型參數
    @SubscribeEvent
    public static void onAttachCapabilities(AttachCapabilitiesEvent<Entity> event) {
        if (event.getObject() instanceof Player player) {
            // 確保不會重複添加
            if (!player.getCapability(ModCapabilities.CULTIVATION_CAP).isPresent()) {
                event.addCapability(
                        ResourceLocation.fromNamespaceAndPath(RealmMod.MOD_ID, CultivationProvider.IDENTIFIER),
                        new CultivationProvider()
                );
            }

            // 年
            if (!player.getCapability(ModCapabilities.AGE_CAP).isPresent()) {
                event.addCapability(
                        ResourceLocation.fromNamespaceAndPath(RealmMod.MOD_ID, AgeProvider.IDENTIFIER),
                        new AgeProvider()
                );
            }

            //境界突破
            if (!player.getCapability(ModCapabilities.BREAKTHROUGH_CAPABILITY_CAP).isPresent()) {
                event.addCapability(
                        ResourceLocation.fromNamespaceAndPath(RealmMod.MOD_ID, RealmBreakthroughProvider.IDENTIFIER),
                        new RealmBreakthroughProvider()
                );
            }

            //靈根
            if (!player.getCapability(ModCapabilities.SPIRIT_ROOT_CAP).isPresent()) {
                event.addCapability(
                        ResourceLocation.fromNamespaceAndPath(RealmMod.MOD_ID, SpiritRootProvider.IDENTIFIER),
                        new SpiritRootProvider()
                );
            }

            //真元
            if (!player.getCapability(ModCapabilities.MANA_CAP).isPresent()) {
                event.addCapability(
                        ResourceLocation.fromNamespaceAndPath(RealmMod.MOD_ID, ManaProvider.IDENTIFIER),
                        new ManaProvider()
                );
            }

            //玩家數據
            if(!player.getCapability(ModCapabilities.ALLOCATE_ATTRIBUTE_CAP).isPresent()){
                event.addCapability(
                        ResourceLocation.fromNamespaceAndPath(RealmMod.MOD_ID, AllocateAttributeProvider.IDENTIFIER),
                        new AllocateAttributeProvider()
                );
            }
            if(!player.getCapability(ModCapabilities.REALM_ATTRIBUTE_CAP).isPresent()){
                event.addCapability(
                        ResourceLocation.fromNamespaceAndPath(RealmMod.MOD_ID, RealmAttributeProvider.IDENTIFIER),
                        new RealmAttributeProvider()
                );
            }
            if(!player.getCapability(ModCapabilities.EQUIPMENT_ATTRIBUTE_CAP).isPresent()){
                event.addCapability(
                        ResourceLocation.fromNamespaceAndPath(RealmMod.MOD_ID, EquipmentAttributeProvider.IDENTIFIER),
                        new EquipmentAttributeProvider()
                );
            }
            if(!player.getCapability(ModCapabilities.TECHNIQUE_ATTRIBUTE_CAP).isPresent()){
                event.addCapability(
                        ResourceLocation.fromNamespaceAndPath(RealmMod.MOD_ID, TechniqueAttributeProvider.IDENTIFIER),
                        new TechniqueAttributeProvider()
                );
            }
            if(!player.getCapability(ModCapabilities.PLAYER_TOTAL_ATTRIBUTE_CAP).isPresent()){
                event.addCapability(
                        ResourceLocation.fromNamespaceAndPath(RealmMod.MOD_ID, PlayerTotalAttributeProvider.IDENTIFIER),
                        new PlayerTotalAttributeProvider()
                );
            }

            if(!player.getCapability(ModCapabilities.MONEY_CAP).isPresent()){
                event.addCapability(
                        ResourceLocation.fromNamespaceAndPath(RealmMod.MOD_ID, MoneyProvider.IDENTIFIER),
                        new MoneyProvider()
                );
            }

            if(!player.getCapability(ModCapabilities.ASSIGNMENT_CAP).isPresent()){
                event.addCapability(
                        ResourceLocation.fromNamespaceAndPath(RealmMod.MOD_ID, AssignmentProvider.IDENTIFIER),
                        new AssignmentProvider()
                );
            }

            if(!player.getCapability(ModCapabilities.TECHNIQUE_CAP).isPresent()){
                event.addCapability(
                        ResourceLocation.fromNamespaceAndPath(RealmMod.MOD_ID, TechniqueProvider.IDENTIFIER),
                        new TechniqueProvider()
                );
            }

            if(!player.getCapability(ModCapabilities.TITLE_CAP).isPresent()){
                event.addCapability(
                        ResourceLocation.fromNamespaceAndPath(RealmMod.MOD_ID, TitleProvider.IDENTIFIER),
                        new TitleProvider()
                );
            }




        }
    }

    // 玩家死亡/重生時複製數據
    @SubscribeEvent
    public static void onPlayerClone(PlayerEvent.Clone event) {
        // 只在死亡重生時處理 (不處理從末地返回的情況)

        System.out.println("========================================");
        System.out.println("=== PlayerEvent.Clone 觸發 (死亡重生) ===");

        Player originalPlayer = event.getOriginal();
        Player newPlayer = event.getEntity();

        // 強制刷新舊玩家的 Capability (重要!)
        originalPlayer.reviveCaps();

        try {
            // 從舊玩家讀取並複製到新玩家
            originalPlayer.getCapability(ModCapabilities.CULTIVATION_CAP).ifPresent(oldData -> {
                newPlayer.getCapability(ModCapabilities.CULTIVATION_CAP).ifPresent(newData -> {

                    CompoundTag tag = oldData.saveNBTData();
                    newData.loadNBTData(tag);

                    // 同步到客戶端
                    if (newPlayer instanceof ServerPlayer serverPlayer) {
                        ModMessages.sendToPlayer(
                                new RealmSyncPacket(
                                        newData.getRealm().ordinal(),
                                        newData.getLayer()
                                ),
                                serverPlayer
                        );
                    }
                });
            });

            originalPlayer.getCapability(ModCapabilities.ASSIGNMENT_CAP).ifPresent(oldData -> {
                newPlayer.getCapability(ModCapabilities.ASSIGNMENT_CAP).ifPresent(newData -> {
                    CompoundTag tag = oldData.saveNBTData();
                    newData.loadNBTData(tag);
                });
            });

            originalPlayer.getCapability(ModCapabilities.TECHNIQUE_CAP).ifPresent(oldData -> {
                newPlayer.getCapability(ModCapabilities.TECHNIQUE_CAP).ifPresent(newData -> {
                    CompoundTag tag = oldData.saveNBTData();
                    newData.loadNBTData(tag);
                });
            });

            originalPlayer.getCapability(ModCapabilities.TITLE_CAP).ifPresent(oldData -> {
                newPlayer.getCapability(ModCapabilities.TITLE_CAP).ifPresent(newData -> {
                    CompoundTag tag = oldData.saveNBTData();
                    newData.loadNBTData(tag);

                    if (event.getEntity() instanceof ServerPlayer player) {
                        player.getServer().execute(() -> {
                            TitleSystem.getInstance().syncEquippedTitle(player);
                            System.out.println("[TitleSync] 玩家 " + player.getName().getString() + " Clone 事件，已同步稱號");
                        });
                    }

                });
            });

            originalPlayer.getCapability(ModCapabilities.AGE_CAP).ifPresent(oldData -> {
                newPlayer.getCapability(ModCapabilities.AGE_CAP).ifPresent(newData -> {
                    CompoundTag tag = oldData.saveNBTData();
                    newData.loadNBTData(tag);
                });
            });

            originalPlayer.getCapability(ModCapabilities.BREAKTHROUGH_CAPABILITY_CAP).ifPresent(oldData -> {
                newPlayer.getCapability(ModCapabilities.BREAKTHROUGH_CAPABILITY_CAP).ifPresent(newData -> {
                    CompoundTag tag = oldData.saveNBTData();
                    newData.loadNBTData(tag);
                });
            });

            originalPlayer.getCapability(ModCapabilities.MANA_CAP).ifPresent(oldData -> {
                newPlayer.getCapability(ModCapabilities.MANA_CAP).ifPresent(newData -> {
                    CompoundTag tag = oldData.saveNBTData();
                    newData.loadNBTData(tag);
                });
            });

            originalPlayer.getCapability(ModCapabilities.MONEY_CAP).ifPresent(oldData -> {
                newPlayer.getCapability(ModCapabilities.MONEY_CAP).ifPresent(newData -> {
                    CompoundTag tag = oldData.saveNBTData();
                    newData.loadNBTData(tag);
                });
            });

            originalPlayer.getCapability(ModCapabilities.SPIRIT_ROOT_CAP).ifPresent(oldData -> {
                newPlayer.getCapability(ModCapabilities.SPIRIT_ROOT_CAP).ifPresent(newData -> {
                    CompoundTag tag = oldData.saveNBTData();
                    newData.loadNBTData(tag);
                });
            });

            originalPlayer.getCapability(ModCapabilities.ALLOCATE_ATTRIBUTE_CAP).ifPresent(oldData -> {
                newPlayer.getCapability(ModCapabilities.ALLOCATE_ATTRIBUTE_CAP).ifPresent(newData -> {
                    CompoundTag tag = oldData.saveNBTData();
                    newData.loadNBTData(tag);
                });
            });

            originalPlayer.getCapability(ModCapabilities.REALM_ATTRIBUTE_CAP).ifPresent(oldData -> {
                newPlayer.getCapability(ModCapabilities.REALM_ATTRIBUTE_CAP).ifPresent(newData -> {
                    CompoundTag tag = oldData.saveNBTData();
                    newData.loadNBTData(tag);
                });
            });

            originalPlayer.getCapability(ModCapabilities.EQUIPMENT_ATTRIBUTE_CAP).ifPresent(oldData -> {
                newPlayer.getCapability(ModCapabilities.EQUIPMENT_ATTRIBUTE_CAP).ifPresent(newData -> {
                    CompoundTag tag = oldData.saveNBTData();
                    newData.loadNBTData(tag);
                });
            });

            originalPlayer.getCapability(ModCapabilities.TECHNIQUE_ATTRIBUTE_CAP).ifPresent(oldData -> {
                newPlayer.getCapability(ModCapabilities.TECHNIQUE_ATTRIBUTE_CAP).ifPresent(newData -> {
                    CompoundTag tag = oldData.saveNBTData();
                    newData.loadNBTData(tag);
                });
            });

            originalPlayer.getCapability(ModCapabilities.PLAYER_TOTAL_ATTRIBUTE_CAP).ifPresent(oldData -> {
                newPlayer.getCapability(ModCapabilities.PLAYER_TOTAL_ATTRIBUTE_CAP).ifPresent(newData -> {
                    CompoundTag tag = oldData.saveNBTData();
                    newData.loadNBTData(tag);
                });
            });

        } finally {
            // 清理舊玩家的 Capability
            originalPlayer.invalidateCaps();
        }

        System.out.println("========================================");
    }

    @SubscribeEvent
    public static void onPlayerLoggedIn(PlayerEvent.PlayerLoggedInEvent event) {

        //境界
        if (event.getEntity() instanceof ServerPlayer serverPlayer) {
            serverPlayer.getCapability(ModCapabilities.CULTIVATION_CAP).ifPresent(data -> {
                ModMessages.sendToPlayer(
                        new RealmSyncPacket(
                                data.getRealm().ordinal(),
                                data.getLayer()
                        ),
                        serverPlayer
                );
                System.out.println(">>> 玩家登入時同步資料: " + data.getRealm().getDisplayName() + " " + data.getLayer() + "層");
            });
        }

        //稱號
        if (event.getEntity() instanceof ServerPlayer player) {
            player.getServer().execute(() -> {
                TitleSystem.getInstance().syncAllVisibleTitles(player);
                System.out.println("[TitleSync] 玩家 " + player.getName().getString() + " 登入，已同步所有可見稱號");
            });
        }

        // 成就
        if (event.getEntity() instanceof ServerPlayer player) {
            AchievementManager.grantAchievement(player, CustomAchievements.EXPLORATION_ROOT);
            AchievementManager.grantAchievement(player, CustomAchievements.BUILDING_ROOT);
            AchievementManager.grantAchievement(player, CustomAchievements.COMBAT_ROOT);
        }

        // 信箱
        if  (event.getEntity() instanceof ServerPlayer player) {
            MailboxStorage storage = MailboxStorage.get(player.level());
            List<Mail> mails = storage.getMails(player.getUUID());
            ModMessages.sendToPlayer(new MailSyncPacket(player.getUUID(), mails), player);
        }

    }

    // 追蹤玩家重生事件(用於調試)
    @SubscribeEvent
    public static void onPlayerRespawn(PlayerEvent.PlayerRespawnEvent event) {
        System.out.println("========================================");
        System.out.println("=== PlayerRespawnEvent 觸發 ===");

        if (event.getEntity() instanceof ServerPlayer serverPlayer) {
            // 重生後立即檢查並同步
            serverPlayer.getCapability(ModCapabilities.CULTIVATION_CAP).ifPresent(data -> {
                System.out.println(">>> 重生後數據檢查: " + data.getRealm().getDisplayName() + " " + data.getLayer() + "層");

                ModMessages.sendToPlayer(
                        new RealmSyncPacket(
                                data.getRealm().ordinal(),
                                data.getLayer()
                        ),
                        serverPlayer
                );
            });
        }

        if (event.getEntity() instanceof ServerPlayer player) {
            player.getServer().execute(() -> {
                TitleSystem.getInstance().syncEquippedTitle(player);
                System.out.println("[TitleSync] 玩家 " + player.getName().getString() + " 重生，已同步稱號");
            });
        }

        System.out.println("========================================");
    }

    @SubscribeEvent
    public static void onPlayerChangeDimension(PlayerEvent.PlayerChangedDimensionEvent event) {
        if (event.getEntity() instanceof ServerPlayer serverPlayer) {
            serverPlayer.getCapability(ModCapabilities.CULTIVATION_CAP).ifPresent(data -> {
                ModMessages.sendToPlayer(
                        new RealmSyncPacket(
                                data.getRealm().ordinal(),
                                data.getLayer()
                        ),
                        serverPlayer
                );
                System.out.println(">>> 維度切換時同步資料: "
                        + data.getRealm().getDisplayName() + " " + data.getLayer() + "層");
            });
        }

        if (event.getEntity() instanceof ServerPlayer player) {
            player.getServer().execute(() -> {
                TitleSystem.getInstance().syncEquippedTitle(player);
                System.out.println("[TitleSync] 玩家 " + player.getName().getString() + " 切換維度，已同步稱號");
            });
        }
    }

    @SubscribeEvent
    public static void onPlayerStartTracking(PlayerEvent.StartTracking event) {
        if (event.getTarget() instanceof ServerPlayer targetPlayer &&
                event.getEntity() instanceof ServerPlayer observer) {

            // 把被追蹤玩家的稱號同步給觀察者
            observer.getServer().execute(() -> {
                TitleSystem.getInstance().syncEquippedTitleTo(targetPlayer, observer);
            });
        }
    }

    @SubscribeEvent
    public static void onPlayerLoggedOut(PlayerEvent.PlayerLoggedOutEvent event) {
        // 郵件系統
        if (event.getEntity() instanceof ServerPlayer player) {
            ClientMailCache.clearMails(player.getUUID());
        }

    }

}