import com.ddhuan.ifscience.common.Enchantment.EnchantedBlocksData;
import com.google.common.collect.ImmutableList;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import com.mojang.blaze3d.vertex.VertexBuilderUtils;
import net.minecraft.block.Block;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.chunk.ChunkRenderCache;
import net.minecraft.client.renderer.chunk.ChunkRenderDispatcher;
import net.minecraft.client.renderer.chunk.VisGraph;
import net.minecraft.fluid.FluidState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockDisplayReader;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.model.data.IModelData;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.Set;

@Mixin(targets = "net.minecraft.client.renderer.chunk.ChunkRenderDispatcher$ChunkRender")
@OnlyIn(Dist.CLIENT)
public class ChunkRenderMixin implements net.minecraftforge.client.extensions.IForgeRenderChunk {
    @Shadow
    private void beginLayer(BufferBuilder bufferBuilderIn) {

    }

    @Mixin(targets = "net.minecraft.client.renderer.chunk.ChunkRenderDispatcher$ChunkRender$RebuildTask")
    @OnlyIn(Dist.CLIENT)
    public static class RebuildTaskMixin implements net.minecraftforge.client.extensions.IForgeRenderChunk {
        @Shadow
        @Final
        public ChunkRenderDispatcher.ChunkRender this$1;

        //@Redirect(method = "compile", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/BlockRendererDispatcher;renderModel(Lnet/minecraft/block/BlockState;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/world/IBlockDisplayReader;Lcom/mojang/blaze3d/matrix/MatrixStack;Lcom/mojang/blaze3d/vertex/IVertexBuilder;ZLjava/util/Random;Lnet/minecraftforge/client/model/data/IModelData;)Z"))
        private boolean compile1(BlockRendererDispatcher instance, BlockState crashreport, BlockPos crashreportcategory, IBlockDisplayReader throwable, MatrixStack matrixStack, IVertexBuilder blockStateIn, boolean posIn, Random lightReaderIn, IModelData matrixStackIn) {
            return false;
        }

        private static Field empty = ObfuscationReflectionHelper.findField(ChunkRenderDispatcher.CompiledChunk.class, "empty");
        private static Field layersStartedField = ObfuscationReflectionHelper.findField(ChunkRenderDispatcher.CompiledChunk.class, "layersUsed");

        //@Inject(method = "compile", at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/matrix/MatrixStack;pop()V"), locals = LocalCapture.CAPTURE_FAILEXCEPTION)
        private void compile2(float xIn, float yIn, float zIn, ChunkRenderDispatcher.CompiledChunk compiledChunkIn, RegionRenderCacheBuilder builderIn, CallbackInfoReturnable<Set<TileEntity>> cir, int i, BlockPos blockpos, BlockPos blockpos1, VisGraph visgraph, Set set, ChunkRenderCache chunkrendercache, MatrixStack matrixstack, Random random, BlockRendererDispatcher blockrendererdispatcher, Iterator var15, BlockPos blockpos2, BlockState blockstate, Block block, FluidState fluidstate, IModelData modelData, Iterator var21, RenderType rendertype, RenderType rendertype1, BufferBuilder bufferbuilder2) throws IllegalAccessException {
            empty.setAccessible(true);
            layersStartedField.setAccessible(true);
            BufferBuilder bufferbuilder3 = bufferbuilder2;
            if (EnchantedBlocksData.EnchantedBlocksClientData.enchantedBlocks.containsKey(blockpos2)) {
                bufferbuilder3 = (BufferBuilder) VertexBuilderUtils.newDelegate(new BufferBuilder(RenderType.getGlint().getBufferSize()), bufferbuilder2);
            }
            if (Minecraft.getInstance().getBlockRendererDispatcher().renderModel(blockstate, blockpos2, chunkrendercache, matrixstack, bufferbuilder2, true, random, modelData)) {
                empty.set(compiledChunkIn, false);
                Set<RenderType> layersStarted = (Set<RenderType>) layersStartedField.get(compiledChunkIn);
                layersStarted.add(rendertype1);
                //layersStartedField.set(compiledChunkIn, layersStarted);
            }
        }

        //@Inject(method = "compile", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/RenderType;getBlockRenderTypes()Ljava/util/List;"), remap = false, locals = LocalCapture.CAPTURE_FAILEXCEPTION)
        private void compile3(float xIn, float yIn, float zIn, ChunkRenderDispatcher.CompiledChunk compiledChunkIn, RegionRenderCacheBuilder builderIn, CallbackInfoReturnable<Set<TileEntity>> cir, int i, BlockPos blockpos, BlockPos blockpos1, VisGraph visgraph, Set set, ChunkRenderCache chunkrendercache, MatrixStack matrixstack, Random random, BlockRendererDispatcher blockrendererdispatcher, Iterator var15, BlockPos blockpos2, BlockState blockstate, Block block, FluidState fluidstate, IModelData modelData) throws IllegalAccessException, InvocationTargetException, ClassNotFoundException {
            /*Field layersStartedField = ObfuscationReflectionHelper.findField(ChunkRenderDispatcher.CompiledChunk.class, "layersUsed");
            Field empty = ObfuscationReflectionHelper.findField(ChunkRenderDispatcher.CompiledChunk.class, "empty");*/

            Method beginLayerMethod = ObfuscationReflectionHelper.findMethod(ChunkRenderDispatcher.ChunkRender.class, "beginLayer", BufferBuilder.class);
            for (RenderType rendertype : RenderType.getBlockRenderTypes()) {
                net.minecraftforge.client.ForgeHooksClient.setRenderLayer(rendertype);
                if (!fluidstate.isEmpty() && RenderTypeLookup.canRenderInLayer(fluidstate, rendertype)) {
                    BufferBuilder bufferbuilder = builderIn.getBuilder(rendertype);
                    Set<RenderType> layersStarted = (Set<RenderType>) layersStartedField.get(compiledChunkIn);
                    if (layersStarted.add(rendertype)) {
                        beginLayerMethod.invoke(this$1, bufferbuilder);//ChunkRenderDispatcher.ChunkRender.beginLayer(bufferbuilder);
                    }

                    if (blockrendererdispatcher.renderFluid(blockpos2, chunkrendercache, bufferbuilder, fluidstate)) {
                        empty.set(compiledChunkIn, false);
                        layersStarted.add(rendertype);
                    }
                }

                if (blockstate.getRenderType() != BlockRenderType.INVISIBLE && RenderTypeLookup.canRenderInLayer(blockstate, rendertype)) {
                    RenderType rendertype1 = rendertype;
                    BufferBuilder bufferbuilder2 = builderIn.getBuilder(rendertype1);

                    /*if (EnchantedBlocksData.EnchantedBlocksClientData.enchantedBlocks.containsKey(blockpos2))
                        bufferbuilder2 = new BufferBuilder(RenderType.getGlint().getBufferSize());*/
                    Set<RenderType> layersStarted = (Set<RenderType>) layersStartedField.get(compiledChunkIn);
                    if (layersStarted.add(rendertype1)) {
                        beginLayerMethod.invoke(this$1, bufferbuilder2);//ChunkRenderDispatcher.ChunkRender.this.beginLayer(bufferbuilder2);
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

        //@Redirect(method = "compile", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/RenderType;getBlockRenderTypes()Ljava/util/List;", ordinal = 0))
        private List<RenderType> compile4() {
            return ImmutableList.of();
        }
    }
}
