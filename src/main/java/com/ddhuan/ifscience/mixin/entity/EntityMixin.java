package com.ddhuan.ifscience.mixin.entity;

import net.minecraft.entity.Entity;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Entity.class)
public abstract class EntityMixin {
    @Shadow
    public World world;

    @Shadow
    public abstract boolean isBurning();

    @Shadow
    public abstract boolean isSpectator();

    @Unique
    private long lastTime_FireRender = System.currentTimeMillis();
    @Unique
    public boolean fireRender = false;//仅客户端用于渲染火焰：屏幕火焰和生物火焰

    @Inject(method = "canRenderOnFire", at = @At("RETURN"), cancellable = true)
    @OnlyIn(Dist.CLIENT)
    public void canRenderOnFire(CallbackInfoReturnable<Boolean> cir) {
        cir.setReturnValue((cir.getReturnValueZ()) || fireRender);//返回一个值可用于生物渲染器渲染火焰附加在生物上
    }

    @Inject(method = "tick", at = @At("HEAD"))
    public void tick(CallbackInfo ci) {
        if (this.world.isRemote) {
            if (System.currentTimeMillis() - lastTime_FireRender >= 500) {
                fireRender = false;
                lastTime_FireRender = System.currentTimeMillis();
            }
        }
    }
}
