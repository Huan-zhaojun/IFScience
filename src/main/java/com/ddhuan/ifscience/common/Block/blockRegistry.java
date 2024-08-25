package com.ddhuan.ifscience.common.Block;

import com.ddhuan.ifscience.Custom.rainingUtil;
import com.ddhuan.ifscience.common.Fluid.FluidRegistry;
import com.ddhuan.ifscience.common.Fluid.LavaFluidTileEntity;
import com.ddhuan.ifscience.common.Fluid.puddleFluidTileEntity;
import com.ddhuan.ifscience.ifscience;
import net.minecraft.block.*;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.RenderTypeLookup;
import net.minecraft.fluid.Fluids;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

import javax.annotation.Nullable;

public class blockRegistry {
    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, ifscience.MOD_ID);
    public static final DeferredRegister<Block> BLOCKS_VANILLA = DeferredRegister.create(ForgeRegistries.BLOCKS, "minecraft");

    public static final RegistryObject<FlowingFluidBlock> puddleFluid = BLOCKS.register("puddle_fluid",
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

    public static final RegistryObject<FlowingFluidBlock> LAVA = BLOCKS_VANILLA.register("lava", () -> new FlowingFluidBlock(Fluids.LAVA, AbstractBlock.Properties.create(Material.LAVA).doesNotBlockMovement().tickRandomly().hardnessAndResistance(100.0F).setLightLevel((state) -> 15).noDrops()) {
        @Override
        public void neighborChanged(BlockState state, World worldIn, BlockPos pos, Block blockIn, BlockPos fromPos, boolean isMoving) {
            if (!worldIn.isRemote()) {
                Biome.RainType rainType = worldIn.getBiome(pos).getPrecipitation();
                rainingUtil.extinguishLava(worldIn, pos, rainType);//岩浆受到雨水被凝固
            }
            super.neighborChanged(state, worldIn, pos, blockIn, fromPos, isMoving);
        }

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
    /*public static final RegistryObject<BreakableBlock> BLUE_ICE = BLOCKS_VANILLA.register("blue_ice",()-> new BreakableBlock(AbstractBlock.Properties.create(Material.PACKED_ICE).hardnessAndResistance(2.8F).slipperiness(0.989F).sound(SoundType.GLASS)){
        @Override
        public void onBlockAdded(BlockState state, World worldIn, BlockPos pos, BlockState oldState, boolean isMoving) {
            //worldIn.notifyNeighborsOfStateChange(pos,this);
        }

        @Override
        public void onReplaced(BlockState state, World worldIn, BlockPos pos, BlockState newState, boolean isMoving) {
            super.onReplaced(state, worldIn, pos, newState, isMoving);
            //worldIn.notifyNeighborsOfStateChange(pos,this);
        }
    });*/

    public static final RegistryObject<IceRailBlock> iceRail = BLOCKS.register("ice_rail", () -> new IceRailBlock(AbstractBlock.Properties.create(Material.IRON).doesNotBlockMovement().hardnessAndResistance(0.7F).sound(SoundType.METAL)));

    public static void setBlockRenderType() {
        RenderTypeLookup.setRenderLayer(iceRail.get(), RenderType.getCutout());
    }
}
