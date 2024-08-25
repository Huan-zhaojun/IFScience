package com.ddhuan.ifscience.common.Item;

import com.ddhuan.ifscience.common.Block.blockRegistry;
import com.ddhuan.ifscience.common.Fluid.FluidRegistry;
import com.ddhuan.ifscience.ifscience;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.*;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

import javax.annotation.Nullable;
import java.util.List;

import static net.minecraft.item.Items.BUCKET;

public class itemRegistry {
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, ifscience.MOD_ID);
    public static final ItemGroup ifScience = new ItemGroup("ifscience") {
        @Override
        public ItemStack createIcon() {
            return new ItemStack(horseshoeMagnet.get());
        }
    };

    public static RegistryObject<Item> puddleFluidBucket = ITEMS.register("puddle_fluid_bucket",
            () -> new BucketItem(FluidRegistry.puddleFluid, new Item.Properties()
                    .group(itemRegistry.ifScience).containerItem(BUCKET).maxStackSize(1)));

    public static RegistryObject<Item> horseshoeMagnet = ITEMS.register("horseshoe_magnet",
            () -> new HorseshoeMagnetItem(new Item.Properties().group(itemRegistry.ifScience)));//磁铁

    public static RegistryObject<Item> iceRail = ITEMS.register("ice_rail",
            () -> new BlockItem(blockRegistry.iceRail.get(), new Item.Properties().group(itemRegistry.ifScience)){
                @Override
                public void addInformation(ItemStack stack, @Nullable World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn) {
                    super.addInformation(stack, worldIn, tooltip, flagIn);
                    tooltip.add(new TranslationTextComponent("text.ifscience.iceRail1").mergeStyle(TextFormatting.AQUA));
                    tooltip.add(new TranslationTextComponent("text.ifscience.iceRail2").mergeStyle(TextFormatting.DARK_AQUA));
                }
            });
}
