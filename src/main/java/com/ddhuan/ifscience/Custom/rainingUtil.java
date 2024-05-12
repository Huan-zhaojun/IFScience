package com.ddhuan.ifscience.Custom;

import com.ddhuan.ifscience.common.Block.blockRegistry;
import com.ddhuan.ifscience.network.Client.entityMotionPack;
import com.ddhuan.ifscience.network.Client.playerPosePack;
import com.ddhuan.ifscience.network.Network;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.entity.Pose;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.Heightmap;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.fml.network.PacketDistributor;

import java.util.HashMap;
import java.util.Random;

public class rainingUtil {
    private rainingUtil() {
    }

    private final static Random random = new Random();
    private final static BlockPos.Mutable blockpos$mutable = new BlockPos.Mutable();
    private static int placePuddleTick = 0;

    //下雨产生积水
    public static void placePuddle(World world, Biome biome, BlockPos posPlayer, ServerWorld world1) {
        if (world.isRaining() && placePuddleTick >= 30 && biome.getPrecipitation() == Biome.RainType.RAIN && biome.getTemperature(blockpos$mutable) >= 0.15F) {
            int playX = posPlayer.getX(), playZ = posPlayer.getZ();
            for (int i = -5; i <= 5; i++) {
                for (int j = -5; j <= 5; j++) {
                    int x = random.nextInt(16) + playX + i * 16, z = random.nextInt(16) + playZ + j * 16;
                    int y = world1.getHeight(Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, x, z);
                    Block block = world.getBlockState(blockpos$mutable.setPos(x, y - 1, z)).getBlock();
                    if (block != blockRegistry.puddleFluid.get() && block != Blocks.WATER && block != Blocks.LAVA) {
                        blockpos$mutable.setY(y);
                        world.setBlockState(blockpos$mutable, blockRegistry.puddleFluid.get().getStateContainer().getValidStates().get(7));
                    }
                }
            }
            placePuddleTick = 0;
        }
        placePuddleTick++;
    }

    private final static HashMap<ServerPlayerEntity, Integer> tumblePlayers = new HashMap<>();

    //玩家踩到下雨的积水被滑倒~
    public static void tumble(ServerPlayerEntity player1, ServerWorld world1, BlockPos posPlayer) {
        if (tumblePlayers.get(player1) == null && blockRegistry.puddleFluid.get().equals(world1.getBlockState(posPlayer).getBlock())) {
            player1.setForcedPose(Pose.SWIMMING);
            Network.INSTANCE.send(PacketDistributor.PLAYER.with(() -> player1), new playerPosePack(player1.getUniqueID(), Pose.SWIMMING));
            double yaw = Math.toRadians(player1.rotationYaw);
            double speed = 1.0;
            Vector3d vector3d = new Vector3d(speed * -Math.sin(yaw), 0.5, speed * Math.cos(yaw));
            player1.setMotion(vector3d);
            Network.INSTANCE.send(PacketDistributor.PLAYER.with(() -> player1), new entityMotionPack(player1.getUniqueID(), vector3d));
            tumblePlayers.put(player1, 40);
        } else if (!tumblePlayers.isEmpty() && tumblePlayers.get(player1) != null && tumblePlayers.get(player1) <= 0) {
            player1.setForcedPose(null);
            Network.INSTANCE.send(PacketDistributor.PLAYER.with(() -> player1), new playerPosePack(player1.getUniqueID(), null));
            tumblePlayers.remove(player1);
        }
        if (tumblePlayers.get(player1) != null) tumblePlayers.put(player1, tumblePlayers.get(player1) - 1);
    }
}
