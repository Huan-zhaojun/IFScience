package com.ddhuan.ifscience.Custom;

import com.ddhuan.ifscience.common.Enchantment.EnchantedBlocksData;
import com.ddhuan.ifscience.common.Enchantment.EnchantmentRegistry;
import com.ddhuan.ifscience.network.Client.BlockBreakProgressPack;
import com.ddhuan.ifscience.network.Network;
import net.minecraft.block.*;
import net.minecraft.state.IntegerProperty;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.fml.network.PacketDistributor;

import java.util.Random;

public final class ExplosionProofUtil {
    private ExplosionProofUtil() {
    }

    private static final Random random = new Random();

    public static final IntegerProperty BreakProgress = IntegerProperty.create("break_progress", 0, 12);

    public static boolean isGlass(Block block) {//是玻璃吗
        return block instanceof AbstractGlassBlock || block instanceof StainedGlassPaneBlock || block.equals(Blocks.GLASS_PANE);
    }

    public static void glassTick(Block block, BlockState state, ServerWorld worldIn, BlockPos pos) {
        if (!worldIn.isRemote) {
            int i = state.get(BreakProgress);
            if (i < 1) return;
            if (!isburn(worldIn, pos)) {//不存在热的东西
                renewState(block, state, worldIn, pos, --i);//逐渐恢复回去
                return;
            }
            if (isCool(worldIn, pos)) {
                if (i < 12) ++i;
                if (i >= 12) {
                    worldIn.destroyBlock(pos, false);//最终导致破裂
                    Network.INSTANCE.send(PacketDistributor.ALL.noArg(), new BlockBreakProgressPack(pos, 10));
                    return;
                }
                renewState(block, state, worldIn, pos, i);//逐渐裂开
                return;
            }
            //不存在冰凉的东西
            renewState(block, state, worldIn, pos, --i);//逐渐恢复回去
        }
    }

    //尝试添加计划刻:玻璃冷热导致爆裂
    public static void tryScheduleBurstTick(Block block, BlockState state, World worldIn, BlockPos pos) {
        if (!worldIn.isRemote && state.get(BreakProgress) == 0) {
            if (EnchantedBlocksData.get(worldIn).getEnchantedBlock(pos, EnchantmentRegistry.ExplosionProof.get()) > 0)
                return;
            if (!isburn(worldIn, pos)) return;
            if (isCool(worldIn, pos))
                renewState(block, state, worldIn, pos, 1);//计划更新：开始裂开
        }
    }

    //玻璃方块状态更新
    public static void renewState(Block block, BlockState state, World worldIn, BlockPos pos, int i) {
        worldIn.setBlockState(pos, state.with(BreakProgress, i));
        Network.INSTANCE.send(PacketDistributor.ALL.noArg(), new BlockBreakProgressPack(pos, i - 2));
        //下一次计划更新
        int scheduledTime = 10;
        scheduledTime = scheduledTime > 3 ? scheduledTime + (random.nextInt((2 * (scheduledTime / 3)) + 1) - (scheduledTime / 3)) : scheduledTime;
        worldIn.getPendingBlockTicks().scheduleTick(pos, block, scheduledTime);
    }

    //检测是否有热源
    public static boolean isburn(World worldIn, BlockPos pos) {
        for (Direction direction : Direction.values()) {
            Block block = worldIn.getBlockState(pos.offset(direction)).getBlock();
            if (block.equals(Blocks.FIRE) || block.equals(Blocks.CAMPFIRE) || block.equals(Blocks.LAVA)) {
                return true;
            }
        }
        return false;
    }

    //检测是否有冷源
    public static boolean isCool(World world, BlockPos pos) {
        for (Direction direction : Direction.values()) {
            Block block = world.getBlockState(pos.offset(direction)).getBlock();
            if (block.equals(Blocks.WATER) || block.equals(Blocks.BLUE_ICE) || block.equals(Blocks.PACKED_ICE) ||
                    block instanceof IceBlock) {
                return true;
            }
        }
        return false;
    }
}
