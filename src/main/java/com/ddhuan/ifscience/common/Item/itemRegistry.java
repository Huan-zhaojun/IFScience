package com.ddhuan.ifscience.common.Item;

import com.ddhuan.ifscience.common.Block.blockRegistry;
import com.ddhuan.ifscience.common.Entity.furnaceTNTEntity;
import com.ddhuan.ifscience.common.Fluid.FluidRegistry;
import com.ddhuan.ifscience.ifscience;
import com.ddhuan.ifscience.network.Client.furnaceTNTRenderPack;
import com.ddhuan.ifscience.network.Network;
import net.minecraft.block.AbstractFurnaceBlock;
import net.minecraft.block.Blocks;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.*;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
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
    public static RegistryObject<Item> puddleFluidBucket = ITEMS.register("puddle_fluid_bucket",
            () -> new BucketItem(FluidRegistry.puddleFluid, new Item.Properties()
                    .group(itemRegistry.ifScience).containerItem(BUCKET).maxStackSize(1)) {
                @Override
                public void addInformation(ItemStack stack, @Nullable World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn) {
                    tooltip.add(new TranslationTextComponent("text.ifscience.puddle_fluid_bucket1").mergeStyle(TextFormatting.AQUA));
                    tooltip.add(new TranslationTextComponent("text.ifscience.puddle_fluid_bucket2").mergeStyle(TextFormatting.BLUE));
                    tooltip.add(new TranslationTextComponent("text.ifscience.puddle_fluid_bucket3").mergeStyle(TextFormatting.YELLOW));
                }
            });

    public static RegistryObject<Item> horseshoeMagnet = ITEMS.register("horseshoe_magnet",
            () -> new HorseshoeMagnetItem(new Item.Properties().group(itemRegistry.ifScience)));//磁铁

    public static RegistryObject<Item> iceRail = ITEMS.register("ice_rail",
            () -> new BlockItem(blockRegistry.iceRail.get(), new Item.Properties().group(itemRegistry.ifScience)) {
                @Override
                public void addInformation(ItemStack stack, @Nullable World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn) {
                    super.addInformation(stack, worldIn, tooltip, flagIn);
                    tooltip.add(new TranslationTextComponent("text.ifscience.iceRail1").mergeStyle(TextFormatting.AQUA));
                    tooltip.add(new TranslationTextComponent("text.ifscience.iceRail2").mergeStyle(TextFormatting.DARK_AQUA));
                }
            });
}
