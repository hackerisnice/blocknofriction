package com.panda.blocknofriction.mixin;

import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Hand;
import net.minecraft.util.math.Vec3d;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LivingEntity.class)
public abstract class EntityRecoilMixin extends Entity {

    public EntityRecoilMixin(net.minecraft.entity.EntityType<?> type, net.minecraft.world.World world) {
        super(type, world);
    }

    private boolean shouldApplyPhysics() {
        boolean isClient = this.getWorld().isClient();
        boolean isPlayer = (Object)this instanceof PlayerEntity;
        
        if (isClient) {
            return isPlayer && this.getClass().getSimpleName().equals("ClientPlayerEntity");
        } else {
            return !isPlayer;
        }
    }

    @Inject(method = "swingHand(Lnet/minecraft/util/Hand;Z)V", at = @At("HEAD"))
    private void applySwingRecoil(Hand hand, boolean fromServerPlayer, CallbackInfo ci) {
        if (!shouldApplyPhysics()) return;
        
        Vec3d lookVec = this.getRotationVector();
        // 【关键调整】把 0.6 降到了 0.15。
        // 这样即使你朝下疯狂按左键，向上的推力也不足以让你克服重力飞起来。
        double recoilForce = 0.15; 
        
        this.addVelocity(-lookVec.x * recoilForce, -lookVec.y * recoilForce, -lookVec.z * recoilForce);
        this.velocityModified = true;
    }

    @Inject(method = "damage", at = @At("RETURN"))
    private void applyDamageRecoil(DamageSource source, float amount, CallbackInfoReturnable<Boolean> cir) {
        if (cir.getReturnValue() && shouldApplyPhysics()) {
            Vec3d sourcePos = source.getPosition();
            if (sourcePos != null) {
                Vec3d pushVec = this.getPos().subtract(sourcePos).normalize();
                // 受击时的推力也稍微调小
                double knockbackForce = 0.4; 
                
                this.addVelocity(pushVec.x * knockbackForce, pushVec.y * knockbackForce, pushVec.z * knockbackForce);
                this.velocityModified = true;
            }
        }
    }
}
