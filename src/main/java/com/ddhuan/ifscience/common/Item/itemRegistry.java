package com.ddhuan.ifscience.common.Item;

import com.ddhuan.ifscience.Custom.magnetUtil;
import com.ddhuan.ifscience.Custom.rainingUtil;
import com.ddhuan.ifscience.ifscience;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class itemRegistry {
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, ifscience.MOD_ID);
    public static final ItemGroup ifScience = new ItemGroup("ifscience") {
        @Override
        public ItemStack createIcon() {
            return new ItemStack(horseshoeMagnet.get());
        }
    };

    public static RegistryObject<Item> puddleFluidBucket = rainingUtil.puddleFluidBucket;
    public static RegistryObject<Item> horseshoeMagnet = magnetUtil.horseshoeMagnet;//磁铁
}
