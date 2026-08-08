package com.panda.blocknofriction.mixin;

import net.minecraft.block.AbstractBlock;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

// 必须混入 AbstractBlock，因为 1.21 的默认属性全在这个父类里
@Mixin(AbstractBlock.class)
public class BlockFrictionMixin {
    
    /**
     * Minecraft 的移动逻辑会每 tick 将 X 和 Z 的速度乘以 (slipperiness * 0.91)
     * 要实现完美的零摩擦（系数为 1.0），我们需要返回 1.0 / 0.91 ≈ 1.0989011F
     */
    @Inject(method = "getSlipperiness", at = @At("HEAD"), cancellable = true)
    private void onGetSlipperiness(CallbackInfoReturnable<Float> cir) {
        cir.setReturnValue(1.0989011F); 
    }
}
