package com.blacksnow1002.realmmod.common.registry.block;

import com.blacksnow1002.realmmod.RealmMod;
import com.blacksnow1002.realmmod.system.profession.alchemy.block.AlchemyToolBlock;
import com.blacksnow1002.realmmod.system.profession.alchemy.block.AlchemyToolBlockEntity;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

public class ModBlockEntities {
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES =
            DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE, RealmMod.MOD_ID);

    public static final RegistryObject<BlockEntityType<AlchemyToolBlockEntity>> ALCHEMY_TOOL_BE =
            BLOCK_ENTITIES.register("alchemy_tool_be", () ->
                    BlockEntityType.Builder.of(
                            (pos, state) -> new AlchemyToolBlockEntity(pos, state,
                                    AlchemyToolBlock.Grades.MORTAL, 1),
                            // 在這裡添加所有使用此BlockEntity的方塊
                            ModBlocks.HEAVEN_ALCHEMY_9_TOOL.get(),
                            ModBlocks.EARTH_ALCHEMY_9_TOOL.get(),
                            ModBlocks.MYSTIC_ALCHEMY_9_TOOL.get(),
                            ModBlocks.MORTAL_ALCHEMY_9_TOOL.get()
                    ).build(null));

    public static void register(IEventBus eventBus) {
        BLOCK_ENTITIES.register(eventBus);
    }
}