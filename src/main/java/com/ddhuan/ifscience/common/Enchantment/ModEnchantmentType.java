package com.ddhuan.ifscience.common.Enchantment;


import net.minecraft.enchantment.EnchantmentType;
import net.minecraft.item.BlockItem;

public class ModEnchantmentType {
    public static final EnchantmentType ALL = EnchantmentType.create("ALL", item -> true);
    public static final EnchantmentType BLOCK = EnchantmentType.create("BLOCK", item -> item instanceof BlockItem);
}
