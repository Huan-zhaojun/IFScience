package com.ddhuan.ifscience.mixin;

import net.minecraft.util.BitArray;
import net.minecraft.world.gen.Heightmap;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;

@Mixin(Heightmap.class)
public abstract class HeightmapMixin {
    @Shadow
    @Final
    private final BitArray data = new BitArray(9, 256);

    @Unique
    public BitArray getBitArray() {
        return data;
    }
}
