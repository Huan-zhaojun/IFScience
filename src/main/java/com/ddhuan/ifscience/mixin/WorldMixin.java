package com.ddhuan.ifscience.mixin;

import com.ddhuan.ifscience.common.Enchantment.EnchantedBlocksData;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(World.class)
public abstract class WorldMixin extends net.minecraftforge.common.capabilities.CapabilityProvider<World> implements IWorld, AutoCloseable, net.minecraftforge.common.extensions.IForgeWorld {
    protected WorldMixin(Class<World> baseClass) {
        super(baseClass);
    }

    //移除或破坏方块的时候移除可能存在的附魔数据
    @Inject(method = "removeBlock", at = @At(value = "HEAD"))
    public void removeBlock(BlockPos pos, boolean isMoving, CallbackInfoReturnable<Boolean> cir) {
        World world = (World) (Object) this;
        if (!world.isRemote)
            EnchantedBlocksData.get(world).removeEnchantedBlock(pos);
    }

    @Inject(method = "destroyBlock", at = @At(value = "HEAD"))
    public void destroyBlock(BlockPos pos, boolean dropBlock, Entity entity, int recursionLeft, CallbackInfoReturnable<Boolean> cir) {
        World world = (World) (Object) this;
        if (!world.isRemote)
            EnchantedBlocksData.get(world).removeEnchantedBlock(pos);
    }
}
