package com.ddhuan.ifscience.common.Fluid;

import com.ddhuan.ifscience.common.Block.blockRegistry;
import com.ddhuan.ifscience.common.Item.itemRegistry;
import com.ddhuan.ifscience.ifscience;
import net.minecraft.fluid.Fluid;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.FluidAttributes;
import net.minecraftforge.fluids.ForgeFlowingFluid;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class FluidRegistry {
    public static final ResourceLocation STILL_WATER_TEXTURE = new ResourceLocation("block/water_still");
    public static final ResourceLocation FLOWING_WATER_TEXTURE = new ResourceLocation("block/water_flow");

    public static final DeferredRegister<Fluid> FLUIDS = DeferredRegister.create(ForgeRegistries.FLUIDS, ifscience.MOD_ID);

    public static RegistryObject<puddleForgeFlowingFluid.Source> puddleFluid = FLUIDS.register("puddle_fluid",
            () -> new puddleForgeFlowingFluid.Source(FluidRegistry.PROPERTIES));
    public static RegistryObject<puddleForgeFlowingFluid.Flowing> puddleFluidFlowing = FLUIDS.register("puddle_fluid_flowing",
            () -> new puddleForgeFlowingFluid.Flowing(FluidRegistry.PROPERTIES));
    public static ForgeFlowingFluid.Properties PROPERTIES = new ForgeFlowingFluid.Properties(
            puddleFluid,
            puddleFluidFlowing,
            FluidAttributes.builder(STILL_WATER_TEXTURE, FLOWING_WATER_TEXTURE).color(0xFF05a3fd))
            .bucket(itemRegistry.puddleFluidBucket).block(blockRegistry.puddleFluid)
            .levelDecreasePerBlock(2)
            .explosionResistance(100F);
}
