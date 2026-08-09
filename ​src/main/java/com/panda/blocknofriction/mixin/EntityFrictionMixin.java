package com.panda.blocknofriction.mixin;

import net.minecraft.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Entity.class)
public abstract class EntityFrictionMixin {

    @Shadow public abstract boolean isOnGround();

    /**
     * 拦截实体获取“地表摩擦系数”的方法。这是 1.21 最根源的摩擦力入口。
     * MC 每 tick 会把速度乘以 (slipperiness * 0.91)。
     * 我们返回 1.0989011F (即 1.0 / 0.91)，乘法抵消后系数刚好是 1.0。
     * 这样只要你在地上，水平速度就不会衰减（零摩擦），同时不影响重力！
     */
    @Inject(method = "getVelocityMultiplier", at = @At("HEAD"), cancellable = true)
    private void onGetVelocityMultiplier(CallbackInfoReturnable<Float> cir) {
        if (this.isOnGround()) {
            cir.setReturnValue(1.0989011F);
        }
    }
}
