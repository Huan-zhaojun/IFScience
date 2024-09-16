package com.ddhuan.ifscience.mixin.item;

import com.ddhuan.ifscience.common.Enchantment.EnchantedBlocksData;
import net.minecraft.item.BlockItem;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(BlockItem.class)
public abstract class BlockItemMixin extends Item {
    @Inject(method = "tryPlace", at = @At(value = "INVOKE", shift = At.Shift.AFTER, target = "Lnet/minecraft/block/Block;onBlockPlacedBy(Lnet/minecraft/world/World;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/BlockState;Lnet/minecraft/entity/LivingEntity;Lnet/minecraft/item/ItemStack;)V"))
    public void tryPlace(BlockItemUseContext context, CallbackInfoReturnable<ActionResultType> cir) {
        World world = context.getWorld();
        if (!world.isRemote) {
            ItemStack itemStack = context.getItem();
            BlockPos pos = context.getPos();
            //放置方块如果有附魔就存储
            EnchantedBlocksData enchantedBlocksData = EnchantedBlocksData.get(world);
            if (itemStack.hasTag())
                enchantedBlocksData.addEnchantedBlock(pos, itemStack);
            else if (!enchantedBlocksData.getEnchantedBlock(pos).isEmpty())//如果原方方块位置已经有附魔数据就清空
                enchantedBlocksData.removeEnchantedBlock(pos);
        }
    }

    public BlockItemMixin(Properties properties) {
        super(properties);
    }
}
