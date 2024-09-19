package com.ddhuan.ifscience.common.Fluid;

import com.ddhuan.ifscience.Custom.rainingUtil;
import com.ddhuan.ifscience.common.TileEntity.TileEntityTypeRegistry;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.biome.Biome;

import static com.ddhuan.ifscience.Config.RAIN;
import static com.ddhuan.ifscience.Config.SOLIDIFY_LAVA;

public class LavaFluidTileEntity extends TileEntity implements ITickableTileEntity {
    public LavaFluidTileEntity() {
        super(TileEntityTypeRegistry.LavaFluidTileEntity.get());
    }

    @Override
    public void tick() {
        if (world != null && RAIN.get() && SOLIDIFY_LAVA.get()) {
            Biome.RainType rainType = world.getBiome(pos).getPrecipitation();
            rainingUtil.solidifyLava(world, pos, rainType);//岩浆受到雨水被凝固
        }
    }
}
