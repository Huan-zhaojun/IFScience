package com.ddhuan.ifscience.common.Block;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.HorizontalBlock;
import net.minecraft.block.material.Material;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.state.DirectionProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.world.Explosion;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;

import javax.annotation.Nullable;

public class HydrantBlock extends Block {
    private static final VoxelShape shape;
    private static final VoxelShape shapeSOUTH;
    private static final VoxelShape shapeWEST;
    private static final VoxelShape shapeNORTH;
    public static final DirectionProperty FACING = HorizontalBlock.HORIZONTAL_FACING;

    static {//碰撞箱设置
        VoxelShape base = Block.makeCuboidShape(4, 0, 4, 12, 1, 12);
        VoxelShape column = Block.makeCuboidShape(6, 1, 6, 10, 12, 10);
        VoxelShape cover1 = Block.makeCuboidShape(5, 12, 5, 11, 13, 11);
        VoxelShape cover2 = Block.makeCuboidShape(6, 13, 6, 10, 14, 10);
        VoxelShape cover3 = Block.makeCuboidShape(7, 14, 7, 9, 15, 9);
        VoxelShape side = Block.makeCuboidShape(7, 9, 4, 9, 11, 12);
        VoxelShape water = Block.makeCuboidShape(4, 9, 7, 6, 11, 9);
        shape = VoxelShapes.or(base, column, cover1, cover2, cover3, side, water);
        VoxelShape sideSOUTH = Block.makeCuboidShape(4, 9, 7, 12, 11, 9);
        VoxelShape waterSOUTH = Block.makeCuboidShape(7, 9, 4, 9, 11, 6);
        shapeSOUTH = VoxelShapes.or(base, column, cover1, cover2, cover3, sideSOUTH, waterSOUTH);
        VoxelShape sideWEST = Block.makeCuboidShape(7, 9, 12, 9, 11, 4);
        VoxelShape waterWEST = Block.makeCuboidShape(10, 9, 7, 12, 11, 9);
        shapeWEST = VoxelShapes.or(base, column, cover1, cover2, cover3, sideWEST, waterWEST);
        VoxelShape sideNORTH = Block.makeCuboidShape(4, 9, 7, 12, 11, 9);
        VoxelShape waterNORTH = Block.makeCuboidShape(7, 9, 10, 9, 11, 12);
        shapeNORTH = VoxelShapes.or(base, column, cover1, cover2, cover3, sideNORTH, waterNORTH);
    }

    public HydrantBlock() {
        super(Properties.create(Material.ROCK).hardnessAndResistance(5f).notSolid());
        this.setDefaultState(this.stateContainer.getBaseState().with(FACING, Direction.EAST));
    }

    @Override
    public VoxelShape getShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context) {
        switch (state.get(FACING)) {
            case EAST:
                return shape; // 默认朝东，无需旋转
            case SOUTH:
                return shapeSOUTH;
            case WEST:
                return shapeWEST;
            case NORTH:
                return shapeNORTH;
            default:
                return shape;
        }
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockItemUseContext context) {
        return this.getDefaultState().with(FACING, context.getPlacementHorizontalFacing());
    }

    @Override
    protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder) {
        builder.add(FACING);
    }

    @Override
    public void onPlayerDestroy(IWorld worldIn, BlockPos pos, BlockState state) {

    }

    @Override
    public void onExplosionDestroy(World worldIn, BlockPos pos, Explosion explosionIn) {

    }
}