package com.blacksnow1002.realmmod.assignment.npc;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

public class CustomNPCEntity extends Villager {

    private String npcId;

    public CustomNPCEntity(EntityType<CustomNPCEntity> entityType, Level level) {
        super(entityType, level);
        this.npcId = "";
    }

    @Override
    public InteractionResult mobInteract(Player player, InteractionHand hand) {
        System.out.println("[Entity] mobInteract 被呼叫，isClientSide: " + this.level().isClientSide);
        if (this.level().isClientSide) return InteractionResult.SUCCESS;

        if (player instanceof ServerPlayer serverPlayer) {
            BaseNPC npc = NPCRegistry.getInstance().getNPC(npcId);
            if (npc != null) {
                System.out.println("[Entity] NPC 邏輯執行");
                npc.onInteract(serverPlayer);
                return InteractionResult.SUCCESS;
            }
        }
        return InteractionResult.FAIL;
    }

    @Override
    protected void registerGoals() {
        // 不添加任何 AI goal，NPC 就不會移動
        super.registerGoals();
        // 移除所有移動相關的 goal
    }

    @Override
    public void aiStep() {
        // 不調用 super，NPC 就不會更新 AI
    }

    @Override
    public boolean canBeLeashed() {
        return false; // 不能被拴住
    }

    @Override
    public boolean hurt(DamageSource source, float amount) {
        return false; // 不能被傷害
    }

    @Override
    public void die(DamageSource source) {
        // 不調用 super，NPC 不會死亡
    }

    /**
     * 設置 NPC ID
     */
    public void setNpcId(String npcId) {
        this.npcId = npcId;
    }

    /**
     * 獲取 NPC ID
     */
    public String getNpcId() {
        return npcId;
    }

    /**
     * 保存 NBT 數據
     */
    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        if (npcId != null) {
            tag.putString("NpcId", npcId);
        }
    }

    /**
     * 加載 NBT 數據
     */
    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        if (tag.contains("NpcId")) {
            this.npcId = tag.getString("NpcId");
        }
    }
}