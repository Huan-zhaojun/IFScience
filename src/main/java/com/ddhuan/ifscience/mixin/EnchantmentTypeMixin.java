package com.ddhuan.ifscience.mixin;

import net.minecraft.enchantment.EnchantmentType;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.gen.Invoker;

import java.util.ArrayList;
import java.util.Arrays;

//弃用
@Mixin(EnchantmentType.class)
public class EnchantmentTypeMixin implements net.minecraftforge.common.IExtensibleEnum {
    //获取原始的$VALUES数组
    @Shadow
    @Final
    @Mutable
    private static EnchantmentType[] $VALUES;

    private static final EnchantmentType ALL = EnchantmentType$$addVariant("ALL", item -> true);
    private static final EnchantmentType BLOCK = EnchantmentType$$addVariant("BLOCK", item -> item instanceof BlockItem);

    @Invoker("<init>")
    public static EnchantmentType EnchantmentType$invokeInit(String name, int ordinal, java.util.function.Predicate<Item> delegate) {
        throw new AssertionError();
    }

    //添加新的枚举值
    private static EnchantmentType EnchantmentType$$addVariant(String name, java.util.function.Predicate<Item> delegate) {
        ArrayList<EnchantmentType> variants = new ArrayList<>(Arrays.asList(EnchantmentTypeMixin.$VALUES));
        EnchantmentType instrument = EnchantmentType$invokeInit(name, variants.get(variants.size() - 1).ordinal() + 1, delegate);
        variants.add(instrument);
        EnchantmentTypeMixin.$VALUES = variants.toArray(new EnchantmentType[0]);
        return instrument;
    }
}
