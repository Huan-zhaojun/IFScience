package com.ddhuan.ifscience.common.Entity.render;

import com.ddhuan.ifscience.common.Entity.CutBlockEntity;
import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.util.math.vector.Vector3f;

public class CutBlockEntityRender<T extends CutBlockEntity> extends blockEntityRender<T> {
    protected CutBlockEntityRender(EntityRendererManager renderManager) {
        super(renderManager);
    }

    @Override
    public void render(T entityIn, float entityYaw, float partialTicks, MatrixStack matrixStackIn, IRenderTypeBuffer bufferIn, int packedLightIn) {
        matrixStackIn.push();
        renderHalfBlock(matrixStackIn, bufferIn, entityIn, packedLightIn, true);
        renderHalfBlock(matrixStackIn, bufferIn, entityIn, packedLightIn, false);
        matrixStackIn.pop();
    }

    private void renderHalfBlock(MatrixStack matrixStackIn, IRenderTypeBuffer bufferIn, T entityIn, int packedLightIn, boolean isUpperHalf) {
        matrixStackIn.push();
        matrixStackIn.translate(0.0D, 0.5D, 0.0D);
        matrixStackIn.rotate(Vector3f.YP.rotationDegrees(-90.0F));
        matrixStackIn.translate(-0.5D, -0.5D, 0.5D);
        matrixStackIn.rotate(Vector3f.YP.rotationDegrees(90.0F));
        //方块切开两半
        if (isUpperHalf) {
            matrixStackIn.translate(-0.025D, 0.0D, 0.0D);
            matrixStackIn.scale(0.5F, 1.0F, 1.0F);
        } else {
            matrixStackIn.translate(0.50D + 0.025D, 0.0D, 0.0D);
            matrixStackIn.scale(0.5F, 1.0F, 1.0F);
        }

        //渲染被切割的方块
        Minecraft.getInstance().getBlockRendererDispatcher().renderBlock(entityIn.blockState, matrixStackIn, bufferIn, packedLightIn, OverlayTexture.NO_OVERLAY);

        matrixStackIn.pop();
    }
}
