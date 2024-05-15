package com.ddhuan.ifscience.mixin.client.renderer;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.client.renderer.OverlayRenderer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.lang.reflect.Field;

//玩家摸熔炉烫渲染屏幕火焰效果的
@OnlyIn(Dist.CLIENT)
@Mixin(OverlayRenderer.class)
public abstract class OverlayRendererMixin {
    @Inject(method = "renderOverlays", at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/systems/RenderSystem;enableAlphaTest()V"
            , shift = At.Shift.BEFORE))
    private static void renderOverlays(Minecraft minecraftIn, MatrixStack matrixStackIn, CallbackInfo ci) {
        ClientPlayerEntity player = Minecraft.getInstance().player;
        if (player != null) {
            try {
                Field fireRender = ClientPlayerEntity.class.getField("fireRender");
                if ((boolean) fireRender.get(player) && !player.isCreative() && !net.minecraftforge.event.ForgeEventFactory.renderFireOverlay(player, matrixStackIn))
                    renderFire(minecraftIn, matrixStackIn);
            } catch (NoSuchFieldException | IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        }
    }

    @Shadow
    private static void renderFire(Minecraft minecraftIn, MatrixStack matrixStackIn) {
    }
}
