package com.ddhuan.ifscience.common.Entity.render;

import com.ddhuan.ifscience.common.Entity.BlockEntity;
import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.block.BlockRenderType;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.texture.AtlasTexture;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class blockEntityRender<T extends BlockEntity> extends EntityRenderer<T> {
    protected blockEntityRender(EntityRendererManager renderManager) {
        super(renderManager);
    }

    public void render(T entityIn, float entityYaw, float partialTicks, MatrixStack matrixStackIn, IRenderTypeBuffer bufferIn, int packedLightIn) {
        matrixStackIn.push();
        matrixStackIn.translate(0.0D, 0.5D, 0.0D);
        matrixStackIn.rotate(Vector3f.YP.rotationDegrees(-90.0F));
        matrixStackIn.translate(-0.5D, -0.5D, 0.5D);
        matrixStackIn.rotate(Vector3f.YP.rotationDegrees(90.0F));
        renderBlock(entityIn, partialTicks, matrixStackIn, bufferIn, packedLightIn);//渲染方块：包括普通模型和方块实体
        matrixStackIn.pop();
        super.render(entityIn, entityYaw, partialTicks, matrixStackIn, bufferIn, packedLightIn);
    }

    //渲染方块：包括普通模型和方块实体
    public void renderBlock(T entityIn, float partialTicks, MatrixStack matrixStackIn, IRenderTypeBuffer bufferIn, int packedLightIn) {
        if (entityIn.tileEntity == null || !entityIn.blockState.getBlock().hasTileEntity(entityIn.blockState))//没有方块实体渲染，普通模型渲染
            Minecraft.getInstance().getBlockRendererDispatcher().renderBlock(entityIn.blockState, matrixStackIn, bufferIn, packedLightIn, OverlayTexture.NO_OVERLAY);
        else if (entityIn.tileEntity != null) {//有方块实体渲染
            if (entityIn.blockState.getRenderType() == BlockRenderType.MODEL)//普通模型和方块实体混合渲染
                Minecraft.getInstance().getBlockRendererDispatcher().renderBlock(entityIn.blockState, matrixStackIn, bufferIn, packedLightIn, OverlayTexture.NO_OVERLAY);
            TileEntityRendererDispatcher.instance.renderTileEntity(entityIn.tileEntity, partialTicks, matrixStackIn, bufferIn);
        }
    }

    @Override
    public ResourceLocation getEntityTexture(T entity) {
        return AtlasTexture.LOCATION_BLOCKS_TEXTURE;
    }
}
