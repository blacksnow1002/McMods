package com.blacksnow1002.realmmod.Mixin;

import com.blacksnow1002.realmmod.util.ModTags;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(BlockBehaviour.BlockStateBase.class)
public abstract class BlockStateBaseMixin {

    @Shadow
    public abstract BlockState asState();

    @Inject(method = "getRenderShape", at = @At("HEAD"), cancellable = true)
    private void onGetRenderShape(CallbackInfoReturnable<RenderShape> cir) {
        BlockState state = this.asState();

        if (state.is(ModTags.Blocks.SPIRITUAL_VISIBLE)) {
            Minecraft mc = Minecraft.getInstance();

            if (mc.player != null) {
                boolean hasLingMu = mc.player.getPersistentData().getBoolean("LingMuActive");

                if (!hasLingMu) {
                    cir.setReturnValue(RenderShape.INVISIBLE);
                }
            }
        }
    }

    @Inject(method = "getVisualShape", at = @At("HEAD"), cancellable = true)
    private void onGetVisualShape(BlockGetter level, BlockPos pos, CollisionContext context, CallbackInfoReturnable<VoxelShape> cir) {
        BlockState state = this.asState();

        if (state.is(ModTags.Blocks.SPIRITUAL_VISIBLE)) {
            Minecraft mc = Minecraft.getInstance();

            if (mc.player != null) {
                boolean hasLingMu = mc.player.getPersistentData().getBoolean("LingMuActive");

                if (!hasLingMu) {
                    cir.setReturnValue(Shapes.empty());
                }
            }
        }
    }

    @Inject(method = "skipRendering", at = @At("HEAD"), cancellable = true)
    private void onSkipRendering(BlockState adjacentState, Direction direction, CallbackInfoReturnable<Boolean> cir) {
        BlockState state = this.asState();

        if (adjacentState.is(ModTags.Blocks.SPIRITUAL_VISIBLE)) {
            Minecraft mc = Minecraft.getInstance();

            if (mc.player != null) {
                boolean hasLingMu = mc.player.getPersistentData().getBoolean("LingMuActive");

                if (!hasLingMu) {
                    cir.setReturnValue(false);
                }
            }
        }

        if (state.is(ModTags.Blocks.SPIRITUAL_VISIBLE)) {
            Minecraft mc = Minecraft.getInstance();

            if (mc.player != null) {
                boolean hasLingMu = mc.player.getPersistentData().getBoolean("LingMuActive");

                if (!hasLingMu) {
                    cir.setReturnValue(true);
                }
            }
        }
    }

    @Inject(method = "propagatesSkylightDown", at = @At("HEAD"), cancellable = true)
    private void onPropagatesSkylightDown(BlockGetter level, BlockPos pos, CallbackInfoReturnable<Boolean> cir) {
        BlockState state = this.asState();

        if (state.is(ModTags.Blocks.SPIRITUAL_VISIBLE)) {
            Minecraft mc = Minecraft.getInstance();

            if (mc.player != null) {
                boolean hasLingMu = mc.player.getPersistentData().getBoolean("LingMuActive");

                if (!hasLingMu) {
                    cir.setReturnValue(true);
                }
            }
        }
    }

    @Inject(method = "getLightBlock", at = @At("HEAD"), cancellable = true)
    private void onGetLightBlock(BlockGetter level, BlockPos pos, CallbackInfoReturnable<Integer> cir) {
        BlockState state = this.asState();

        if (state.is(ModTags.Blocks.SPIRITUAL_VISIBLE)) {
            Minecraft mc = Minecraft.getInstance();

            if (mc.player != null) {
                boolean hasLingMu = mc.player.getPersistentData().getBoolean("LingMuActive");

                if (!hasLingMu) {
                    cir.setReturnValue(0);
                }
            }
        }
    }

    @Inject(method = "getShadeBrightness", at = @At("HEAD"), cancellable = true)
    private void onGetShadeBrightness(BlockGetter level, BlockPos pos, CallbackInfoReturnable<Float> cir) {
        BlockState state = this.asState();

        if (state.is(ModTags.Blocks.SPIRITUAL_VISIBLE)) {
            Minecraft mc = Minecraft.getInstance();

            if (mc.player != null) {
                boolean hasLingMu = mc.player.getPersistentData().getBoolean("LingMuActive");

                if (!hasLingMu) {
                    cir.setReturnValue(1.0F);
                }
            }
        }
    }

    // 移除所有 getShape、getCollisionShape、getOcclusionShape 的 Mixin
    // 因為在 1.21.1 版本中這些方法簽名可能不穩定
}