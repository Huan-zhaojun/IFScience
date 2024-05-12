package com.ddhuan.ifscience.mixin;

import com.ddhuan.ifscience.Custom.rainingUtil;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.chunk.AbstractChunkProvider;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.gen.Heightmap;
import net.minecraft.world.server.ChunkHolder;
import net.minecraft.world.server.ServerChunkProvider;
import net.minecraft.world.server.ServerWorld;
import net.minecraft.world.spawner.WorldEntitySpawner;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.util.Optional;

@Mixin(ServerChunkProvider.class)
public abstract class ServerChunkProviderMixin extends AbstractChunkProvider {
    @Shadow
    @Final
    public ServerWorld world;

    @SuppressWarnings({"target", "unresolvable-target", })
    @Inject(method = "lambda$tickChunks$5", at = @At(value = "INVOKE", target = "Lnet/minecraft/profiler/IProfiler;endSection()V", shift = At.Shift.AFTER), locals = LocalCapture.CAPTURE_FAILSOFT)
    private void tickChunks$lambda(long j, boolean flag1, WorldEntitySpawner.EntityDensityManager worldentityspawner$entitydensitymanager, boolean flag2, int k, ChunkHolder p_241099_7_, CallbackInfo ci, Optional<Chunk> optional) {
        if (optional.isPresent()) {
            Chunk chunk$Mixin = optional.get();
            //rainingUtil.placePuddle(world, chunk$Mixin, chunk$Mixin.getHeightmap(Heightmap.Type.MOTION_BLOCKING_NO_LEAVES), chunk$Mixin.getPos());
        }
    }
}
