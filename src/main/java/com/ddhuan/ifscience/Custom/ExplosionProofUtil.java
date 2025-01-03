package com.ddhuan.ifscience.Custom;

import com.ddhuan.ifscience.Config;
import com.ddhuan.ifscience.common.Block.blockRegistry;
import com.ddhuan.ifscience.common.Enchantment.EnchantedBlocksData;
import com.ddhuan.ifscience.common.Enchantment.EnchantmentRegistry;
import com.ddhuan.ifscience.common.SoundEventRegistry;
import com.ddhuan.ifscience.network.Client.BlockBreakProgressPack;
import com.ddhuan.ifscience.network.Network;
import com.ddhuan.ifscience.network.SoundHandlerNetHelper;
import net.minecraft.block.*;
import net.minecraft.state.IntegerProperty;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.fml.network.PacketDistributor;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Random;

import static com.ddhuan.ifscience.Config.*;

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
                SoundHandlerNetHelper.stop(SoundEventRegistry.glassBurst.get().getName(), null);//停止玻璃开裂声
                return;
            }
            if (isCool(worldIn, pos)) {
                if (i < 12) ++i;
                if (i >= 12) {
                    SoundHandlerNetHelper.stop(SoundEventRegistry.glassBurst.get().getName(), null);//停止玻璃开裂声
                    worldIn.destroyBlock(pos, false);//最终导致破裂
                    Network.INSTANCE.send(PacketDistributor.ALL.noArg(), new BlockBreakProgressPack(pos, 10));
                    return;
                }
                renewState(block, state, worldIn, pos, i);//逐渐裂开

                //播放玻璃开裂声
                SoundHandlerNetHelper.play(pos.getX(), pos.getY(), pos.getZ(),
                        SoundEventRegistry.glassBurst.get(), SoundCategory.VOICE,
                        new Float(GLASS_BURST_SOUND_VOLUME.get()), 1F, true, 0);
                return;
            }
            //不存在冰凉的东西
            renewState(block, state, worldIn, pos, --i);//逐渐恢复回去
            SoundHandlerNetHelper.stop(SoundEventRegistry.glassBurst.get().getName(), null);//停止玻璃开裂声
        }
    }

    //尝试添加计划刻:玻璃冷热导致爆裂
    public static void tryScheduleBurstTick(Block block, BlockState state, World worldIn, BlockPos pos) {
        if (!worldIn.isRemote && state.get(BreakProgress) == 0) {
            //防爆玻璃或者防爆附魔
            if (EnchantedBlocksData.get(worldIn).getEnchantedBlock(pos, EnchantmentRegistry.ExplosionProof.get()) > 0 ||
                    worldIn.getBlockState(pos).getBlock() == blockRegistry.reinforcedGlass.get())
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
        int scheduledTime = GLASS_BURST_TIME.get();
        scheduledTime = scheduledTime > 3 ? scheduledTime + (random.nextInt((2 * (scheduledTime / 3)) + 1) - (scheduledTime / 3)) : scheduledTime;
        worldIn.getPendingBlockTicks().scheduleTick(pos, block, scheduledTime);
    }

    public static final List<? extends String> heatSourceBlocksName =
            Arrays.asList("minecraft:fire", "minecraft:lava", "minecraft:campfire"),
            coolSourceBlocksName = Arrays.asList("minecraft:water", "minecraft:blue_ice", "minecraft:packed_ice");
    public static HashSet<ResourceLocation> heatSourceBlocks = new HashSet<>(), coolSourceBlocks = new HashSet<>();

    //检测是否有热源
    public static boolean isburn(World worldIn, BlockPos pos) {
        if (heatSourceBlocks.isEmpty()) heatSourceBlocks = Config.ListConfig.getBlockSet(HEAT_SOURCE_BLOCKS.get());
        for (Direction direction : Direction.values()) {
            ResourceLocation resourceLocation = worldIn.getBlockState(pos.offset(direction)).getBlock().getRegistryName();
            if (heatSourceBlocks.contains(resourceLocation)) return true;
        }
        return false;
    }

    //检测是否有冷源
    public static boolean isCool(World worldIn, BlockPos pos) {
        if (coolSourceBlocks.isEmpty()) coolSourceBlocks = Config.ListConfig.getBlockSet(COOL_SOURCE__BLOCKS.get());
        for (Direction direction : Direction.values()) {
            Block block = worldIn.getBlockState(pos.offset(direction)).getBlock();
            ResourceLocation resourceLocation = block.getRegistryName();
            if (coolSourceBlocks.contains(resourceLocation)) return true;
            if (block instanceof IceBlock) return true;
        }
        return false;
    }

    public static void updateConfig_StaticValue() {
        heatSourceBlocks = Config.ListConfig.getBlockSet(HEAT_SOURCE_BLOCKS.get());
        coolSourceBlocks = Config.ListConfig.getBlockSet(COOL_SOURCE__BLOCKS.get());
    }
}
