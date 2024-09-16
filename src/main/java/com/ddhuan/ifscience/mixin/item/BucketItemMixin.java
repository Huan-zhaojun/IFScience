package com.ddhuan.ifscience.mixin.item;

import com.ddhuan.ifscience.common.Fluid.FluidRegistry;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.block.BlockState;
import net.minecraft.block.IBucketPickupHandler;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.BucketItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.stats.Stats;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.RayTraceContext;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(BucketItem.class)
public abstract class BucketItemMixin extends Item {
    @Shadow
    private Fluid containedBlock;

    public BucketItemMixin(Properties properties) {
        super(properties);
    }

    @Inject(method = "onItemRightClick", at = @At(value = "RETURN", ordinal = 4), cancellable = true, locals = LocalCapture.CAPTURE_FAILEXCEPTION)
    public void onItemRightClick(World worldIn, PlayerEntity playerIn, Hand handIn, CallbackInfoReturnable<ActionResult<ItemStack>> cir, ItemStack itemstack) {
        RayTraceResult raytraceresult = rayTrace(worldIn, playerIn, this.containedBlock == Fluids.EMPTY ? RayTraceContext.FluidMode.SOURCE_ONLY : RayTraceContext.FluidMode.NONE);
        BlockRayTraceResult blockraytraceresult = (BlockRayTraceResult) raytraceresult;
        BlockPos blockpos = blockraytraceresult.getPos();
        BlockPos blockpos1 = rayTrace(worldIn, playerIn, RayTraceContext.FluidMode.ANY).getPos();//获取包括不是水源的流水方块的位置
        BlockState blockstate = worldIn.getBlockState(blockpos);
        BlockState blockstate1 = worldIn.getBlockState(blockpos1);
        if (blockstate.getBlock() instanceof IBucketPickupHandler) ;
        //不一定只有水源方块才能被水桶收回，水流方块现在或许也可以被收回
        else if (blockstate1.getBlock() instanceof IBucketPickupHandler) {
            //对应的自定义的FlowingFluidBlock当LEVEL !=0(不是水源)需要重写pickupFluid才能返回不是EMPTY流体
            Fluid fluid1 = ((IBucketPickupHandler)blockstate1.getBlock()).pickupFluid(worldIn, blockpos1, blockstate1);
            if (fluid1.equals(FluidRegistry.puddleFluidFlowing.get())/*积水能被收回桶*/) {
                if (fluid1 != Fluids.EMPTY) {
                    playerIn.addStat(Stats.ITEM_USED.get(this));

                    SoundEvent soundevent1 = this.containedBlock.getAttributes().getFillSound();
                    if (soundevent1 == null)
                        soundevent1 = fluid1.isIn(FluidTags.LAVA) ? SoundEvents.ITEM_BUCKET_FILL_LAVA : SoundEvents.ITEM_BUCKET_FILL;
                    playerIn.playSound(soundevent1, 1.0F, 1.0F);
                    ItemStack itemstack1 = DrinkHelper.fill(itemstack, playerIn, new ItemStack(fluid1.getFilledBucket()));
                    if (!worldIn.isRemote) {
                        CriteriaTriggers.FILLED_BUCKET.trigger((ServerPlayerEntity) playerIn, new ItemStack(fluid1.getFilledBucket()));
                    }

                    cir.setReturnValue(ActionResult.func_233538_a_(itemstack1, worldIn.isRemote()));
                }
            }
        }
    }
}
