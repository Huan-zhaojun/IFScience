package com.ddhuan.ifscience.Custom;

import com.ddhuan.ifscience.Config;
import com.ddhuan.ifscience.common.Entity.CutBlockEntity;
import com.ddhuan.ifscience.common.Entity.render.CutBlockEntityRender;
import com.ddhuan.ifscience.common.Item.AngleGrinder;
import com.ddhuan.ifscience.common.customDamage;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.item.TNTEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;

import static com.ddhuan.ifscience.Config.REBOUND1;
import static com.ddhuan.ifscience.Config.REBOUND2;


public final class CutBlockUtil {
    //左键点击切割方块
    public static void onPlayerBreakBlock(PlayerInteractEvent.LeftClickBlock event) {
        PlayerEntity player = event.getPlayer();
        World world = player.world;
        if (!world.isRemote && player.getHeldItemMainhand().getItem() instanceof AngleGrinder) {
            ItemStack itemStack = player.getHeldItemMainhand();
            AngleGrinder angleGrinder = (AngleGrinder) itemStack.getItem();
            player.setHeldItem(Hand.MAIN_HAND, ItemStack.EMPTY);
            event.setCanceled(true);
            if (itemStack.getDamage() >= itemStack.getMaxDamage()) return;//耐久耗尽
            BlockPos pos = event.getPos();
            BlockState blockState = world.getBlockState(pos);
            if (!Blocks.AIR.equals(blockState.getBlock())) {
                boolean cutEverything = Config.CUT_EVERYTHING.get();//切割任何方块模式
                if (!cutEverything) {
                    if (blockState.getBlock().equals(Blocks.TNT)) {//切割TNT导致爆炸
                        ItemEntity itementity = new ItemEntity(world, pos.getX(), pos.getY(), pos.getZ(), itemStack);
                        itementity.setDefaultPickupDelay();
                        world.addEntity(itementity);
                        world.setBlockState(pos, Blocks.AIR.getDefaultState());
                        TNTEntity tntentity = new TNTEntity(world, (double) pos.getX() + 0.5D, (double) pos.getY(), (double) pos.getZ() + 0.5D, null);
                        tntentity.setFuse(10);
                        world.addEntity(tntentity);
                        world.playSound(null, tntentity.getPosX(), tntentity.getPosY(), tntentity.getPosZ(), SoundEvents.ENTITY_TNT_PRIMED, SoundCategory.BLOCKS, 1.0F, 1.0F);
                        return;
                    }
                    float blockHardness = blockState.getBlockHardness(world, pos);//判定角磨机硬度和方块硬度
                    if (angleGrinder.hardness == blockHardness) {//硬度相等
                        if (Math.random() < (double) REBOUND2.get() / 1000) {
                            itemStack.setDamage(itemStack.getDamage() + (itemStack.getMaxDamage() / 3));
                            ItemEntity itementity = new ItemEntity(world, pos.getX(), pos.getY(), pos.getZ(), itemStack);
                            itementity.setDefaultPickupDelay();
                            world.addEntity(itementity);
                            player.attackEntityFrom(customDamage.AngleGrinder1, 8);
                            return;
                        }
                    } else if (angleGrinder.hardness < blockHardness || blockHardness < 0) {
                        itemStack.setDamage(itemStack.getMaxDamage());//完全损坏
                        ItemEntity itementity = new ItemEntity(world, pos.getX(), pos.getY(), pos.getZ(), itemStack);
                        itementity.setDefaultPickupDelay();
                        world.addEntity(itementity);
                        player.attackEntityFrom(customDamage.AngleGrinder1, (float) (player.getMaxHealth() * (1 + Math.random())));
                        return;
                    } else if (angleGrinder.hardness > blockHardness) {
                        if (Math.random() < (double) REBOUND1.get() / 1000) {
                            itemStack.setDamage(itemStack.getDamage() + (itemStack.getMaxDamage() / 5));
                            ItemEntity itementity = new ItemEntity(world, pos.getX(), pos.getY(), pos.getZ(), itemStack);
                            itementity.setDefaultPickupDelay();
                            world.addEntity(itementity);
                            player.attackEntityFrom(customDamage.AngleGrinder1, 8);
                            return;
                        }
                    }
                }
                CompoundNBT tileEntityNbt = new CompoundNBT();
                TileEntity tileEntity = world.getTileEntity(pos);
                if (tileEntity != null) tileEntityNbt = tileEntity.write(new CompoundNBT());
                world.setBlockState(pos, Blocks.AIR.getDefaultState());
                Direction direction = player.getHorizontalFacing();//获取玩家切割的朝向
                itemStack.setDamage(itemStack.getDamage() + 10);
                world.addEntity(new CutBlockEntity(world, pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5, blockState, tileEntityNbt, direction,
                        player.getUniqueID(), itemStack));
            }
        }
    }

    public static void updateConfig_StaticValue() {
        CutBlockEntityRender.levelCutTick = Config.LEVEL_CUT_TICK.get() * 50;
        CutBlockEntityRender.verticalCutTick = Config.VERTICAL_CUT_TICK.get() * 50;
        CutBlockEntityRender.cutBlockTick = Config.CUT_BLOCK_TICK.get() * 50;
        CutBlockEntity.maxLifeTick = Config.CUTBLOCK_MAX_LIFE_TICK.get();
        CutBlockEntityRender.maxAnimationTick = CutBlockEntityRender.levelCutTick + CutBlockEntityRender.verticalCutTick + CutBlockEntityRender.cutBlockTick;
    }

    private CutBlockUtil() {
    }
}
