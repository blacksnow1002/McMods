package com.blacksnow1002.realmmod.client.renderer;

import com.blacksnow1002.realmmod.assignment.npc.CustomNPCEntity;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.VillagerRenderer;
import net.minecraft.client.model.VillagerModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.resources.ResourceLocation;

public class CustomNPCRenderer extends VillagerRenderer {
    public CustomNPCRenderer(EntityRendererProvider.Context context) {
        super(context);
    }
}

