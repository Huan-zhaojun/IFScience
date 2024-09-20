package com.ddhuan.ifscience.mixin.block;

import com.ddhuan.ifscience.Config;
import com.ddhuan.ifscience.common.TileEntity.TorchTileEntity;
import net.minecraft.block.BlockState;
import net.minecraft.block.TorchBlock;
import net.minecraft.block.WallTorchBlock;
import net.minecraft.particles.IParticleData;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.IBlockReader;
import org.spongepowered.asm.mixin.Mixin;

import javax.annotation.Nullable;

@Mixin(WallTorchBlock.class)
public abstract class WallTorchBlockMixin extends TorchBlock {
    public WallTorchBlockMixin(Properties properties, IParticleData particleData) {
        super(properties, particleData);
    }

    @Override
    public boolean hasTileEntity(BlockState state) {
        return Config.TORCH.get();//开启火把科学玩法
    }

    @Nullable
    @Override
    public TileEntity createTileEntity(BlockState state, IBlockReader world) {
        return new TorchTileEntity();
    }
}
