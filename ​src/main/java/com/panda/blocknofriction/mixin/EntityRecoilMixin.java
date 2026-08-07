package com.panda.blocknofriction.mixin;

import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.util.Hand;
import net.minecraft.util.math.Vec3d;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LivingEntity.class)
public abstract class EntityRecoilMixin extends Entity {

    public EntityRecoilMixin(net.minecraft.entity.EntityType<?> type, net.minecraft.world.World world) {
        super(type, world);
    }

    /**
     * 捕获挥手动作（采矿、挥剑等）。
     * 提供与视线相反的矢量反冲力。
     */
    @Inject(method = "swingHand(Lnet/minecraft/util/Hand;Z)V", at = @At("HEAD"))
    private void applySwingRecoil(Hand hand, boolean fromServerPlayer, CallbackInfo ci) {
        if (this.getWorld().isClient) return; // 让服务端处理物理以避免严重的脱步发抖
        
        Vec3d lookVec = this.getRotationVector();
        // 反冲力大小，可根据需求调节
        double recoilForce = 0.25; 
        
        // 施加相反方向的力
        this.addVelocity(-lookVec.x * recoilForce, -lookVec.y * recoilForce, -lookVec.z * recoilForce);
        this.velocityModified = true;
    }

    /**
     * 捕获受击动作。
     * 根据伤害来源的坐标生成被击飞的反冲力。
     */
    @Inject(method = "damage", at = @At("RETURN"))
    private void applyDamageRecoil(DamageSource source, float amount, CallbackInfoReturnable<Boolean> cir) {
        // 仅当伤害成功应用时触发
        if (cir.getReturnValue() && !this.getWorld().isClient) {
            Vec3d sourcePos = source.getPosition();
            if (sourcePos != null) {
                // 计算从伤害源指向受击者的标准化向量
                Vec3d pushVec = this.getPos().subtract(sourcePos).normalize();
                
                // 受击的反作用力通常更大
                double knockbackForce = 0.6;
                
                this.addVelocity(pushVec.x * knockbackForce, pushVec.y * knockbackForce, pushVec.z * knockbackForce);
                this.velocityModified = true;
            }
        }
    }
}
