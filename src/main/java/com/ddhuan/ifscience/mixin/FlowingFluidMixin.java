package com.ddhuan.ifscience.mixin;

import com.ddhuan.ifscience.common.Block.blockRegistry;
import com.ddhuan.ifscience.common.Enchantment.EnchantedBlocksData;
import com.ddhuan.ifscience.common.Enchantment.EnchantmentRegistry;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.fluid.FlowingFluid;
import net.minecraft.fluid.Fluid;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(FlowingFluid.class)
public abstract class FlowingFluidMixin extends Fluid {

    @Inject(method = "isBlocked", at = @At(value = "HEAD"), cancellable = true)
    private void isBlocked(IBlockReader worldIn, BlockPos pos, BlockState state, Fluid fluidIn, CallbackInfoReturnable<Boolean> cir) {
        if (worldIn instanceof World) {
            World world = (World) worldIn;
            if (!world.isRemote && EnchantedBlocksData.get(world).getEnchantedBlock(pos, EnchantmentRegistry.WaterProof.get()) > 0)
                cir.setReturnValue(false);//水流无法破坏有防水附魔的方块
        }
    }

    @ModifyArg(method = "flowInto", at = @At(value = "INVOKE", target = "Lnet/minecraft/fluid/FlowingFluid;beforeReplacingBlock(Lnet/minecraft/world/IWorld;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/BlockState;)V"))
    protected BlockState flowInto(BlockState blockState) {
        Block block = blockState.getBlock();
        if (block.equals(Blocks.TORCH) || block.equals(Blocks.WALL_TORCH))
            blockState = blockRegistry.extinguishedTorch.get().getDefaultState();//被水流冲毁的火把会掉落熄灭的火把
        return blockState;
    }
}
