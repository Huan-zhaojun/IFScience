package com.ddhuan.ifscience.event;

import com.ddhuan.ifscience.Custom.CutBlockUtil;
import com.ddhuan.ifscience.Custom.magnetUtil;
import com.ddhuan.ifscience.Custom.rainingUtil;
import com.ddhuan.ifscience.common.customDamage;
import com.ddhuan.ifscience.network.Client.fireRenderPack;
import com.ddhuan.ifscience.network.Network;
import net.minecraft.block.AbstractFurnaceBlock;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.tileentity.AbstractFurnaceTileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.network.PacketDistributor;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import static com.ddhuan.ifscience.Config.RAIN;

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
    public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
        PlayerEntity player = event.player;
        World world = player.world;
        if (!world.isRemote) {
            ServerPlayerEntity player1 = (ServerPlayerEntity) player;
            ServerWorld world1 = (ServerWorld) world;
            BlockPos posPlayer = player1.getPosition();
            if (RAIN.get())
                rainingUtil.tumble(player1, world1, posPlayer);//玩家踩到下雨的积水被滑倒~
        }
    }

    @SubscribeEvent
    public static void onPlayerBreakSpeed(PlayerEvent.BreakSpeed event) {
        burnHand2(event);//挖方块烫手
    }

    @SubscribeEvent
    public static void onPlayerLeftClickBlock(PlayerInteractEvent.LeftClickBlock event) {
        burnHand1(event);//左键点方块烫手
        CutBlockUtil.onPlayerBreakBlock(event);//角磨机切割方块
    }

    @SubscribeEvent
    public static void onPlayerRightClickBlock(PlayerInteractEvent.RightClickBlock event) {
        burnHand1(event);//右键键点方块烫手
    }

    @SubscribeEvent//与实体交互事件
    public static void onPlayerInteractEntity(PlayerInteractEvent.EntityInteract event) throws NoSuchFieldException, IllegalAccessException {
        World world = event.getWorld();
        Entity entity = event.getTarget();
        PlayerEntity player = event.getPlayer();

        if (!world.isRemote) {
            if (magnetUtil.canFeedIron_LivingEntity) //给生物喂铁锭上磁性
                magnetUtil.LivingEntity_setMagnetAttract(event, world, entity, player);
        }
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
