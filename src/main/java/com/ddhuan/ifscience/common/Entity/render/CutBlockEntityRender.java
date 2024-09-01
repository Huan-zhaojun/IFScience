package com.ddhuan.ifscience.common.Entity.render;

import com.ddhuan.ifscience.common.Entity.CutBlockEntity;
import com.ddhuan.ifscience.common.Item.itemRegistry;
import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.model.ItemCameraTransforms;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.vector.Vector3f;

public class CutBlockEntityRender<T extends CutBlockEntity> extends blockEntityRender<T> {
    protected CutBlockEntityRender(EntityRendererManager renderManager) {
        super(renderManager);
    }

    @Override
    public void render(T entityIn, float entityYaw, float partialTicks, MatrixStack matrixStackIn, IRenderTypeBuffer bufferIn, int packedLightIn) {
        CutBlockEntity.Direction direction = entityIn.direction;//获取切割方向
        matrixStackIn.push();
        if (direction != null) {
            renderHalfBlock(direction, matrixStackIn, bufferIn, entityIn, packedLightIn, true);
            renderHalfBlock(direction, matrixStackIn, bufferIn, entityIn, packedLightIn, false);
            renderAngleGrinder(direction, matrixStackIn, bufferIn, packedLightIn);//渲染切割的角磨机
        }
        matrixStackIn.pop();
    }

    private void renderHalfBlock(CutBlockEntity.Direction direction, MatrixStack matrixStackIn, IRenderTypeBuffer bufferIn, T entityIn, int packedLightIn, boolean isHalf) {
        matrixStackIn.push();
        matrixStackIn.translate(0.0D, 0.5D, 0.0D);
        matrixStackIn.rotate(Vector3f.YP.rotationDegrees(-90.0F));
        matrixStackIn.translate(-0.5D, -0.5D, 0.5D);
        matrixStackIn.rotate(Vector3f.YP.rotationDegrees(90.0F));
        //方块切开两半
        if (direction.equals(CutBlockEntity.Direction.NORTH) || direction.equals(CutBlockEntity.Direction.SOUTH)) {
            //南北切，东西分
            if (isHalf) {
                matrixStackIn.translate(0/*-0.025D*/, 0.0D, 0.0D);
                matrixStackIn.scale(0.5F, 1.0F, 1.0F);
            } else {
                matrixStackIn.translate(0.50D /*+ 0.025D*/, 0.0D, 0.0D);
                matrixStackIn.scale(0.5F, 1.0F, 1.0F);
            }
        } else {
            if (isHalf) {
                matrixStackIn.translate(0.0D, 0.0D, 0/*-0.025D*/);
                matrixStackIn.scale(1.0F, 1.0F, 0.5F);
            } else {
                matrixStackIn.translate(0.0D, 0.0D, 0.50D /*+ 0.025D*/);
                matrixStackIn.scale(1.0F, 1.0F, 0.5F);
            }
        }

        //渲染被切割的方块
        Minecraft.getInstance().getBlockRendererDispatcher().renderBlock(entityIn.blockState, matrixStackIn, bufferIn, packedLightIn, OverlayTexture.NO_OVERLAY);

        matrixStackIn.pop();
    }

    //渲染切割的角磨机
    private static void renderAngleGrinder(CutBlockEntity.Direction direction, MatrixStack matrixStackIn, IRenderTypeBuffer bufferIn, int packedLightIn) {
        matrixStackIn.push();
        /*if (Minecraft.getInstance().world != null && Minecraft.getInstance().world.getGameTime() % 10 == 0)
            System.out.println(direction);*/
        //对齐初始位置、朝向和高度
        matrixStackIn.translate(direction.translateX, 1.25D, direction.translateZ);
        matrixStackIn.rotate(Vector3f.YN.rotationDegrees(direction.degreesYN));
        matrixStackIn.rotate(Vector3f.ZN.rotationDegrees(90));
        Minecraft.getInstance().getItemRenderer().renderItem(new ItemStack(itemRegistry.angleGrinder.get(), 1),
                ItemCameraTransforms.TransformType.FIXED, packedLightIn, OverlayTexture.NO_OVERLAY, matrixStackIn, bufferIn);
        matrixStackIn.pop();
    }
}
