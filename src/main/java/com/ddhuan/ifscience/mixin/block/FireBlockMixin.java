package com.ddhuan.ifscience.mixin.block;

import com.ddhuan.ifscience.common.Enchantment.EnchantedBlocksData;
import com.ddhuan.ifscience.common.Enchantment.EnchantmentRegistry;
import net.minecraft.block.AbstractFireBlock;
import net.minecraft.block.FireBlock;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Random;

@Mixin(FireBlock.class)
public abstract class FireBlockMixin extends AbstractFireBlock {
    public FireBlockMixin(Properties properties, float fireDamage) {
        super(properties, fireDamage);
    }

    @Inject(method = "canCatchFire", at = @At(value = "HEAD"), cancellable = true, remap = false)
    public void canCatchFire(IBlockReader world, BlockPos pos, Direction face, CallbackInfoReturnable<Boolean> cir) {
        if (world instanceof World && !((World) world).isRemote) {
            EnchantedBlocksData enchantedBlocksData = EnchantedBlocksData.get((World) world);
            if (enchantedBlocksData != null && enchantedBlocksData.getEnchantedBlock(pos, EnchantmentRegistry.FireProof.get()) > 0)
                cir.setReturnValue(false);//带有防火附魔的方块不会被烧毁
        }
    }

    @Inject(method = "tryCatchFire", at = @At(value = "HEAD"), cancellable = true, remap = false)
    private void tryCatchFire(World worldIn, BlockPos pos, int chance, Random random, int age, Direction face, CallbackInfo ci) {
        if (!worldIn.isRemote) {
            EnchantedBlocksData enchantedBlocksData = EnchantedBlocksData.get(worldIn);
            if (enchantedBlocksData != null && enchantedBlocksData.getEnchantedBlock(pos, EnchantmentRegistry.FireProof.get()) > 0)
                ci.cancel();//带有防火附魔的方块不会被烧毁
        }
    }
}
