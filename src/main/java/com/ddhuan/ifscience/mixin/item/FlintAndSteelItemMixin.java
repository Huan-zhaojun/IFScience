package com.ddhuan.ifscience.mixin.item;

import com.ddhuan.ifscience.common.Block.ExtinguishedTorch;
import com.ddhuan.ifscience.common.Block.WallExtinguishedTorch;
import com.ddhuan.ifscience.common.Block.blockRegistry;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.WallTorchBlock;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.FlintAndSteelItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUseContext;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(FlintAndSteelItem.class)
public abstract class FlintAndSteelItemMixin extends Item {
    public FlintAndSteelItemMixin(Properties properties) {
        super(properties);
    }

    @Inject(method = "onItemUse", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/math/BlockPos;offset(Lnet/minecraft/util/Direction;)Lnet/minecraft/util/math/BlockPos;"), cancellable = true, locals = LocalCapture.CAPTURE_FAILEXCEPTION)
    public void onItemUse(ItemUseContext context, CallbackInfoReturnable<ActionResultType> cir, PlayerEntity playerentity, World world, BlockPos blockpos, BlockState blockstate) {
        //打火石能打着熄灭的火把
        if (blockstate.getBlock() instanceof ExtinguishedTorch) {
            BlockState blockState = world.getBlockState(blockpos);
            BlockState blockState1 = Blocks.TORCH.getDefaultState();
            if (blockState.getBlock().equals(blockRegistry.wallExtinguishedTorch.get()))
                blockState1 = Blocks.WALL_TORCH.getDefaultState().with(WallTorchBlock.HORIZONTAL_FACING, blockState.get(WallExtinguishedTorch.HORIZONTAL_FACING));
            world.setBlockState(blockpos, blockState1, 11);
            world.playSound(playerentity, blockpos, SoundEvents.ITEM_FLINTANDSTEEL_USE, SoundCategory.BLOCKS, 1.0F, random.nextFloat() * 0.4F + 0.8F);
            ItemStack itemstack = context.getItem();
            if (playerentity instanceof ServerPlayerEntity) {
                CriteriaTriggers.PLACED_BLOCK.trigger((ServerPlayerEntity) playerentity, blockpos, itemstack);
                itemstack.damageItem(1, playerentity, (player) -> {
                    player.sendBreakAnimation(context.getHand());
                });
            }
            cir.setReturnValue(ActionResultType.func_233537_a_(world.isRemote()));
        }
    }
}
