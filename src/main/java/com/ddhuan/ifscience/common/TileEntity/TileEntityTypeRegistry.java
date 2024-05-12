package com.ddhuan.ifscience.common.TileEntity;

import com.ddhuan.ifscience.common.Block.blockRegistry;
import com.ddhuan.ifscience.common.Fluid.puddleFluidTileEntity;
import com.ddhuan.ifscience.ifscience;
import net.minecraft.tileentity.TileEntityType;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class TileEntityTypeRegistry {
    public static final DeferredRegister<TileEntityType<?>> TILE_ENTITIES = DeferredRegister.create(ForgeRegistries.TILE_ENTITIES, ifscience.MOD_ID);
    public static final RegistryObject<TileEntityType<puddleFluidTileEntity>> puddleFluidTileEntity = TILE_ENTITIES.register("puddle_fluid_tileentity", () -> TileEntityType.Builder.create(puddleFluidTileEntity::new, blockRegistry.puddleFluid.get()).build(null));
}
