package com.ddhuan.ifscience.common.Item;

import com.ddhuan.ifscience.common.Enchantment.EnchantmentRegistry;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentData;
import net.minecraft.item.EnchantedBookItem;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.fml.RegistryObject;

import java.util.stream.Collectors;

public class ModEnchantedBookItem extends EnchantedBookItem {
    public ModEnchantedBookItem(Properties builder) {
        super(builder);
    }

    public static ItemStack getEnchantedItemStack(EnchantmentData enchantData) {
        ItemStack itemstack = new ItemStack(itemRegistry.modEnchantedBook.get())
                .setDisplayName(new StringTextComponent(
                        enchantData.enchantment.getDisplayName(enchantData.enchantmentLevel).getString())
                        .appendSibling(itemRegistry.modEnchantedBook.get().getName()));

        addEnchantment(itemstack, enchantData);
        return itemstack;
    }

    @Override
    public void fillItemGroup(ItemGroup group, NonNullList<ItemStack> items) {
        if (this.isInGroup(group)) {
            for (Enchantment enchantment : EnchantmentRegistry.ENCHANTMENTS.getEntries().stream().map(RegistryObject::get).collect(Collectors.toList())) {
                if (enchantment.type != null) {
                    for (int i = enchantment.getMinLevel(); i <= enchantment.getMaxLevel(); ++i) {
                        items.add(getEnchantedItemStack(new EnchantmentData(enchantment, i)));
                    }
                }
            }
        }
    }
}
