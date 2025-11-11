//package com.blacksnow1002.realmmod.item.custom.base;
//
//import com.blacksnow1002.realmmod.item.ModDataComponents;
//import com.blacksnow1002.realmmod.item.custom.ReforgeMainElementItem;
//import net.minecraft.network.chat.Component;
//import net.minecraft.world.entity.EquipmentSlotGroup;
//import net.minecraft.world.entity.LivingEntity;
//import net.minecraft.world.entity.ai.attributes.AttributeModifier;
//import net.minecraft.world.entity.ai.attributes.Attributes;
//import net.minecraft.world.entity.player.Player;
//import net.minecraft.world.item.Item;
//import net.minecraft.world.item.ItemStack;
//import net.minecraft.world.item.TooltipFlag;
//import net.minecraft.world.item.component.ItemAttributeModifiers;
//import net.minecraft.world.level.Level;
//
//import java.util.List;
//import java.util.UUID;
//
//public abstract class BaseWeaponItem extends Item {
//    public BaseWeaponItem(Properties properties) {
//        super(properties.stacksTo(1));
//    }
//
//    protected abstract float getBaseAttackSpeed();
//
//    protected abstract float getBaseDamage();
//
//    protected abstract String getBaseDamageType();
//
//    protected abstract int getBaseDurability(); // 耐久度 0為無限
//
//
//    @Override
//    public ItemStack getDefaultInstance() {
//        ItemStack stack = new ItemStack(this);
//
//        ItemAttributeModifiers.Builder builder = ItemAttributeModifiers.builder();
//
//        double attackSpeedModifier = getBaseAttackSpeed() - 4.0;
//        builder.add(
//                Attributes.ATTACK_SPEED,
//                new AttributeModifier(
//                        UUID.randomUUID(),
//                        attackSpeedModifier,
//                        AttributeModifier.Operation.ADD_VALUE
//                ), EquipmentSlotGroup.MAINHAND
//        );
//
//        builder.add(
//                Attributes.ATTACK_DAMAGE,
//                new AttributeModifier(
//                        UUID.randomUUID(),
//                        getBaseAttackDamage(),
//                        AttributeModifier.Operation.ADD_VALUE
//                ),
//                EquipmentSlotGroup.MAINHAND
//        );
//    }
//}
