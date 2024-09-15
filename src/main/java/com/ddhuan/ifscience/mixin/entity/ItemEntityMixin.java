package com.ddhuan.ifscience.mixin.entity;

import com.ddhuan.ifscience.common.Enchantment.EnchantmentRegistry;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ItemEntity.class)
public abstract class ItemEntityMixin extends Entity {
    public ItemEntityMixin(EntityType<?> entityTypeIn, World worldIn) {
        super(entityTypeIn, worldIn);
    }

    @Shadow
    public abstract ItemStack getItem();

    @Inject(method = "isImmuneToFire", at = @At(value = "HEAD"), cancellable = true)
    public void isImmuneToFire(CallbackInfoReturnable<Boolean> cir) {
        if (EnchantmentHelper.getEnchantmentLevel(EnchantmentRegistry.FireProof.get(), getItem()) > 0)
            cir.setReturnValue(true);//当物品具有防火附魔时不会被烧掉
    }
}
