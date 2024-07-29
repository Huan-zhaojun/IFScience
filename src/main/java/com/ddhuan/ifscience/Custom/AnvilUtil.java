package com.ddhuan.ifscience.Custom;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

//坠落铁砧砸碎方块
public class AnvilUtil {
    public static void destroyBlock(BlockState fallTile, World world, BlockPos blockpos) {
        if (fallTile.getBlock().matchesBlock(Blocks.ANVIL)) {
            Block block = world.getBlockState(blockpos).getBlock();
            Block[] blocks = {Blocks.GLASS, Blocks.GOLD_BLOCK, Blocks.TURTLE_EGG,Blocks.TORCH,Blocks.CAMPFIRE};
            for (Block block1 : blocks) {
                if (block1.equals(block)) {
                    world.destroyBlock(blockpos, true);
                }
            }
        }
    }
}
