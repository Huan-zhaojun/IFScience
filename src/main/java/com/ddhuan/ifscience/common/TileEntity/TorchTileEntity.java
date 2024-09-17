package com.ddhuan.ifscience.common.TileEntity;

import com.ddhuan.ifscience.common.Block.blockRegistry;
import com.ddhuan.ifscience.common.Enchantment.EnchantedBlocksData;
import com.ddhuan.ifscience.common.Enchantment.EnchantmentRegistry;
import net.minecraft.block.AbstractFireBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.WallTorchBlock;
import net.minecraft.entity.LivingEntity;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

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
            int fire = 100;
            if (getBlockState().getBlock().equals(Blocks.TORCH)) {//普通火把
                if (world.getGameTime() % 20 == 0 && world.rand.nextInt(1000) + 1 <= fire)
                    fire();//使易燃方块着火
                double h = 0.55;
                AxisAlignedBB axisAlignedBB = new AxisAlignedBB(pos.getX() + 0.5, pos.getY() + h, pos.getZ() + 0.5, pos.getX() + 0.5, pos.getY() + h, pos.getZ() + 0.5).grow(0.35);
                List<LivingEntity> livingEntitys = world.getEntitiesWithinAABB(LivingEntity.class, axisAlignedBB);
                if (!livingEntitys.isEmpty()) {
                    livingEntitys.forEach(livingEntity -> {
                        livingEntity.setFire(1);//靠近火把会使生物着火
                    });
                }
            } else if (getBlockState().getBlock().equals(Blocks.WALL_TORCH)) {//靠墙火把
                if (world.getGameTime() % 20 == 0 && world.rand.nextInt(1000) + 1 <= fire)
                    WallFire();//使易燃方块着火
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

    public void fire() {
        for (Direction direction : Direction.values()) {
            if (world != null) {
                BlockPos offset = pos.offset(direction);
                if (canFire(world, offset, direction.getOpposite()))
                    placeFireBlock(pos);
            }
        }
    }

    public void WallFire() {
        Direction direction = getBlockState().get(WallTorchBlock.HORIZONTAL_FACING);
        Direction[] directions = new Direction[]{Direction.DOWN, Direction.UP};
        if (direction.equals(Direction.EAST) || direction.equals(Direction.WEST)) {
            directions = new Direction[]{Direction.DOWN, Direction.UP, Direction.SOUTH, Direction.NORTH};
        } else if (direction.equals(Direction.SOUTH) || direction.equals(Direction.NORTH)) {
            directions = new Direction[]{Direction.DOWN, Direction.UP, Direction.EAST, Direction.WEST};
        }

        if (world != null && world.getGameTime() % 6 == 0) {//四周闭塞将在火把位置起火
            for (Direction direction1 : directions) {
                BlockPos offset = pos.offset(direction1);
                BlockPos offset1 = offset.offset(direction.getOpposite());
                if (!world.getBlockState(offset1).getBlock().equals(Blocks.AIR) &&
                        world.getBlockState(offset).getBlock().equals(Blocks.AIR) && canFire(world, offset1, direction))
                    return;
            }
            BlockPos offset = pos.offset(direction.getOpposite());
            if (canFire(world, offset, direction)) {
                world.setBlockState(pos, Blocks.AIR.getDefaultState());
                placeFireBlock(pos);
            }
        } else if (world != null) {
            for (int i = 5; i >= 2; i--) {
                BlockPos offset = pos.offset(directions[i - 2]);
                BlockPos offset1 = offset.offset(direction.getOpposite());
                if (world.getGameTime() % i == 0 && world.getBlockState(offset).getBlock().equals(Blocks.AIR) &&
                        !world.getBlockState(offset1).getBlock().equals(Blocks.AIR) &&
                        canFire(world, offset1, direction)) {
                    placeFireBlock(offset);//四周着火
                    break;
                }
            }
        }
    }

    public void placeFireBlock(BlockPos firePos) {//放火
        if (world != null && !world.isRemote) {
            BlockState blockstate1 = AbstractFireBlock.getFireForPlacement(world, firePos);
            world.setBlockState(firePos, blockstate1, 11);
        }
    }

    public static boolean canFire(World world, BlockPos pos, Direction face) {//可以着火
        boolean b = world.getBlockState(pos).isFlammable(world, pos, face);
        if (!world.isRemote) {
            EnchantedBlocksData enchantedBlocksData = EnchantedBlocksData.get(world);
            if (enchantedBlocksData.getEnchantedBlock(pos, EnchantmentRegistry.FireProof.get()) > 0)
                b = false;//带有防火附魔的方块不会被烧毁
        }
        return b;
    }
}
