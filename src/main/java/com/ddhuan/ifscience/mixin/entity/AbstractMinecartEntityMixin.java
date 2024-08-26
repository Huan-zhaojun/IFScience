package com.ddhuan.ifscience.mixin.entity;

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
        return Integer.MAX_VALUE;//修改在铁轨上最大速度值限制值
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
                double slowFactor = 0.6D; // 这个值可以根据需要调整，越小减速越明显
                this.setMotion(vec.mul(slowFactor, 1.0D, slowFactor)); // 减少 X 和 Z 轴上的速度
            } else this.setMotion(vec.mul(0, 1.0D, 0));
        }
    }

    @ModifyVariable(method = "moveMinecartOnRail", at = @At(value = "STORE"), index = 5, remap = false)
    public double moveMinecartOnRail$Mixin(double value) {
        return Integer.MAX_VALUE;//修改在铁轨上最大的速度限制值
    }
}