package com.blacksnow1002.realmmod.entity;

import com.blacksnow1002.realmmod.RealmMod;
import com.mojang.authlib.GameProfile;
import net.minecraft.core.UUIDUtil;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

import java.util.Optional;
import java.util.UUID;

/**
 * 玩家分身實體 - 具有玩家外觀的戰鬥夥伴
 */
public class PlayerCloneEntity extends PathfinderMob {

    // 使用 String 來同步 GameProfile 資料
    private static final EntityDataAccessor<String> DATA_PROFILE_NAME =
            SynchedEntityData.defineId(PlayerCloneEntity.class, EntityDataSerializers.STRING);

    private static final EntityDataAccessor<Optional<UUID>> DATA_PROFILE_UUID =
            SynchedEntityData.defineId(PlayerCloneEntity.class, EntityDataSerializers.OPTIONAL_UUID);

    private static final EntityDataAccessor<Optional<UUID>> DATA_OWNER_UUID =
            SynchedEntityData.defineId(PlayerCloneEntity.class, EntityDataSerializers.OPTIONAL_UUID);

    private GameProfile cachedProfile;
    private ServerPlayer cachedOwner;
    private int ownerCacheTime;

    public PlayerCloneEntity(EntityType<? extends PathfinderMob> type, Level level) {
        super(type, level);
        this.setCustomNameVisible(true);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return PathfinderMob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 20.0D)
                .add(Attributes.MOVEMENT_SPEED, 0.35D)  // 增加移動速度 (原本 0.3D)
                .add(Attributes.ATTACK_DAMAGE, 2.0D)
                .add(Attributes.FOLLOW_RANGE, 32.0D)
                .add(Attributes.KNOCKBACK_RESISTANCE, 0.3D);
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(DATA_PROFILE_NAME, "");
        builder.define(DATA_PROFILE_UUID, Optional.empty());
        builder.define(DATA_OWNER_UUID, Optional.empty());

        System.out.println("[PlayerClone] 數據同步器已定義");
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(1, new FloatGoal(this));
        this.goalSelector.addGoal(2, new MeleeAttackGoal(this, 1.8D, false));  // 大幅增加攻擊速度
        this.goalSelector.addGoal(3, new FollowOwnerGoal(this, 1.8D, 10.0F, 3.0F));  // 大幅增加跟隨速度
        this.goalSelector.addGoal(7, new WaterAvoidingRandomStrollGoal(this, 1.5D));  // 增加閒逛速度
        this.goalSelector.addGoal(8, new LookAtPlayerGoal(this, Player.class, 8.0F));
        this.goalSelector.addGoal(9, new RandomLookAroundGoal(this));

