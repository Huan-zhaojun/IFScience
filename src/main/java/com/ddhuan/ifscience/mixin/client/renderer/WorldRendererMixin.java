package com.ddhuan.ifscience.mixin.client.renderer;

import com.ddhuan.ifscience.Custom.rainingUtil;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.resources.IResourceManagerReloadListener;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.IBlockDisplayReader;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.Heightmap;
import net.minecraftforge.client.IWeatherRenderHandler;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Surrogate;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.util.Random;

@Mixin(WorldRenderer.class)
public abstract class WorldRendererMixin implements IResourceManagerReloadListener, AutoCloseable {
    @Shadow
    @Final
    private Minecraft mc;

    @Shadow
    @Final
    private static ResourceLocation RAIN_TEXTURES;
    @Shadow
    @Final
    private static ResourceLocation SNOW_TEXTURES;

    @Shadow
    private int ticks;

    @Shadow
    private static int getCombinedLight(IBlockDisplayReader lightReaderIn, BlockPos blockPosIn) {
        return 15;
    }

    @Shadow
    @Final
    private float[] rainSizeX;
    @Shadow
    @Final
    private final float[] rainSizeZ = new float[1024];

    @Inject(method = "renderRainSnow", at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/systems/RenderSystem;depthMask(Z)V", shift = At.Shift.AFTER), locals = LocalCapture.CAPTURE_FAILSOFT, cancellable = true)
    private void renderRainSnow(LightTexture lightmapIn, float partialTicks, double xIn, double yIn, double zIn, CallbackInfo ci, IWeatherRenderHandler renderHandler, float f, World world, int i, int j, int k, Tessellator tessellator, BufferBuilder bufferbuilder, int l) {
        renderRainSnow1(lightmapIn, partialTicks, xIn, yIn, zIn, ci, f, world, i, j, k, tessellator, bufferbuilder, l);
    }

    private void renderRainSnow1(LightTexture lightmapIn, float partialTicks, double xIn, double yIn, double zIn, CallbackInfo ci, float f, World world, int i, int j, int k, Tessellator tessellator, BufferBuilder bufferbuilder, int l) {
        int i1 = -1;
        float f1 = (float) this.ticks + partialTicks;
        RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
        BlockPos.Mutable blockpos$mutable = new BlockPos.Mutable();

        for (int j1 = k - l; j1 <= k + l; ++j1) {
            for (int k1 = i - l; k1 <= i + l; ++k1) {
                int l1 = (j1 - k + 16) * 32 + k1 - i + 16;
                double d0 = (double) this.rainSizeX[l1] * 0.5D;
                double d1 = (double) this.rainSizeZ[l1] * 0.5D;
                blockpos$mutable.setPos(k1, 0, j1);
                Biome biome = world.getBiome(blockpos$mutable);
                int i2 = world.getHeight(Heightmap.Type.MOTION_BLOCKING, blockpos$mutable).getY();
                int j2 = j - l;
                int k2 = j + l;
                if (j2 < i2) {
                    j2 = i2;
                }

                if (k2 < i2) {
                    k2 = i2;
                }

                int l2 = i2;
                if (i2 < j) {
                    l2 = j;
                }

                if (j2 != k2) {
                    Random random = new Random((long) (k1 * k1 * 3121 + k1 * 45238971 ^ j1 * j1 * 418711 + j1 * 13761));
                    blockpos$mutable.setPos(k1, j2, j1);
                    float f2 = biome.getTemperature(blockpos$mutable);
                    if (biome.getPrecipitation() != Biome.RainType.NONE) {
                        if (f2 >= 0.15F) {
                            if (i1 != 0) {
                                if (i1 >= 0) {
                                    tessellator.draw();
                                }

                                i1 = 0;
                                this.mc.getTextureManager().bindTexture(RAIN_TEXTURES);
                                bufferbuilder.begin(7, DefaultVertexFormats.PARTICLE_POSITION_TEX_COLOR_LMAP);
                            }

                            int i3 = this.ticks + k1 * k1 * 3121 + k1 * 45238971 + j1 * j1 * 418711 + j1 * 13761 & 31;
                            float f3 = -((float) i3 + partialTicks) / 32.0F * (3.0F + random.nextFloat());
                            double d2 = (double) ((float) k1 + 0.5F) - xIn;
                            double d4 = (double) ((float) j1 + 0.5F) - zIn;
                            float f4 = MathHelper.sqrt(d2 * d2 + d4 * d4) / (float) l;
                            float f5 = ((1.0F - f4 * f4) * 0.5F + 0.5F) * f;
                            blockpos$mutable.setPos(k1, l2, j1);
                            int j3 = getCombinedLight(world, blockpos$mutable);
                            bufferbuilder.pos((double) k1 - xIn - d0 + 0.5D, (double) k2 - yIn, (double) j1 - zIn - d1 + 0.5D).tex(0.0F, (float) j2 * 0.25F + f3).color(1.0F, 1.0F, 1.0F, f5).lightmap(j3).endVertex();
                            bufferbuilder.pos((double) k1 - xIn + d0 + 0.5D, (double) k2 - yIn, (double) j1 - zIn + d1 + 0.5D).tex(1.0F, (float) j2 * 0.25F + f3).color(1.0F, 1.0F, 1.0F, f5).lightmap(j3).endVertex();
                            bufferbuilder.pos((double) k1 - xIn + d0 + 0.5D, (double) j2 - yIn, (double) j1 - zIn + d1 + 0.5D).tex(1.0F, (float) k2 * 0.25F + f3).color(1.0F, 1.0F, 1.0F, f5).lightmap(j3).endVertex();
                            bufferbuilder.pos((double) k1 - xIn - d0 + 0.5D, (double) j2 - yIn, (double) j1 - zIn - d1 + 0.5D).tex(0.0F, (float) k2 * 0.25F + f3).color(1.0F, 1.0F, 1.0F, f5).lightmap(j3).endVertex();
                        } else {
                            if (i1 != 1) {
                                if (i1 >= 0) {
                                    tessellator.draw();
                                }

                                i1 = 1;
                                this.mc.getTextureManager().bindTexture(SNOW_TEXTURES);
                                bufferbuilder.begin(7, DefaultVertexFormats.PARTICLE_POSITION_TEX_COLOR_LMAP);
                            }

                            float f6 = -((float) (this.ticks & 511) + partialTicks) / 512.0F;
                            float f7 = (float) (random.nextDouble() + (double) f1 * 0.01D * (double) ((float) random.nextGaussian()));
                            float f8 = (float) (random.nextDouble() + (double) (f1 * (float) random.nextGaussian()) * 0.001D);
                            double d3 = (double) ((float) k1 + 0.5F) - xIn;
                            double d5 = (double) ((float) j1 + 0.5F) - zIn;
                            float f9 = MathHelper.sqrt(d3 * d3 + d5 * d5) / (float) l;
                            float f10 = ((1.0F - f9 * f9) * 0.3F + 0.5F) * f;
                            blockpos$mutable.setPos(k1, l2, j1);
                            int k3 = getCombinedLight(world, blockpos$mutable);
                            int l3 = k3 >> 16 & '\uffff';
                            int i4 = (k3 & '\uffff') * 3;
                            int j4 = (l3 * 3 + 240) / 4;
                            int k4 = (i4 * 3 + 240) / 4;
                            bufferbuilder.pos((double) k1 - xIn - d0 + 0.5D, (double) k2 - yIn, (double) j1 - zIn - d1 + 0.5D).tex(0.0F + f7, (float) j2 * 0.25F + f6 + f8).color(1.0F, 1.0F, 1.0F, f10).lightmap(k4, j4).endVertex();
                            bufferbuilder.pos((double) k1 - xIn + d0 + 0.5D, (double) k2 - yIn, (double) j1 - zIn + d1 + 0.5D).tex(1.0F + f7, (float) j2 * 0.25F + f6 + f8).color(1.0F, 1.0F, 1.0F, f10).lightmap(k4, j4).endVertex();
                            bufferbuilder.pos((double) k1 - xIn + d0 + 0.5D, (double) j2 - yIn, (double) j1 - zIn + d1 + 0.5D).tex(1.0F + f7, (float) k2 * 0.25F + f6 + f8).color(1.0F, 1.0F, 1.0F, f10).lightmap(k4, j4).endVertex();
                            bufferbuilder.pos((double) k1 - xIn - d0 + 0.5D, (double) j2 - yIn, (double) j1 - zIn - d1 + 0.5D).tex(0.0F + f7, (float) k2 * 0.25F + f6 + f8).color(1.0F, 1.0F, 1.0F, f10).lightmap(k4, j4).endVertex();
                        }
                    } else {
                        i1 = rainingUtil.renderFireRain(partialTicks, xIn, yIn, zIn, f, world, tessellator, bufferbuilder, (float) l, i1, k1, j1, random, blockpos$mutable, l2, d0, k2, d1, j2, this.mc, this.ticks);
                    }
                }
            }
        }

        if (i1 >= 0) {
            tessellator.draw();
        }

        RenderSystem.enableCull();
        RenderSystem.disableBlend();
        RenderSystem.defaultAlphaFunc();
        RenderSystem.disableAlphaTest();
        lightmapIn.disableLightmap();
        ci.cancel();
    }

    @Surrogate
    private void renderRainSnow(LightTexture lightmapIn, float partialTicks, double xIn, double yIn, double zIn, CallbackInfo ci, float f, World world, int i, int j, int k, Tessellator tessellator, BufferBuilder bufferbuilder, int l){
        renderRainSnow1(lightmapIn, partialTicks, xIn, yIn, zIn, ci, f, world, i, j, k, tessellator, bufferbuilder, l);
    }
}
