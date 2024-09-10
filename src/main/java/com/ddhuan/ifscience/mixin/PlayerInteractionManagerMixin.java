package com.ddhuan.ifscience.mixin;

import com.ddhuan.ifscience.common.Enchantment.EnchantedBlocksData;
import net.minecraft.server.management.PlayerInteractionManager;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.server.ServerWorld;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PlayerInteractionManager.class)
public class PlayerInteractionManagerMixin {
    @Shadow
    public ServerWorld world;

    //移除方块的时候移除可能存在的附魔数据
    @Inject(method = "tryHarvestBlock", at = @At(value = "RETURN", ordinal = 4))
    public void tryHarvestBlock$Mixin1(BlockPos pos, CallbackInfoReturnable<Boolean> cir) {
        EnchantedBlocksData.get(world).removeEnchantedBlock(pos);
    }

    @Inject(method = "tryHarvestBlock", at = @At(value = "RETURN", ordinal = 5))
    public void tryHarvestBlock$Mixin2(BlockPos pos, CallbackInfoReturnable<Boolean> cir) {
        EnchantedBlocksData.get(world).removeEnchantedBlock(pos);
    }
}
