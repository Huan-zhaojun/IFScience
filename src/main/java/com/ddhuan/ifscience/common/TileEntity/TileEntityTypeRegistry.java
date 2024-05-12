package com.ddhuan.ifscience.common.TileEntity;

import com.ddhuan.ifscience.common.Block.blockRegistry;
import com.ddhuan.ifscience.common.Fluid.LavaFluidTileEntity;
import com.ddhuan.ifscience.common.Fluid.puddleFluidTileEntity;
import com.ddhuan.ifscience.ifscience;
import net.minecraft.block.Blocks;
import net.minecraft.tileentity.TileEntityType;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class TileEntityTypeRegistry {
    public static final DeferredRegister<TileEntityType<?>> TILE_ENTITIES = DeferredRegister.create(ForgeRegistries.TILE_ENTITIES, ifscience.MOD_ID);
    public static final RegistryObject<TileEntityType<puddleFluidTileEntity>> puddleFluidTileEntity = TILE_ENTITIES.register("puddle_fluid_tileentity", () -> TileEntityType.Builder.create(puddleFluidTileEntity::new, blockRegistry.puddleFluid.get()).build(null));
    public static final RegistryObject<TileEntityType<LavaFluidTileEntity>> LavaFluidTileEntity = TILE_ENTITIES.register("lava_fluid_tileentity", () -> TileEntityType.Builder.create(LavaFluidTileEntity::new, Blocks.LAVA).build(null));
}
