package com.ddhuan.ifscience.common.Item;

import com.ddhuan.ifscience.common.Block.blockRegistry;
import com.ddhuan.ifscience.common.Entity.furnaceTNTEntity;
import com.ddhuan.ifscience.common.Fluid.FluidRegistry;
import com.ddhuan.ifscience.ifscience;
import com.ddhuan.ifscience.network.Client.furnaceTNTRenderPack;
import com.ddhuan.ifscience.network.Network;
import net.minecraft.block.*;
import net.minecraft.block.material.Material;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.FlowingFluid;
import net.minecraft.fluid.Fluid;
import net.minecraft.item.*;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.fml.network.PacketDistributor;
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

    public static RegistryObject<Item> furnaceTNT = ITEMS.register("furnace_tnt",
            () -> new Item(new Item.Properties().group(itemRegistry.ifScience)) {
                @Override
                public ActionResultType onItemUse(ItemUseContext context) {
                    World world = context.getWorld();
                    Direction direction = Direction.NORTH;
                    if (context.getPlayer() != null) {
                        direction = context.getPlayer().getHorizontalFacing().getOpposite();//使得生成的熔炉始终朝向玩家
                    }
                    BlockPos pos = new BlockItemUseContext(context).getPos();//获取真正放置的方块的位置
                    if (!world.isRemote) {
                        furnaceTNTEntity furnace = new furnaceTNTEntity(world, (double) pos.getX() + 0.5, (double) pos.getY(), (double) pos.getZ() + 0.5, world.getBlockState(pos));
                        furnace.setFuse(20);
                        world.addEntity(furnace);
                        Network.INSTANCE.send(PacketDistributor.ALL.noArg(), new furnaceTNTRenderPack(Blocks.FURNACE.getDefaultState().with(AbstractFurnaceBlock.FACING, direction).with(AbstractFurnaceBlock.LIT, true)));
                        world.playSound(null, furnace.getPosX(), furnace.getPosY(), furnace.getPosZ(), SoundEvents.ENTITY_TNT_PRIMED, SoundCategory.BLOCKS, 1.0F, 1.0F);
                        world.setBlockState(pos, Blocks.AIR.getDefaultState(), 11);
                    }
                    return ActionResultType.CONSUME;
                }

                @Override
                public void addInformation(ItemStack stack, @Nullable World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn) {
                    tooltip.add(new TranslationTextComponent("text.ifscience.furnace_tnt1").mergeStyle(TextFormatting.RED));
                    tooltip.add(new TranslationTextComponent("text.ifscience.furnace_tnt2").mergeStyle(TextFormatting.DARK_RED));
                }
            });
    public static RegistryObject<BucketItem> puddleFluidBucket = ITEMS.register("puddle_fluid_bucket",
            () -> new BucketItem(FluidRegistry.puddleFluidFlowing, new Item.Properties()
                    .group(itemRegistry.ifScience).containerItem(BUCKET).maxStackSize(1)) {
                @Override
                public void addInformation(ItemStack stack, @Nullable World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn) {
                    tooltip.add(new TranslationTextComponent("text.ifscience.puddle_fluid_bucket1").mergeStyle(TextFormatting.AQUA));
                    tooltip.add(new TranslationTextComponent("text.ifscience.puddle_fluid_bucket2").mergeStyle(TextFormatting.BLUE));
                    tooltip.add(new TranslationTextComponent("text.ifscience.puddle_fluid_bucket3").mergeStyle(TextFormatting.YELLOW));
                }

                @Override//积水桶放置不是水源的积水
                public boolean tryPlaceContainedLiquid(@Nullable PlayerEntity player, World worldIn, BlockPos posIn, @Nullable BlockRayTraceResult rayTrace) {
                    Fluid containedBlock = FluidRegistry.puddleFluid.get();
                    if (!(containedBlock instanceof FlowingFluid)) {
                        return false;
                    } else {
                        BlockState blockstate = worldIn.getBlockState(posIn);
                        Block block = blockstate.getBlock();
                        Material material = blockstate.getMaterial();
                        boolean flag = blockstate.isReplaceable(containedBlock);
                        boolean flag1 = blockstate.isAir() || flag || block instanceof ILiquidContainer && ((ILiquidContainer) block).canContainFluid(worldIn, posIn, blockstate, containedBlock);
                        if (!flag1) {
                            return rayTrace != null && this.tryPlaceContainedLiquid(player, worldIn, rayTrace.getPos().offset(rayTrace.getFace()), (BlockRayTraceResult) null);
                        } else if (worldIn.getDimensionType().isUltrawarm() && containedBlock.isIn(FluidTags.WATER)) {
                            int i = posIn.getX();
                            int j = posIn.getY();
                            int k = posIn.getZ();
                            worldIn.playSound(player, posIn, SoundEvents.BLOCK_FIRE_EXTINGUISH, SoundCategory.BLOCKS, 0.5F, 2.6F + (worldIn.rand.nextFloat() - worldIn.rand.nextFloat()) * 0.8F);

                            for (int l = 0; l < 8; ++l) {
                                worldIn.addParticle(ParticleTypes.LARGE_SMOKE, (double) i + Math.random(), (double) j + Math.random(), (double) k + Math.random(), 0.0D, 0.0D, 0.0D);
                            }

                            return true;
                        } else if (block instanceof ILiquidContainer && ((ILiquidContainer) block).canContainFluid(worldIn, posIn, blockstate, containedBlock)) {
                            ((ILiquidContainer) block).receiveFluid(worldIn, posIn, blockstate, ((FlowingFluid) containedBlock).getStillFluidState(false));
                            this.playEmptySound(player, worldIn, posIn);
                            return true;
                        } else {
                            if (!worldIn.isRemote && flag && !material.isLiquid()) {
                                worldIn.destroyBlock(posIn, true);
                            }

                            if (!worldIn.setBlockState(posIn, blockRegistry.puddleFluid.get().getStateContainer().getValidStates().get(7), 11) && !blockstate.getFluidState().isSource()) {
                                return false;
                            } else {
                                this.playEmptySound(player, worldIn, posIn);
                                return true;
                            }
                        }
                    }
                }
            });

    public static RegistryObject<HorseshoeMagnetItem> horseshoeMagnet = ITEMS.register("horseshoe_magnet",
            () -> new HorseshoeMagnetItem(new Item.Properties().group(itemRegistry.ifScience)));//磁铁

    public static RegistryObject<BlockItem> iceRail = ITEMS.register("ice_rail",/*寒冰铁轨*/
            () -> new BlockItem(blockRegistry.iceRail.get(), new Item.Properties().group(itemRegistry.ifScience)) {
                @Override
                public void addInformation(ItemStack stack, @Nullable World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn) {
                    super.addInformation(stack, worldIn, tooltip, flagIn);
                    tooltip.add(new TranslationTextComponent("text.ifscience.iceRail1").mergeStyle(TextFormatting.AQUA));
                    tooltip.add(new TranslationTextComponent("text.ifscience.iceRail2").mergeStyle(TextFormatting.DARK_AQUA));
                }
            });

    public static RegistryObject<AngleGrinder> ironAngleGrinder = ITEMS.register("iron_angle_grinder",
            () -> new AngleGrinder(new Item.Properties().group(itemRegistry.ifScience).maxDamage(250), 5.0F, 9F) {
                @Override
                public void addInformation(ItemStack stack, @Nullable World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn) {
                    tooltip.add(new TranslationTextComponent("text.ifscience.iron_angle_grinder1").mergeStyle(TextFormatting.YELLOW));
                    tooltip.add(new TranslationTextComponent("text.ifscience.iron_angle_grinder2").mergeStyle(TextFormatting.DARK_RED));
                }
            });//铁质角磨机

    public static RegistryObject<AngleGrinder> obsidianNetheriteAngleGrinder = ITEMS.register("obsidian_netherite_angle_grinder",
            () -> new AngleGrinder(new Item.Properties().group(itemRegistry.ifScience).maxDamage(2500).isImmuneToFire(), 100.0F, 19F) {
                @Override
                public void addInformation(ItemStack stack, @Nullable World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn) {
                    tooltip.add(new TranslationTextComponent("text.ifscience.obsidian_netherite_angle_grinder1").mergeStyle(TextFormatting.GOLD));
                    tooltip.add(new TranslationTextComponent("text.ifscience.obsidian_netherite_angle_grinder2").mergeStyle(TextFormatting.YELLOW));
                    tooltip.add(new TranslationTextComponent("text.ifscience.iron_angle_grinder2").mergeStyle(TextFormatting.DARK_RED));
                }
            });//黑曜合金角磨机

    public static RegistryObject<WallOrFloorItem> extinguishedTorch = ITEMS.register("extinguished_torch",/*熄灭的火把*/
            () -> new WallOrFloorItem(blockRegistry.extinguishedTorch.get(), blockRegistry.wallExtinguishedTorch.get(), (new Item.Properties()).group(ifScience)));

    public static RegistryObject<WallOrFloorItem> safetyTorch = ITEMS.register("safety_torch",/*安全火把*/
            () -> new WallOrFloorItem(blockRegistry.safetyTorch.get(), blockRegistry.wallSafetyTorch.get(), (new Item.Properties()).group(ifScience)));

    public static RegistryObject<BlockItem> reinforcedGlass = ITEMS.register("reinforced_glass",
            () -> new BlockItem(blockRegistry.reinforcedGlass.get(), new Item.Properties().group(itemRegistry.ifScience)));

    //自定义附魔书
    public static RegistryObject<ModEnchantedBookItem> modEnchantedBook = ITEMS.register("enchanted_book",
            () -> new ModEnchantedBookItem(new Item.Properties().group(itemRegistry.ifScience).maxStackSize(1).rarity(Rarity.UNCOMMON)));
}
