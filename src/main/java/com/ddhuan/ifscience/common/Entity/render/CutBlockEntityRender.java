package com.ddhuan.ifscience.common.Entity.render;

import com.ddhuan.ifscience.common.Entity.CutBlockEntity;
import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.model.ItemCameraTransforms;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.util.Direction;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.vector.Vector3f;

public class CutBlockEntityRender<T extends CutBlockEntity> extends blockEntityRender<T> {
    public static int levelCutTick = 1000;//横向切割时间
    public static int verticalCutTick = 1000;//竖向切割时间
    public static int cutBlockTick = 500;//切完方块的分裂时间
    public static int maxAnimationTick = levelCutTick + verticalCutTick + cutBlockTick;//最大动画时间

    protected CutBlockEntityRender(EntityRendererManager renderManager) {
        super(renderManager);
    }

    @Override
    public void render(T entityIn, float entityYaw, float partialTicks, MatrixStack matrixStackIn, IRenderTypeBuffer bufferIn, int packedLightIn) {
        if (entityIn.lastTime == 0) entityIn.lastTime = System.currentTimeMillis();
        if (Minecraft.getInstance().isGamePaused()) entityIn.lastTime = System.currentTimeMillis();
        CutBlockEntity.Direction direction = entityIn.direction;//获取切割方向
        matrixStackIn.push();
        if (direction != null) {
            renderHalfBlock(direction, entityIn.animationTick, matrixStackIn, bufferIn, entityIn, packedLightIn, true);
            renderHalfBlock(direction, entityIn.animationTick, matrixStackIn, bufferIn, entityIn, packedLightIn, false);
            if (entityIn.animationTick <= levelCutTick + verticalCutTick)
                renderAngleGrinder(direction, entityIn.animationTick, entityIn, matrixStackIn, bufferIn, packedLightIn);//渲染切割的角磨机
        }
        matrixStackIn.pop();
        if (entityIn.animationTick < maxAnimationTick && !Minecraft.getInstance().isGamePaused()) {/*++entityIn.animationTick*/
            entityIn.animationTick += (int) (System.currentTimeMillis() - entityIn.lastTime);
            entityIn.lastTime = System.currentTimeMillis();
        }
    }

    private void renderHalfBlock(CutBlockEntity.Direction direction, int animationTick, MatrixStack matrixStackIn, IRenderTypeBuffer bufferIn, T entityIn, int packedLightIn, boolean isHalf) {
        matrixStackIn.push();
        matrixStackIn.translate(0.0D, 0.5D, 0.0D);
        matrixStackIn.rotate(Vector3f.YP.rotationDegrees(-90.0F));
        matrixStackIn.translate(-0.5D, -0.5D, 0.5D);
        matrixStackIn.rotate(Vector3f.YP.rotationDegrees(90.0F));
        //方块切开两半
        double gap = 0.035D;//分裂间隙
        if (direction.equals(CutBlockEntity.Direction.NORTH) || direction.equals(CutBlockEntity.Direction.SOUTH)) {
            //南北切，东西分
            if (isHalf) {
                matrixStackIn.translate(0 + moveBlock(isHalf, gap, animationTick), 0.0D, 0.0D);
                matrixStackIn.rotate(Vector3f.ZN.rotationDegrees(rotateBlock(direction, animationTick)/*-45*//*-90*/));//方块倾斜
                matrixStackIn.scale(0.5F, 1.0F, 1.0F);
            } else {
                matrixStackIn.translate(0.5 + moveBlock(isHalf, gap, animationTick), 0, 0.0D);
                matrixStackIn.translate(0.5, 0, 0.0D);//旋转中心点平移到左侧居中位置
                matrixStackIn.rotate(Vector3f.ZN.rotationDegrees(-rotateBlock(direction, animationTick)));//方块倾斜
                matrixStackIn.translate(-0.5, 0, 0.0D);//复位回来
                matrixStackIn.scale(0.5F, 1.0F, 1.0F);
            }
        } else {
            if (isHalf) {
                matrixStackIn.translate(0.0D, 0.0D, 0 + moveBlock(isHalf, gap, animationTick));
                matrixStackIn.rotate(Vector3f.XN.rotationDegrees(rotateBlock(direction, animationTick)/*45*//*90*/));//方块倾斜
                matrixStackIn.scale(1.0F, 1.0F, 0.5F);
            } else {
                matrixStackIn.translate(0.0D, 0.0D, 0.50D + moveBlock(isHalf, gap, animationTick));
                matrixStackIn.translate(0, 0, 0.5D);
                matrixStackIn.rotate(Vector3f.XN.rotationDegrees(-rotateBlock(direction, animationTick)/*45*//*90*/));//方块倾斜
                matrixStackIn.translate(0, 0, -0.5D);
                matrixStackIn.scale(1.0F, 1.0F, 0.5F);
            }
        }

        //渲染被切割的方块
        Minecraft.getInstance().getBlockRendererDispatcher().renderBlock(entityIn.blockState, matrixStackIn, bufferIn, packedLightIn, OverlayTexture.NO_OVERLAY);

        matrixStackIn.pop();
    }

