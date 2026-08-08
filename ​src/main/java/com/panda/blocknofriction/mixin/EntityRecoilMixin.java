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

    /**
     * 核心修复：分离玩家与怪物的物理计算端
     */
    private boolean shouldApplyPhysics() {
        boolean isClient = this.getWorld().isClient();
        boolean isPlayer = (Object)this instanceof PlayerEntity;
        
        if (isClient) {
            // 在客户端，只允许本地玩家（ClientPlayerEntity）产生反冲，防止联机时别的玩家抽搐
            return isPlayer && this.getClass().getSimpleName().equals("ClientPlayerEntity");
        } else {
            // 在服务端，只处理非玩家实体（怪物、动物）的反冲。玩家交由客户端计算后发包同步。
            return !isPlayer;
        }
    }

    /**
     * 捕获挥手动作（包括挥剑攻击、长按左键持续采矿）。
     * 施加视线相反方向的推力。
     */
    @Inject(method = "swingHand(Lnet/minecraft/util/Hand;Z)V", at = @At("HEAD"))
    private void applySwingRecoil(Hand hand, boolean fromServerPlayer, CallbackInfo ci) {
        if (!shouldApplyPhysics()) return;
        
        Vec3d lookVec = this.getRotationVector();
        double recoilForce = 0.6; // 太空枪反冲力，根据手感可调大
        
        // 给实体添加相反方向的速度
        this.addVelocity(-lookVec.x * recoilForce, -lookVec.y * recoilForce, -lookVec.z * recoilForce);
        this.velocityModified = true;
    }

    /**
     * 捕获受击动作。被攻击时产生被击飞的矢量反冲力。
     */
    @Inject(method = "damage", at = @At("RETURN"))
    private void applyDamageRecoil(DamageSource source, float amount, CallbackInfoReturnable<Boolean> cir) {
        // 如果伤害判定成功生效
        if (cir.getReturnValue() && shouldApplyPhysics()) {
            Vec3d sourcePos = source.getPosition();
            if (sourcePos != null) {
                // 计算从伤害来源指向受击者的向量
                Vec3d pushVec = this.getPos().subtract(sourcePos).normalize();
                double knockbackForce = 1.2; // 受击击退力
                
                this.addVelocity(pushVec.x * knockbackForce, pushVec.y * knockbackForce, pushVec.z * knockbackForce);
                this.velocityModified = true;
            }
        }
    }
}
