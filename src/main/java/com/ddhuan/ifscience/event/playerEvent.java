package com.ddhuan.ifscience.event;

import com.ddhuan.ifscience.common.customDamage;
import com.ddhuan.ifscience.network.Client.fireRenderPack;
import com.ddhuan.ifscience.network.Network;
import net.minecraft.block.AbstractFurnaceBlock;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.tileentity.AbstractFurnaceTileEntity;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.network.PacketDistributor;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

@Mod.EventBusSubscriber()
public class playerEvent {
    @SubscribeEvent
    public static void onPlayerCloned(PlayerEvent.Clone event) {
        PlayerEntity player = event.getPlayer();
        World world = player.world;
        if (!world.isRemote) {
            if (event.isWasDeath()) {//当玩家死亡

            }
        }
    }

    @SubscribeEvent
    public static void onPlayerBreakSpeed(PlayerEvent.BreakSpeed event) {
        burnHand2(event);//挖方块烫手
    }

    @SubscribeEvent
    public static void onPlayerLeftClickBlock(PlayerInteractEvent.LeftClickBlock event) {
        burnHand1(event);//左键点方块烫手
    }

    @SubscribeEvent
    public static void onPlayerRightClickBlock(PlayerInteractEvent.RightClickBlock event) {
        burnHand1(event);//右键键点方块烫手
    }


    private static void burnHand1(PlayerInteractEvent event) {
        World world = event.getWorld();
        if (!world.isRemote && world.getBlockState(event.getPos()).getBlock() instanceof AbstractFurnaceBlock) {
            AbstractFurnaceTileEntity tileEntity = (AbstractFurnaceTileEntity) world.getTileEntity(event.getPos());
            try {
                Method method = AbstractFurnaceTileEntity.class.getDeclaredMethod("isBurning_Mixin");
                boolean isBurning = (boolean) method.invoke(tileEntity);
                ServerPlayerEntity player = (ServerPlayerEntity) event.getPlayer();
                if (!player.isCreative() && isBurning) {
                    player.attackEntityFrom(customDamage.FurnaceBurn, player.getMaxHealth() / 5.0f);
                    Network.INSTANCE.send(PacketDistributor.PLAYER.with(() -> player), new fireRenderPack(player.getUniqueID()));
                }
            } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private static void burnHand2(PlayerEvent.BreakSpeed event) {
        World world = event.getPlayer().world;
        if (!world.isRemote && world.getBlockState(event.getPos()).getBlock() instanceof AbstractFurnaceBlock) {
            AbstractFurnaceTileEntity tileEntity = (AbstractFurnaceTileEntity) world.getTileEntity(event.getPos());
            try {
                Method method = AbstractFurnaceTileEntity.class.getDeclaredMethod("isBurning_Mixin");
                boolean isBurning = (boolean) method.invoke(tileEntity);
                ServerPlayerEntity player = (ServerPlayerEntity) event.getPlayer();
                if (!player.isCreative() && isBurning) {
                    player.attackEntityFrom(customDamage.FurnaceBurn, player.getMaxHealth() / 5.0f);
                    Network.INSTANCE.send(PacketDistributor.PLAYER.with(() -> player), new fireRenderPack(player.getUniqueID()));
                }
            } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