    //渲染切割的角磨机
    private void renderAngleGrinder(CutBlockEntity.Direction direction, int animationTick, T entityIn, MatrixStack matrixStackIn, IRenderTypeBuffer bufferIn, int packedLightIn) {
        matrixStackIn.push();
        /*if (Minecraft.getInstance().world != null && Minecraft.getInstance().world.getGameTime() % 10 == 0)
            System.out.println(direction);*/
        //对齐初始位置、朝向和高度
        VoxelShape shape = entityIn.blockState.getShape(entityIn.world, entityIn.getPosition());//获取方块真实大小长宽高
        double height = Math.abs(shape.getStart(Direction.Axis.Y) - shape.getEnd(Direction.Axis.Y)), fall = height - 0.2;//真实高度
        double shapeX = Math.abs(shape.getStart(Direction.Axis.X) - shape.getEnd(Direction.Axis.X)),
                shapeZ = Math.abs(shape.getStart(Direction.Axis.Z) - shape.getEnd(Direction.Axis.Z));//真实长宽
        matrixStackIn.translate(direction.translateX + direction.moveX(animationTick, levelCutTick, shapeX),
                /*1.25D*/height + 0.25 - (animationTick > levelCutTick ? (animationTick <= (levelCutTick + verticalCutTick) ? fall/*0.8D*/ * ((double) (animationTick - levelCutTick) / verticalCutTick) : fall) : 0),
                direction.translateZ + direction.moveZ(animationTick, levelCutTick, shapeZ));
        matrixStackIn.rotate(Vector3f.YN.rotationDegrees(direction.degreesYN));
        matrixStackIn.rotate(Vector3f.ZN.rotationDegrees(90));
        Minecraft.getInstance().getItemRenderer().renderItem(entityIn.angleGrinder,
                ItemCameraTransforms.TransformType.FIXED, packedLightIn, OverlayTexture.NO_OVERLAY, matrixStackIn, bufferIn);
        matrixStackIn.pop();
    }

    //获取方块被切开后横向移动动画的量值
    private static double moveBlock(boolean isHalf, double gap, int animationTick) {
        if (animationTick > levelCutTick && animationTick <= (levelCutTick + verticalCutTick)) {
            return (isHalf ? -1.0 : +1.0) * gap * ((double) (animationTick - levelCutTick) / verticalCutTick);
        } else if (animationTick <= levelCutTick) {
            return 0;
        } else if (animationTick > (levelCutTick + verticalCutTick)) {
            double lastMove = (isHalf ? -1.0 : +1.0) * gap, move = (isHalf ? -1.0 : +1.0) * 0.75;
            if (animationTick <= (levelCutTick + verticalCutTick + cutBlockTick)) {
                return lastMove + move * ((double) (animationTick - (levelCutTick + verticalCutTick)) / cutBlockTick);
            } else if (animationTick > (levelCutTick + verticalCutTick + cutBlockTick)) {
                return lastMove + move;
            }
        }
        return 0;
    }

    //获取方块被切开后旋转动画的量值
    private static float rotateBlock(CutBlockEntity.Direction direction, int animationTick) {
        boolean flag = direction.equals(CutBlockEntity.Direction.NORTH) || direction.equals(CutBlockEntity.Direction.SOUTH);
        int lastTick = levelCutTick + verticalCutTick;
        if (animationTick > lastTick && animationTick <= (lastTick + cutBlockTick)) {
            return (float) ((flag ? -1.0 : +1.0) * 90 * ((double) (animationTick - lastTick) / cutBlockTick));
        } else if (animationTick <= lastTick) {
            return 0;
        } else if (animationTick >= (lastTick + cutBlockTick))
            return (float) (flag ? -1.0 : +1.0) * 90;
        return 0;
    }
}
