package com.ddhuan.ifscience.common.TileEntity;

import com.ddhuan.ifscience.common.Block.blockRegistry;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.WallTorchBlock;
import net.minecraft.entity.LivingEntity;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.AxisAlignedBB;

import java.util.List;

import static com.ddhuan.ifscience.common.Block.WallExtinguishedTorch.HORIZONTAL_FACING;
import static com.ddhuan.ifscience.common.TileEntity.TileEntityTypeRegistry.torchTileEntity;

public class TorchTileEntity extends TileEntity implements ITickableTileEntity {
    public TorchTileEntity() {
        super(torchTileEntity.get());
    }

    @Override
    public void tick() {
        if (world != null && !world.isRemote) {
            if (getBlockState().getBlock().equals(Blocks.TORCH)) {
                double h = 0.55;
                AxisAlignedBB axisAlignedBB = new AxisAlignedBB(pos.getX() + 0.5, pos.getY() + h, pos.getZ() + 0.5, pos.getX() + 0.5, pos.getY() + h, pos.getZ() + 0.5).grow(0.35);
                List<LivingEntity> livingEntitys = world.getEntitiesWithinAABB(LivingEntity.class, axisAlignedBB);
                if (!livingEntitys.isEmpty()) {
                    livingEntitys.forEach(livingEntity -> {
                        livingEntity.setFire(1);//靠近火把会使生物着火
                    });
                }
            } else if (getBlockState().getBlock().equals(Blocks.WALL_TORCH)) {
                double h = 0.775, x = 0, z = 0, l = 0.20;
                Direction direction = getBlockState().get(WallTorchBlock.HORIZONTAL_FACING);
                if (direction.equals(Direction.EAST) || direction.equals(Direction.WEST))
                    x = direction == Direction.EAST ? -l : +l;
                else if (direction.equals(Direction.SOUTH) || direction.equals(Direction.NORTH))
                    z = direction == Direction.SOUTH ? -l : +l;
                AxisAlignedBB axisAlignedBB2 = new AxisAlignedBB(pos.getX() + 0.5 + x, pos.getY() + h, pos.getZ() + 0.5 + z, pos.getX() + 0.5 + x, pos.getY() + h, pos.getZ() + 0.5 + z).grow(0.275);
                List<LivingEntity> livingEntitys2 = world.getEntitiesWithinAABB(LivingEntity.class, axisAlignedBB2);
                if (!livingEntitys2.isEmpty()) {
                    livingEntitys2.forEach(livingEntity -> {
                        livingEntity.setFire(1);//靠近火把会使生物着火
                    });
                }
            }
            if (world.isRainingAt(pos)) {//下雨火把会被熄灭
                BlockState blockState = world.getBlockState(pos);
                BlockState blockState1 = blockRegistry.extinguishedTorch.get().getDefaultState();
                if (blockState.getBlock().equals(Blocks.WALL_TORCH))
                    blockState1 = blockRegistry.wallExtinguishedTorch.get().getDefaultState().with(HORIZONTAL_FACING, blockState.get(WallTorchBlock.HORIZONTAL_FACING));
                world.setBlockState(pos, blockState1, 11);
            }
        }
    }
}
