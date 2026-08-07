package com.panda.blocknofriction.mixin;

import net.minecraft.block.Block;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Block.class)
public class BlockFrictionMixin {
    
    /**
     * Minecraft 的运动计算公式中，每 tick 速度衰减乘数为: 0.91 * slipperiness
     * 为了让速度不衰减 (乘数为 1.0)，我们需要返回 1.0 / 0.91 ≈ 1.0989F
     */
    @Inject(method = "getSlipperiness", at = @At("HEAD"), cancellable = true)
    private void onGetSlipperiness(CallbackInfoReturnable<Float> cir) {
        cir.setReturnValue(1.0989011F); 
    }
}
