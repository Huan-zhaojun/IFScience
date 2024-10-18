package com.ddhuan.ifscience.mixin.block;

import com.ddhuan.ifscience.common.Enchantment.EnchantedBlocksData;
import com.ddhuan.ifscience.common.Enchantment.EnchantmentRegistry;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.AbstractGlassBlock;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(AbstractBlock.AbstractBlockState.class)
public abstract class AbstractBlockStateMixin {
    @Inject(method = "getBlockHardness", at = @At("HEAD"), cancellable = true)
    public void getBlockHardness(IBlockReader worldIn, BlockPos pos, CallbackInfoReturnable<Float> cir) {
        if (worldIn instanceof World) {
            World world = (World) worldIn;
            //玻璃具有防爆附魔时硬度变硬
            if (world.getBlockState(pos).getBlock() instanceof AbstractGlassBlock) {
                if (!world.isRemote && EnchantedBlocksData.get(world).getEnchantedBlock(pos, EnchantmentRegistry.ExplosionProof.get()) > 0) {
                    cir.setReturnValue(5F);
                } else if (world.isRemote && EnchantedBlocksData.EnchantedBlocksClientData
                        .getEnchantedBlock(pos, EnchantmentRegistry.ExplosionProof.get()) > 0) {
                    cir.setReturnValue(5F);
                }
            }
        }
    }
}
