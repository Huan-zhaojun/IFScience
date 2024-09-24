package com.ddhuan.ifscience.mixin;

import com.ddhuan.ifscience.common.Enchantment.EnchantedBlocksData;
import com.ddhuan.ifscience.common.Enchantment.EnchantmentRegistry;
import net.minecraft.block.BlockState;
import net.minecraft.fluid.FluidState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.Explosion;
import net.minecraft.world.ExplosionContext;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.Optional;

@Mixin(Explosion.class)
public abstract class ExplosionMixin {
    @Shadow
    @Final
    private World world;

    @Shadow
    @Final
    private ExplosionContext context;

    @Redirect(method = "doExplosionA", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/ExplosionContext;getExplosionResistance(Lnet/minecraft/world/Explosion;Lnet/minecraft/world/IBlockReader;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/BlockState;Lnet/minecraft/fluid/FluidState;)Ljava/util/Optional;"))
    public Optional<Float> doExplosionA(ExplosionContext instance, Explosion explosion, IBlockReader reader, BlockPos pos, BlockState state, FluidState fluid) {
        if (EnchantedBlocksData.get(world).getEnchantedBlock(pos, EnchantmentRegistry.ExplosionProof.get()) > 0)
            return Optional.of(10000F);//方块防爆附魔抵抗爆炸射线
        return instance.getExplosionResistance(explosion, this.world, pos, state, fluid);
    }
}
