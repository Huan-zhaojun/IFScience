package com.ddhuan.ifscience.mixin;

import com.ddhuan.ifscience.common.Enchantment.EnchantedBlocksData;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IItemProvider;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Map;

@Mixin(Block.class)
public abstract class BlockMixin extends AbstractBlock implements IItemProvider, net.minecraftforge.common.extensions.IForgeBlock {
    public BlockMixin(Properties properties) {
        super(properties);
    }

    //玩家破坏附魔的方块会掉落带有附魔nbt的方块物品
    @Inject(method = "spawnAsEntity", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/item/ItemEntity;<init>(Lnet/minecraft/world/World;DDDLnet/minecraft/item/ItemStack;)V"))
    private static void spawnAsEntity(World worldIn, BlockPos pos, ItemStack stack, CallbackInfo ci) {
        Map<Enchantment, Integer> enchMap = EnchantedBlocksData.get(worldIn).getEnchantedBlock(pos);
        if (!enchMap.isEmpty())
            EnchantmentHelper.setEnchantments(enchMap, stack);
    }
}
