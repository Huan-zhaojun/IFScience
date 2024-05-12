package com.ddhuan.ifscience.common.Fluid;

import com.ddhuan.ifscience.Custom.rainingUtil;
import com.ddhuan.ifscience.common.TileEntity.TileEntityTypeRegistry;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.biome.Biome;

public class LavaFluidTileEntity extends TileEntity implements ITickableTileEntity {
    public LavaFluidTileEntity() {
        super(TileEntityTypeRegistry.LavaFluidTileEntity.get());
    }

    @Override
    public void tick() {
        if (world != null) {
            Biome.RainType rainType = world.getBiome(pos).getPrecipitation();
            rainingUtil.extinguishLava(world, pos, rainType);
        }
    }
}
