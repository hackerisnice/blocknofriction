package com.panda.blocknofriction.mixin;

import com.panda.blocknofriction.BlockNoFriction;
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
        // 【新增】如果未开启该模式，直接跳过计算
        if (!BlockNoFriction.isNoFrictionEnabled) return; 
        
        if (!shouldApplyPhysics()) return;
        
        Vec3d lookVec = this.getRotationVector();
        double recoilForce = 0.15; 
        
        this.addVelocity(-lookVec.x * recoilForce, -lookVec.y * recoilForce, -lookVec.z * recoilForce);
        this.velocityModified = true;
    }

    @Inject(method = "damage", at = @At("RETURN"))
    private void applyDamageRecoil(DamageSource source, float amount, CallbackInfoReturnable<Boolean> cir) {
        // 【新增】如果未开启该模式，直接跳过计算
        if (!BlockNoFriction.isNoFrictionEnabled) return; 
        
        if (cir.getReturnValue() && shouldApplyPhysics()) {
            Vec3d sourcePos = source.getPosition();
            if (sourcePos != null) {
                Vec3d pushVec = this.getPos().subtract(sourcePos).normalize();
                double knockbackForce = 0.4; 
                
                this.addVelocity(pushVec.x * knockbackForce, pushVec.y * knockbackForce, pushVec.z * knockbackForce);
                this.velocityModified = true;
            }
        }
    }
}
