package com.ddhuan.ifscience.mixin.entity;

import com.ddhuan.ifscience.Custom.MinecartUtil;
import com.ddhuan.ifscience.common.Block.IceRailBlock;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.item.minecart.AbstractMinecartEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;
import net.minecraftforge.common.extensions.IForgeEntityMinecart;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import static com.ddhuan.ifscience.Custom.MinecartUtil.IS_COLLISION_FLY;

@Mixin(AbstractMinecartEntity.class)
public abstract class AbstractMinecartEntityMixin extends Entity implements IForgeEntityMinecart {
    @Shadow(remap = false)
    public abstract void setMaxSpeedAirVertical(float value);

    @Shadow(remap = false)
    public abstract void setMaxSpeedAirLateral(float value);

    @Shadow(remap = false)
    public abstract void setDragAir(double value);

    public AbstractMinecartEntityMixin(EntityType<?> entityTypeIn, World worldIn) {
        super(entityTypeIn, worldIn);
    }

    @Inject(method = "moveDerailedMinecart", at = @At(value = "HEAD"))
    protected void moveDerailedMinecart$Mixin_1(CallbackInfo ci) {
        this.setMaxSpeedAirLateral(Integer.MAX_VALUE);
        this.setMaxSpeedAirVertical(Integer.MAX_VALUE);
        this.setDragAir(0.95f);
    }

    @ModifyArg(method = "moveDerailedMinecart", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/math/vector/Vector3d;scale(D)Lnet/minecraft/util/math/vector/Vector3d;"), index = 0)
    protected double moveDerailedMinecart$Mixin_2(double factor) {
        return 0.5D;
    }

    @ModifyArg(method = "moveAlongTrack", at = @At(value = "INVOKE", target = "Ljava/lang/Math;min(DD)D"), index = 0)
    protected double moveAlongTrack$Mixin_1(double a, double b) {
        return MinecartUtil.MAX_SPEED/*Integer.MAX_VALUE*/;//修改在铁轨上最大速度值限制值
    }

    @Redirect(method = "moveAlongTrack", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/math/vector/Vector3d;mul(DDD)Lnet/minecraft/util/math/vector/Vector3d;", ordinal = 0))
    protected Vector3d moveAlongTrack$Mixin_2(Vector3d instance, double factorX, double factorY, double factorZ) {
        return instance.mul(0.5D, 0, 0.5D);//修改减速百分比
    }

    @Inject(method = "moveAlongTrack", at = @At(value = "TAIL"))
    protected void moveAlongTrack$Mixin_3(BlockPos pos, BlockState state, CallbackInfo ci) {
        if (state.getBlock() instanceof IceRailBlock && state.get(IceRailBlock.FROZEN) && shouldDoRailFunctions()) {
            Vector3d vec = this.getMotion();
            double dd = Math.sqrt(horizontalMag(vec));
            if (dd > 0.01D) {
                double slowFactor = MinecartUtil.SLOW_RATIO; //这个值可以根据需要调整，越小减速越明显
                this.setMotion(vec.mul(slowFactor, 1.0D, slowFactor));
                getPassengers().forEach(entity -> {
                    entity.setMotion(entity.getMotion().mul(slowFactor, 1.0D, slowFactor));
                });
            } else {
                this.setMotion(vec.mul(0, 1.0D, 0));
                getPassengers().forEach(entity -> {
                    entity.setMotion(entity.getMotion().mul(0, 1.0D, 0));
                });
            }
        }
    }

    @ModifyVariable(method = "moveMinecartOnRail", at = @At(value = "STORE"), index = 5, remap = false)
    public double moveMinecartOnRail$Mixin(double value) {
        return MinecartUtil.MAX_SPEED/*Integer.MAX_VALUE*/;//修改矿车最大速度限制值
    }


    @Inject(method = "canCollide", at = @At(value = "HEAD"), cancellable = true)
    public void canCollide(Entity entity, CallbackInfoReturnable<Boolean> cir) {
        Vector3d speedVec = this.getMotion();
        if (IS_COLLISION_FLY && speedVec.length() >= 0.75) cir.setReturnValue(false);//取消矿车行驶时与生物的碰撞
    }

    @Inject(method = "tick", at = @At(value = "HEAD"))
    public void tick$Mixin1(CallbackInfo ci) {
        Vector3d speedVec = this.getMotion();
        //高速行驶会撞飞玩家和生物
        if (IS_COLLISION_FLY && speedVec.length() >= 0.75) MinecartUtil.knockEntity(speedVec,(AbstractMinecartEntity) (Object)this);
    }

    @Redirect(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/Entity;applyEntityCollision(Lnet/minecraft/entity/Entity;)V", ordinal = 0))
    public void tick$Mixin2_applyEntityCollision(Entity instance, Entity entityIn) {
        //用于取消矿车原版碰撞以适应撞飞玩法
        Vector3d speedVec = this.getMotion();
        if (speedVec.length() < 0.75) instance.applyEntityCollision(this);
        else if (speedVec.length() >= 0.75 && IS_COLLISION_FLY) MinecartUtil.knockEntity(speedVec,(AbstractMinecartEntity) (Object)this);
    }
}