        this.targetSelector.addGoal(1, new CopyOwnerTargetGoal(this));
        this.targetSelector.addGoal(2, new DefendOwnerGoal(this));
    }

    /**
     * 設置主人和外觀
     */
    public void setOwner(ServerPlayer owner) {
        this.cachedOwner = owner;
        GameProfile profile = owner.getGameProfile();

        System.out.println("[PlayerClone] 設置主人: " + profile.getName());

        this.entityData.set(DATA_OWNER_UUID, Optional.of(owner.getUUID()));
        this.entityData.set(DATA_PROFILE_NAME, profile.getName());
        this.entityData.set(DATA_PROFILE_UUID, Optional.of(profile.getId()));

        this.cachedProfile = profile;

        // 設置顯示名稱
        this.setCustomName(net.minecraft.network.chat.Component.translatable(
                "§name.realmmod.entity.player_clone",
                owner.getName().getString()
        ));

        System.out.println("[PlayerClone] 主人設置完成，Profile: " + profile.getName() + ", UUID: " + profile.getId());
    }

    /**
     * 獲取主人
     */
    public ServerPlayer getOwner() {
        if (this.cachedOwner != null && !this.cachedOwner.isRemoved()) {
            return this.cachedOwner;
        }

        Optional<UUID> ownerUUID = this.entityData.get(DATA_OWNER_UUID);
        if (ownerUUID.isPresent() && this.level() instanceof net.minecraft.server.level.ServerLevel serverLevel) {
            this.cachedOwner = serverLevel.getServer().getPlayerList().getPlayer(ownerUUID.get());
            return this.cachedOwner;
        }

        return null;
    }

    /**
     * 獲取玩家資料（用於渲染）
     */
    public GameProfile getGameProfile() {
        if (this.cachedProfile != null) {
            return this.cachedProfile;
        }

        String name = this.entityData.get(DATA_PROFILE_NAME);
        Optional<UUID> uuid = this.entityData.get(DATA_PROFILE_UUID);

        if (!name.isEmpty() && uuid.isPresent()) {
            this.cachedProfile = new GameProfile(uuid.get(), name);
            System.out.println("[PlayerClone] 客戶端獲取 GameProfile: " + name + ", UUID: " + uuid.get());
            return this.cachedProfile;
        }

        System.out.println("[PlayerClone] 警告：無法獲取 GameProfile！name=" + name + ", uuid=" + uuid);
        return null;
    }

    @Override
    public void tick() {
        super.tick();

        // 只在伺服器端檢查 owner（客戶端無法獲取 ServerPlayer）
        if (!this.level().isClientSide) {
            ServerPlayer owner = getOwner();
            if (owner == null || !owner.isAlive() || owner.hasDisconnected()) {
                this.discard();
                return;
            }

            // 距離過遠時傳送回主人身邊
            if (this.distanceToSqr(owner) > 900) {
                Vec3 ownerPos = owner.position();
                this.teleportTo(ownerPos.x, ownerPos.y, ownerPos.z);
            }
        }
    }

    @Override
    public boolean hurt(DamageSource source, float amount) {
        // 不受主人傷害
        if (source.getEntity() == getOwner()) {
            return false;
        }
        return super.hurt(source, amount);
    }

    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);

        Optional<UUID> ownerUUID = this.entityData.get(DATA_OWNER_UUID);
        if (ownerUUID.isPresent()) {
            tag.putUUID("OwnerUUID", ownerUUID.get());
        }

        String profileName = this.entityData.get(DATA_PROFILE_NAME);
        Optional<UUID> profileUUID = this.entityData.get(DATA_PROFILE_UUID);

        if (!profileName.isEmpty() && profileUUID.isPresent()) {
            CompoundTag profileTag = new CompoundTag();
            profileTag.putString("Name", profileName);
            profileTag.putUUID("Id", profileUUID.get());
            tag.put("GameProfile", profileTag);
        }
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);

        if (tag.hasUUID("OwnerUUID")) {
            this.entityData.set(DATA_OWNER_UUID, Optional.of(tag.getUUID("OwnerUUID")));
        }

        if (tag.contains("GameProfile", Tag.TAG_COMPOUND)) {
            CompoundTag profileTag = tag.getCompound("GameProfile");
            String name = profileTag.getString("Name");
            UUID uuid = profileTag.getUUID("Id");

            this.entityData.set(DATA_PROFILE_NAME, name);
            this.entityData.set(DATA_PROFILE_UUID, Optional.of(uuid));
            this.cachedProfile = new GameProfile(uuid, name);
        }
    }

    /**
     * 跟隨主人 AI
     */
    private static class FollowOwnerGoal extends Goal {
        private final PlayerCloneEntity clone;
        private final double speedModifier;
        private final float maxDistance;
        private final float minDistance;

        public FollowOwnerGoal(PlayerCloneEntity clone, double speed, float maxDist, float minDist) {
            this.clone = clone;
            this.speedModifier = speed;
            this.maxDistance = maxDist;
            this.minDistance = minDist;
        }

        @Override
        public boolean canUse() {
            ServerPlayer owner = clone.getOwner();
            if (owner == null || !owner.isAlive()) return false;

            double distance = clone.distanceTo(owner);
            return distance > minDistance && clone.getTarget() == null;
        }

        @Override
        public void tick() {
            ServerPlayer owner = clone.getOwner();
            if (owner == null) return;

            clone.getLookControl().setLookAt(owner, 10.0F, clone.getMaxHeadXRot());

            if (--clone.ownerCacheTime <= 0) {
                clone.ownerCacheTime = 5;  // 更頻繁更新（原本10）
                double distance = clone.distanceTo(owner);

                if (distance > maxDistance) {
                    // 遠距離：極速追趕
                    clone.getNavigation().moveTo(owner, speedModifier * 2.5);
                } else if (distance > minDistance) {
                    // 中距離：快速跟隨
                    clone.getNavigation().moveTo(owner, speedModifier * 1.8);
                }
            }
        }
    }

    /**
     * 複製主人攻擊目標 AI
     */
    private static class CopyOwnerTargetGoal extends Goal {
        private final PlayerCloneEntity clone;

        public CopyOwnerTargetGoal(PlayerCloneEntity clone) {
            this.clone = clone;
        }

        @Override
        public boolean canUse() {
            ServerPlayer owner = clone.getOwner();
            if (owner == null) return false;

            LivingEntity target = owner.getLastHurtMob();
            return target != null && target.isAlive()
                    && target != clone && clone.distanceTo(target) < 20;
        }

        @Override
        public void start() {
            ServerPlayer owner = clone.getOwner();
            if (owner != null) {
                LivingEntity target = owner.getLastHurtMob();
                if (target != null && target.isAlive()) {
                    clone.setTarget(target);
                }
            }
        }
    }

    /**
     * 保護主人 AI
     */
    private static class DefendOwnerGoal extends Goal {
        private final PlayerCloneEntity clone;

        public DefendOwnerGoal(PlayerCloneEntity clone) {
            this.clone = clone;
        }

        @Override
        public boolean canUse() {
            ServerPlayer owner = clone.getOwner();
            if (owner == null) return false;

            LivingEntity attacker = owner.getLastHurtByMob();
            return attacker != null && attacker.isAlive()
                    && attacker != clone && clone.distanceTo(attacker) < 20;
        }

        @Override
        public void start() {
            ServerPlayer owner = clone.getOwner();
            if (owner != null) {
                LivingEntity attacker = owner.getLastHurtByMob();
                if (attacker != null && attacker.isAlive()) {
                    clone.setTarget(attacker);
                }
            }
        }
    }
}