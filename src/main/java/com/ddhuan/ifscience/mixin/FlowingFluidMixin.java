package com.ddhuan.ifscience.mixin;

import com.ddhuan.ifscience.common.Enchantment.EnchantedBlocksData;
import com.ddhuan.ifscience.common.Enchantment.EnchantmentRegistry;
import net.minecraft.block.BlockState;
import net.minecraft.fluid.FlowingFluid;
import net.minecraft.fluid.Fluid;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(FlowingFluid.class)
public abstract class FlowingFluidMixin extends Fluid {

    @Inject(method = "isBlocked", at = @At(value = "HEAD"), cancellable = true)
    private void isBlocked(IBlockReader worldIn, BlockPos pos, BlockState state, Fluid fluidIn, CallbackInfoReturnable<Boolean> cir) {
        if (worldIn instanceof World) {
            World world = (World) worldIn;
            if (!world.isRemote && EnchantedBlocksData.get(world).getEnchantedBlock(pos, EnchantmentRegistry.WaterProof.get()) != null)
                cir.setReturnValue(false);
        }
    }
}
