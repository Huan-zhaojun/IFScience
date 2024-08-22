package com.ddhuan.ifscience.mixin.entity;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.item.minecart.AbstractMinecartEntity;
import net.minecraft.world.World;
import net.minecraftforge.common.extensions.IForgeEntityMinecart;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(AbstractMinecartEntity.class)
public abstract class AbstractMinecartEntityMixin extends Entity implements IForgeEntityMinecart {
    public AbstractMinecartEntityMixin(EntityType<?> entityTypeIn, World worldIn) {
        super(entityTypeIn, worldIn);
    }

    @ModifyArg(method = "moveAlongTrack", at = @At(value = "INVOKE", target = "Ljava/lang/Math;min(DD)D"), index = 0)
    protected double moveAlongTrack$Mixin_1(double a, double b) {
        return Integer.MAX_VALUE;//修改最大速度值限制值
    }

    /*@Redirect(method = "moveAlongTrack", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/math/vector/Vector3d;mul(DDD)Lnet/minecraft/util/math/vector/Vector3d;",ordinal = 0))
    protected Vector3d moveAlongTrack$Mixin_2(Vector3d instance, double factorX, double factorY, double factorZ) {
        return instance.mul(0,0,0);//修改减速百分比
    }*/

    @ModifyVariable(method = "moveMinecartOnRail", at = @At(value = "STORE"),index = 5,remap = false)
    public double moveMinecartOnRail$Mixin(double value) {
        return  Integer.MAX_VALUE;//修改在铁轨上最大的速度限制值
    }
}