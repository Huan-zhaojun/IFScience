package com.ddhuan.ifscience.common.Block;

import com.ddhuan.ifscience.common.Fluid.FluidRegistry;
import com.ddhuan.ifscience.common.Fluid.LavaFluidTileEntity;
import com.ddhuan.ifscience.common.Fluid.puddleFluidTileEntity;
import com.ddhuan.ifscience.ifscience;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.FlowingFluidBlock;
import net.minecraft.block.material.Material;
import net.minecraft.fluid.Fluids;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.IBlockReader;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

import javax.annotation.Nullable;

public class blockRegistry {
    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, ifscience.MOD_ID);
    public static final DeferredRegister<Block> BLOCKS_vanilla = DeferredRegister.create(ForgeRegistries.BLOCKS, "minecraft");

    public static RegistryObject<FlowingFluidBlock> puddleFluid = BLOCKS.register("puddle_fluid",
            () -> new FlowingFluidBlock(FluidRegistry.puddleFluid, Block.Properties.create(Material.WATER)
                    .doesNotBlockMovement().hardnessAndResistance(100.0F).noDrops()) {
                @Override
                public boolean hasTileEntity(BlockState state) {
                    return true;
                }

                @Nullable
                @Override
                public TileEntity createTileEntity(BlockState state, IBlockReader world) {
                    return new puddleFluidTileEntity();
                }
            });

    public static final RegistryObject<FlowingFluidBlock> lava = BLOCKS_vanilla.register("lava", () -> new FlowingFluidBlock(Fluids.LAVA, AbstractBlock.Properties.create(Material.LAVA).doesNotBlockMovement().tickRandomly().hardnessAndResistance(100.0F).setLightLevel((state) -> 15).noDrops()) {
        @Override
        public boolean hasTileEntity(BlockState state) {
            return true;
        }

        @Nullable
        @Override
        public TileEntity createTileEntity(BlockState state, IBlockReader world) {
            return new LavaFluidTileEntity();
        }
    });
}
