package com.ddhuan.ifscience.Custom;

import com.ddhuan.ifscience.common.Block.blockRegistry;
import com.ddhuan.ifscience.network.Client.entityMotionPack;
import com.ddhuan.ifscience.network.Client.playerPosePack;
import com.ddhuan.ifscience.network.Network;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.entity.Pose;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.Heightmap;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.fml.network.PacketDistributor;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Random;

public class rainingUtil {
    private rainingUtil() {
    }

    private final static Random random = new Random();
    private final static BlockPos.Mutable blockpos$mutable = new BlockPos.Mutable();
    private final static HashSet<ChunkPos> chunkPosSet = new HashSet<>();

    //下雨产生地面积水
    public static void placePuddle(World world) {
        if (world.isRaining() && world.getGameTime() % 40 == 0) {
            for (PlayerEntity player : world.getPlayers()) {
                Biome biome = world.getBiome(player.getPosition());
                if (biome.getPrecipitation() == Biome.RainType.RAIN || biome.getPrecipitation() == Biome.RainType.SNOW) {
                    int playerChunkCoordX = player.chunkCoordX, playerChunkCoordZ = player.chunkCoordZ;
                    for (int i = -5; i <= 5; i++) {
                        for (int j = -5; j <= 5; j++) {
                            chunkPosSet.add(new ChunkPos(playerChunkCoordX + i, playerChunkCoordZ + j));
                        }
                    }
                }
            }
            for (ChunkPos chunkPos : chunkPosSet) {
                int x = (chunkPos.x << 4) + random.nextInt(16), z = (chunkPos.z << 4) + random.nextInt(16);
                int y = world.getHeight(Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, x, z);
                Block block = world.getBlockState(blockpos$mutable.setPos(x, y - 1, z)).getBlock();
                if (block != blockRegistry.puddleFluid.get() && block != Blocks.WATER && block != Blocks.LAVA) {
                    blockpos$mutable.setY(y);
                    world.setBlockState(blockpos$mutable, blockRegistry.puddleFluid.get().getStateContainer().getValidStates().get(7));
                }
            }
            chunkPosSet.clear();
        }
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

    public static void extinguishLava(World world, BlockPos pos, Biome.RainType rainType) {
        if (world.isRaining() && (rainType == Biome.RainType.RAIN || rainType == Biome.RainType.SNOW)) {
            if (world.getHeight(Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, pos).add(0, -1, 0).equals(pos)) {
                world.addParticle(ParticleTypes.POOF, pos.getX() + 0.5, pos.getY() + 1.2, pos.getZ() + 0.5, 0, 0, 0);
                world.playSound(pos.getX(), pos.getY(), pos.getZ(), SoundEvents.BLOCK_FIRE_EXTINGUISH, SoundCategory.BLOCKS, 1f, 1f, true);
                world.setBlockState(pos, Blocks.OBSIDIAN.getDefaultState());
            }
        }
    }
}
