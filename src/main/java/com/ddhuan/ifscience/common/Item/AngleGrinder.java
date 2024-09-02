package com.ddhuan.ifscience.common.Item;

import com.ddhuan.ifscience.common.Entity.CutBlockEntity;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;

import javax.annotation.Nullable;
import java.util.List;

public class AngleGrinder extends Item {
    public AngleGrinder(Properties properties) {
        super(properties.maxStackSize(1));
    }

    @Override
    public boolean canPlayerBreakBlockWhileHolding(BlockState state, World worldIn, BlockPos pos, PlayerEntity player) {
        return false;
    }

    //左键点击切割方块
    public static void onPlayerBreakBlock(PlayerInteractEvent.LeftClickBlock event) {
        PlayerEntity player = event.getPlayer();
        World world = player.world;
        if (!world.isRemote && itemRegistry.angleGrinder.get().equals(player.getHeldItemMainhand().getItem())) {
            event.setCanceled(true);
            BlockPos pos = event.getPos();
            BlockState blockState = world.getBlockState(pos);
            if (!Blocks.AIR.equals(blockState.getBlock())) {
                world.setBlockState(pos, Blocks.AIR.getDefaultState());
                Direction direction = player.getHorizontalFacing();//获取玩家切割的朝向
                world.addEntity(new CutBlockEntity(world, pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5, blockState, direction,player.getUniqueID()));
                player.setHeldItem(Hand.MAIN_HAND,ItemStack.EMPTY);
                //player.replaceItemInInventory()
            }
        }
    }

    /*@Override
    public ActionResultType onItemUse(ItemUseContext context) {
        World world = context.getWorld();
        if (!world.isRemote) {
            BlockPos pos = context.getPos();
            BlockState blockState = world.getBlockState(pos);
            world.setBlockState(pos, Blocks.AIR.getDefaultState());
            Direction direction = Direction.NORTH;
            if (context.getPlayer() != null) {
                direction = context.getPlayer().getHorizontalFacing();//获取玩家切割的朝向
            }
            world.addEntity(new CutBlockEntity(world, pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5, blockState,direction));
        }
        return super.onItemUse(context);
    }*/

    @Override
    public void addInformation(ItemStack stack, @Nullable World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn) {
        //tooltip.add(new TranslationTextComponent("").mergeStyle(TextFormatting.RED));
    }
}
