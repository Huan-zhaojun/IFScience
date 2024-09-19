package com.ddhuan.ifscience.mixin.block;

import com.ddhuan.ifscience.Config;
import com.ddhuan.ifscience.common.TileEntity.TorchTileEntity;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.TorchBlock;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.IBlockReader;
import org.spongepowered.asm.mixin.Mixin;

import javax.annotation.Nullable;

@Mixin(TorchBlock.class)
public abstract class TorchBlockMixin extends Block {
    public TorchBlockMixin(Properties properties) {
        super(properties);
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
