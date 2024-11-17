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
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.Fluids;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

import javax.annotation.Nullable;

import static com.ddhuan.ifscience.Config.RAIN;
import static com.ddhuan.ifscience.Config.SOLIDIFY_LAVA;

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

                @Override//非水源流体也能被空桶收回
                public Fluid pickupFluid(IWorld worldIn, BlockPos pos, BlockState state) {
                    worldIn.setBlockState(pos, Blocks.AIR.getDefaultState(), 11);
                    return FluidRegistry.puddleFluidFlowing.get();
                }
            });

    public static final RegistryObject<FlowingFluidBlock> LAVA = BLOCKS_VANILLA.register("lava", () -> new FlowingFluidBlock(Fluids.LAVA, AbstractBlock.Properties.create(Material.LAVA).doesNotBlockMovement().tickRandomly().hardnessAndResistance(100.0F).setLightLevel((state) -> 15).noDrops()) {
        @Override
        public void neighborChanged(BlockState state, World worldIn, BlockPos pos, Block blockIn, BlockPos fromPos, boolean isMoving) {
            if (!worldIn.isRemote() && RAIN.get() && SOLIDIFY_LAVA.get()) {
                Biome.RainType rainType = worldIn.getBiome(pos).getPrecipitation();
                rainingUtil.solidifyLava(worldIn, pos, rainType);//岩浆受到雨水被凝固
            }
            super.neighborChanged(state, worldIn, pos, blockIn, fromPos, isMoving);
        }

        @Override
        public boolean hasTileEntity(BlockState state) {
            return RAIN.get() && SOLIDIFY_LAVA.get();
        }

        @Nullable
        @Override
        public TileEntity createTileEntity(BlockState state, IBlockReader world) {
            return new LavaFluidTileEntity();
        }
    });

    public static final RegistryObject<IceRailBlock> iceRail = BLOCKS.register("ice_rail", () -> new IceRailBlock(AbstractBlock.Properties.create(Material.IRON).doesNotBlockMovement().hardnessAndResistance(0.7F).sound(SoundType.METAL)));

    //熄灭的火把
    public static final RegistryObject<ExtinguishedTorch> extinguishedTorch = BLOCKS.register("extinguished_torch", () -> new ExtinguishedTorch(AbstractBlock.Properties.create(Material.MISCELLANEOUS).doesNotBlockMovement().zeroHardnessAndResistance().sound(SoundType.WOOD)));
    public static final RegistryObject<WallExtinguishedTorch> wallExtinguishedTorch = BLOCKS.register("wall_extinguished_torch", () -> new WallExtinguishedTorch(AbstractBlock.Properties.create(Material.MISCELLANEOUS).doesNotBlockMovement().zeroHardnessAndResistance().setLightLevel((state) -> 14).sound(SoundType.WOOD).lootFrom(extinguishedTorch)));
    //安全火把
    public static final RegistryObject<SafetyTorch> safetyTorch = BLOCKS.register("safety_torch", () -> new SafetyTorch(AbstractBlock.Properties.create(Material.ROCK).zeroHardnessAndResistance().setLightLevel((state) -> 14).sound(SoundType.WOOD)));
    public static final RegistryObject<WallSafetyTorch> wallSafetyTorch = BLOCKS.register("wall_safety_torch", () -> new WallSafetyTorch(AbstractBlock.Properties.create(Material.ROCK).zeroHardnessAndResistance().setLightLevel((state) -> 14).sound(SoundType.WOOD).lootFrom(safetyTorch)));

    public static final RegistryObject<GlassBlock> reinforcedGlass = BLOCKS.register("reinforced_glass", () -> new GlassBlock(AbstractBlock.Properties.create(Material.GLASS).hardnessAndResistance(10.0F).sound(SoundType.GLASS).notSolid().setAllowsSpawn((p1, p2, p3, p4) -> false).setOpaque((p1, p2, p3) -> false).setSuffocates((p1, p2, p3) -> false).setBlocksVision((p1, p2, p3) -> false)));

    public static final RegistryObject<HydrantBlock> hydrantBlock = BLOCKS.register("hydrant_block", HydrantBlock::new);//消防栓

    public static final RegistryObject<Block> iceChestBlock = BLOCKS.register("ice_chest", () -> new ChestBlock(AbstractBlock.Properties.create(Material.IRON).hardnessAndResistance(10F).sound(SoundType.STONE), () -> TileEntityType.CHEST));

    public static void setBlockRenderType() {
        RenderTypeLookup.setRenderLayer(iceRail.get(), RenderType.getCutout());
        RenderTypeLookup.setRenderLayer(extinguishedTorch.get(), RenderType.getCutout());
        RenderTypeLookup.setRenderLayer(wallExtinguishedTorch.get(), RenderType.getCutout());
        RenderTypeLookup.setRenderLayer(safetyTorch.get(), RenderType.getCutout());
        RenderTypeLookup.setRenderLayer(wallSafetyTorch.get(), RenderType.getCutout());
        RenderTypeLookup.setRenderLayer(reinforcedGlass.get(), RenderType.getCutout());
    }
}
