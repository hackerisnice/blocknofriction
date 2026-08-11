package com.panda.blocknofriction.mixin;

import com.panda.blocknofriction.BlockNoFriction;
import net.minecraft.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Entity.class)
public abstract class EntityFrictionMixin {

    @Shadow public abstract boolean isOnGround();

    @Inject(method = "getVelocityMultiplier", at = @At("HEAD"), cancellable = true)
    private void onGetVelocityMultiplier(CallbackInfoReturnable<Float> cir) {
        // 仅当指令开启了模式，且实体在地面上时，才应用零摩擦
        if (BlockNoFriction.isNoFrictionEnabled && this.isOnGround()) {
            cir.setReturnValue(1.0989011F);
        }
    }
}
