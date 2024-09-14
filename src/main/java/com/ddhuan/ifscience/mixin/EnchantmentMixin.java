package com.ddhuan.ifscience.mixin;

import net.minecraft.enchantment.Enchantment;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import static com.ddhuan.ifscience.Config.ENCHANTING_ALL;

@Mixin(Enchantment.class)
public abstract class EnchantmentMixin extends net.minecraftforge.registries.ForgeRegistryEntry<Enchantment> {

    @Final
    @Inject(method = "isCompatibleWith", at = @At(value = "HEAD"), cancellable = true)
    public void isCompatibleWith(Enchantment enchantmentIn, CallbackInfoReturnable<Boolean> cir) {
        if (ENCHANTING_ALL.get())
            cir.setReturnValue(true);//附魔不受魔咒冲突的限制
    }
}
