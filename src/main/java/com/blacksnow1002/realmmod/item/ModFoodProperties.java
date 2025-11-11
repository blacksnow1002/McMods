package com.blacksnow1002.realmmod.item;

import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.food.FoodProperties;

public class ModFoodProperties {
    public static final FoodProperties SPIRIT_FRUIT = new FoodProperties.Builder().nutrition(0).saturationModifier(0)
            .fast()
            .effect(new MobEffectInstance(MobEffects.GLOWING, 20, 0), 1.0F)
            .alwaysEdible()
            .build();

    public static final FoodProperties ELIXIR = new FoodProperties.
            Builder().
            nutrition(0).
            saturationModifier(0).
            fast().
            effect(new MobEffectInstance(MobEffects.GLOWING, 20, 0), 1.0F).
            alwaysEdible().
            build();
}