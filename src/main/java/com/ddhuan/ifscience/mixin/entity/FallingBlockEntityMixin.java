package com.ddhuan.ifscience.mixin.entity;

import com.ddhuan.ifscience.Custom.AnvilUtil;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.item.FallingBlockEntity;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(FallingBlockEntity.class)
public abstract class FallingBlockEntityMixin extends Entity {
    public FallingBlockEntityMixin(EntityType<?> entityTypeIn, World worldIn) {
        super(entityTypeIn, worldIn);
    }

    @Shadow
    private BlockState fallTile;

    @Inject(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/item/FallingBlockEntity;getPosition()Lnet/minecraft/util/math/BlockPos;", ordinal = 1, shift = At.Shift.AFTER))
    public void tick(CallbackInfo ci) {
        AnvilUtil.destroyBlock(fallTile,world,this.getPosition().add(0,-0.1,0));
    }
}
