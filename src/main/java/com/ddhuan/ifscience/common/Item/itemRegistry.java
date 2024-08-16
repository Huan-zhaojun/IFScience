package com.ddhuan.ifscience.common.Item;

import com.ddhuan.ifscience.common.Fluid.FluidRegistry;
import com.ddhuan.ifscience.ifscience;
import net.minecraft.item.BucketItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

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
}
