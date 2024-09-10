import com.google.common.collect.ImmutableList;
import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.block.Block;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.chunk.ChunkRenderCache;
import net.minecraft.client.renderer.chunk.ChunkRenderDispatcher;
import net.minecraft.client.renderer.chunk.ChunkRenderDispatcher.ChunkRender;
import net.minecraft.client.renderer.chunk.VisGraph;
import net.minecraft.fluid.FluidState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.model.data.IModelData;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.Set;

@Mixin(targets = "net.minecraft.client.renderer.chunk.ChunkRenderDispatcher$ChunkRender$RebuildTask")
@OnlyIn(Dist.CLIENT)
public class ChunkRenderDispatcher$ChunkRender$RebuildTask implements net.minecraftforge.client.extensions.IForgeRenderChunk {
    /*@Inject(method = "compile",at = @At(value = "INVOKE", target = ""))
    private void compile1(float xIn, float yIn, float zIn, ChunkRenderDispatcher.CompiledChunk compiledChunkIn, RegionRenderCacheBuilder builderIn, CallbackInfoReturnable<Set<TileEntity>> cir) {

    }
    @Redirect(method = "compile", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/BlockRendererDispatcher;renderModel(Lnet/minecraft/block/BlockState;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/world/IBlockDisplayReader;Lcom/mojang/blaze3d/matrix/MatrixStack;Lcom/mojang/blaze3d/vertex/IVertexBuilder;ZLjava/util/Random;Lnet/minecraftforge/client/model/data/IModelData;)Z"))
    private boolean compile2(BlockRendererDispatcher instance, BlockState blockstate, BlockPos blockpos2, IBlockDisplayReader chunkrendercache, MatrixStack matrixstack, IVertexBuilder bufferbuilder2, boolean posIn, Random random, IModelData modelData) {
        return instance.renderModel(blockstate, blockpos2, chunkrendercache, matrixstack, bufferbuilder2, true, random, modelData);
    }*/
    @Inject(method = "compile", at = @At(value = "INVOKE", shift = At.Shift.AFTER, target = "Lnet/minecraft/client/renderer/chunk/ChunkRenderDispatcher$ChunkRender$RebuildTask;getModelData(Lnet/minecraft/util/math/BlockPos;)Lnet/minecraftforge/client/model/data/IModelData;"), remap = false, locals = LocalCapture.CAPTURE_FAILEXCEPTION)
    private void compile3(float xIn, float yIn, float zIn, ChunkRenderDispatcher.CompiledChunk compiledChunkIn, RegionRenderCacheBuilder builderIn, CallbackInfoReturnable<Set<TileEntity>> cir, int i, BlockPos blockpos, BlockPos blockpos1, VisGraph visgraph, Set set, ChunkRenderCache chunkrendercache, MatrixStack matrixstack, Random random, BlockRendererDispatcher blockrendererdispatcher, Iterator var15, BlockPos blockpos2, BlockState blockstate, Block block, FluidState fluidstate) throws IllegalAccessException, InvocationTargetException, ClassNotFoundException {
        net.minecraftforge.client.model.data.IModelData modelData = (IModelData) ObfuscationReflectionHelper.findMethod(
                Class.forName("net.minecraft.client.renderer.chunk.ChunkRenderDispatcher$ChunkRender$ChunkRenderTask"), "getModelData", BlockPos.class).invoke(this, blockpos);
        Field layersStartedField = ObfuscationReflectionHelper.findField(ChunkRenderDispatcher.CompiledChunk.class, "layersStarted");
        Set<RenderType> layersStarted = (Set<RenderType>) layersStartedField.get(compiledChunkIn);
        Field empty = ObfuscationReflectionHelper.findField(ChunkRenderDispatcher.CompiledChunk.class, "empty");

        Method beginLayerMethod = ObfuscationReflectionHelper.findMethod(ChunkRender.class, "beginLayer", BufferBuilder.class);

        Class<?> aClass = Class.forName("net.minecraft.client.renderer.chunk.ChunkRenderDispatcher$ChunkRender$RebuildTask");
        for (RenderType rendertype : RenderType.getBlockRenderTypes()) {
            System.out.println(111111111);
            net.minecraftforge.client.ForgeHooksClient.setRenderLayer(rendertype);
            System.out.println(222222222);
            if (!fluidstate.isEmpty() && RenderTypeLookup.canRenderInLayer(fluidstate, rendertype)) {
                BufferBuilder bufferbuilder = builderIn.getBuilder(rendertype);
                if (layersStarted.add(rendertype)) {
                    beginLayerMethod.invoke(this, bufferbuilder)
                    ;//ChunkRenderDispatcher.ChunkRender.beginLayer(bufferbuilder);
                }

                if (blockrendererdispatcher.renderFluid(blockpos2, chunkrendercache, bufferbuilder, fluidstate)) {
                    empty.set(compiledChunkIn, false);
                    layersStarted.add(rendertype);
                }
            }

            if (blockstate.getRenderType() != BlockRenderType.INVISIBLE && RenderTypeLookup.canRenderInLayer(blockstate, rendertype)) {
                RenderType rendertype1 = rendertype;
                BufferBuilder bufferbuilder2 = builderIn.getBuilder(rendertype1);
                if (layersStarted.add(rendertype1)) {
                    beginLayerMethod.invoke(this, bufferbuilder2);//ChunkRenderDispatcher.ChunkRender.this.beginLayer(bufferbuilder2);
                }

                matrixstack.push();
                matrixstack.translate((double) (blockpos2.getX() & 15), (double) (blockpos2.getY() & 15), (double) (blockpos2.getZ() & 15));
                if (blockrendererdispatcher.renderModel(blockstate, blockpos2, chunkrendercache, matrixstack, bufferbuilder2, true, random, modelData)) {
                    empty.set(compiledChunkIn, false);
                    layersStarted.add(rendertype1);
                }

                matrixstack.pop();
            }
        }
    }

    @Redirect(method = "compile", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/RenderType;getBlockRenderTypes()Ljava/util/List;"))
    private List<RenderType> compile4() {
        return ImmutableList.of();
    }
}