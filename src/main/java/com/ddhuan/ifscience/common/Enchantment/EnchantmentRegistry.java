package com.ddhuan.ifscience.common.Enchantment;

import com.ddhuan.ifscience.ifscience;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public final class EnchantmentRegistry {
    public static final DeferredRegister<Enchantment> ENCHANTMENTS = DeferredRegister.create(ForgeRegistries.ENCHANTMENTS, ifscience.MOD_ID);

    //方块防水附魔
    public static RegistryObject<Enchantment> waterproof = ENCHANTMENTS.register("waterproof",
            () -> new Enchantment(Enchantment.Rarity.UNCOMMON, ModEnchantmentType.BLOCK, new EquipmentSlotType[]{EquipmentSlotType.MAINHAND}) {
                @Override
                public ITextComponent getDisplayName(int level) {
                    return new TranslationTextComponent(this.getName()).mergeStyle(TextFormatting.BLUE);
                }
            });
}