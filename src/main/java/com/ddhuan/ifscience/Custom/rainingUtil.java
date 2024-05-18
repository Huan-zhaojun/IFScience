package com.ddhuan.ifscience.Custom;

import com.ddhuan.ifscience.common.Block.blockRegistry;
import com.ddhuan.ifscience.common.Entity.blockEntity;
import com.ddhuan.ifscience.ifscience;
import com.ddhuan.ifscience.network.Client.blockEntityRenderPack;
import com.ddhuan.ifscience.network.Client.entityMotionPack;
import com.ddhuan.ifscience.network.Client.playerPosePack;
import com.ddhuan.ifscience.network.Network;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.Pose;
import net.minecraft.entity.effect.LightningBoltEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.Items;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.util.BitArray;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.gen.Heightmap;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.fml.network.PacketDistributor;

import java.lang.reflect.InvocationTargetException;
import java.util.*;

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
            Vector3d vector3d = player1.getMotion().add(speed * -Math.sin(yaw), 0.5, speed * Math.cos(yaw));
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

    //岩浆受到雨水被凝固
    public static void extinguishLava(World world, BlockPos pos, Biome.RainType rainType) {
        if (world.isRaining() && (rainType == Biome.RainType.RAIN || rainType == Biome.RainType.SNOW)) {
            if (world.getHeight(Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, pos).add(0, -1, 0).equals(pos)) {
                world.addParticle(ParticleTypes.POOF, pos.getX() + 0.5, pos.getY() + 1.2, pos.getZ() + 0.5, 0, 0, 0);
                world.playSound(pos.getX(), pos.getY(), pos.getZ(), SoundEvents.BLOCK_FIRE_EXTINGUISH, SoundCategory.BLOCKS, 1f, 1f, true);
                world.setBlockState(pos, Blocks.OBSIDIAN.getDefaultState());
            }
        }
    }

    public static final long thunderTime = 200;
    public static final int thunderPlayerWeight = 50;


    //打雷：越平坦的地方玩家越容易被劈，越高的地方越容易被劈
    public static void thunder(ServerWorld world1) throws IllegalAccessException, InvocationTargetException, NoSuchMethodException {
        LinkedHashMap<ChunkPos, Double> chunkPosMap = new LinkedHashMap<>();
        if ((world1.isThundering() || world1.isRaining()) && world1.getGameTime() % thunderTime == 0) {
            int avgSum = 0, avgMin = 255;
            for (ServerPlayerEntity world1Player : world1.getPlayers()) {
                int playerChunkCoordX = world1Player.chunkCoordX, playerChunkCoordZ = world1Player.chunkCoordZ;
                Chunk chunk;
                Heightmap heightmap;
                BitArray data;
                for (int i = -4; i <= 4; i++) {
                    for (int j = -4; j <= 4; j++) {
                        final int[] sum = {0};
                        chunk = world1.getChunk(playerChunkCoordX + i, playerChunkCoordZ + j);
                        if (chunkPosMap.get(chunk.getPos()) != null) break;
                        heightmap = chunk.getHeightmap(Heightmap.Type.MOTION_BLOCKING);
                        data = (BitArray) heightmap.getClass().getDeclaredMethod("getBitArray").invoke(heightmap);
                        data.getAll(value -> sum[0] += value);
                        int avg = (i == 0 && j == 0 ? (sum[0] / 256) + thunderPlayerWeight : sum[0] / 256);
                        avgSum += avg;
                        avgMin = Math.min(avg, avgMin);
                        chunkPosMap.put(chunk.getPos(), (double) avg);
                    }
                }
            }
            int finalAvgMin = avgMin;
            for (Map.Entry<ChunkPos, Double> entry : chunkPosMap.entrySet()) {
                entry.setValue(entry.getValue() - avgMin);
            }
            avgSum -= avgMin * chunkPosMap.size();
            double v = random.nextDouble() * avgSum;
            double avgSum1 = 0;
            ChunkPos chunkPos = null;
            for (Map.Entry<ChunkPos, Double> entry : chunkPosMap.entrySet()) {
                Double value = entry.getValue();
                if (value == 0) continue;
                avgSum1 += value;
                if (v <= avgSum1) {
                    chunkPos = entry.getKey();
                    break;
                }
            }
            if (chunkPos != null) {
                List<LivingEntity> entityList = world1.getEntitiesWithinAABB(LivingEntity.class, new AxisAlignedBB(new BlockPos((chunkPos.x << 4), 0, (chunkPos.z << 4)), new BlockPos((chunkPos.x << 4) + 15, 255, (chunkPos.z << 4) + 15)));
                for (LivingEntity livingEntity : entityList) {
                    BlockPos pos = livingEntity.getPosition();
                    int x1 = pos.getX(), y1 = pos.getY(), z1 = pos.getZ();
                    int height1 = world1.getHeight(Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, x1, z1);
                    int height2 = world1.getHeight(Heightmap.Type.MOTION_BLOCKING, x1, z1);
                    if (y1 == height1 || y1 + 1 == height1 || y1 == height2 || y1 - 1 == height2) {
                        LightningBoltEntity lightningBolt = new LightningBoltEntity(EntityType.LIGHTNING_BOLT, world1);
                        lightningBolt.setPosition(x1, y1, z1);
                        world1.addEntity(lightningBolt);
                        break;
                    }
                }
                if (entityList.isEmpty()) {
                    LightningBoltEntity lightningBolt = new LightningBoltEntity(EntityType.LIGHTNING_BOLT, world1);
                    int x1 = (chunkPos.x << 4) + random.nextInt(15), z1 = (chunkPos.z << 4) + random.nextInt(15);
                    int y1 = world1.getHeight(Heightmap.Type.MOTION_BLOCKING, x1, z1);
                    lightningBolt.setPosition(x1, y1, z1);
                    world1.addEntity(lightningBolt);
                }
            }
        }
    }

    private static final ResourceLocation FIRE_RAIN_TEXTURES = new ResourceLocation(ifscience.MOD_ID, "textures/environment/fire_rain.png");

    //下火雨
    public static int renderFireRain(float partialTicks, double xIn, double yIn, double zIn, float f, World world, Tessellator tessellator, BufferBuilder bufferbuilder, float l, int i1, int k1, int j1, Random random, BlockPos.Mutable blockpos$mutable, int l2, double d0, int k2, double d1, int j2, Minecraft mc, int ticks) {
        if (i1 != 3) {
            if (i1 >= 0) {
                tessellator.draw();
            }

            i1 = 3;
            mc.getTextureManager().bindTexture(FIRE_RAIN_TEXTURES);
            bufferbuilder.begin(7, DefaultVertexFormats.PARTICLE_POSITION_TEX_COLOR_LMAP);
        }

        int i3 = ticks + k1 * k1 * 3121 + k1 * 45238971 + j1 * j1 * 418711 + j1 * 13761 & 31;
        float f3 = -((float) i3 + partialTicks) / 32.0F * (3.0F + random.nextFloat());
        double d2 = (double) ((float) k1 + 0.5F) - xIn;
        double d4 = (double) ((float) j1 + 0.5F) - zIn;
        float f4 = MathHelper.sqrt(d2 * d2 + d4 * d4) / l;
        float f5 = ((1.0F - f4 * f4) * 0.5F + 0.5F) * f;
        blockpos$mutable.setPos(k1, l2, j1);
        int j3 = WorldRenderer.getCombinedLight(world, blockpos$mutable);
        bufferbuilder.pos((double) k1 - xIn - d0 + 0.5D, (double) k2 - yIn, (double) j1 - zIn - d1 + 0.5D).tex(0.0F, (float) j2 * 0.25F + f3).color(1.0F, 1.0F, 1.0F, f5).lightmap(j3).endVertex();
        bufferbuilder.pos((double) k1 - xIn + d0 + 0.5D, (double) k2 - yIn, (double) j1 - zIn + d1 + 0.5D).tex(1.0F, (float) j2 * 0.25F + f3).color(1.0F, 1.0F, 1.0F, f5).lightmap(j3).endVertex();
        bufferbuilder.pos((double) k1 - xIn + d0 + 0.5D, (double) j2 - yIn, (double) j1 - zIn + d1 + 0.5D).tex(1.0F, (float) k2 * 0.25F + f3).color(1.0F, 1.0F, 1.0F, f5).lightmap(j3).endVertex();
        bufferbuilder.pos((double) k1 - xIn - d0 + 0.5D, (double) j2 - yIn, (double) j1 - zIn - d1 + 0.5D).tex(0.0F, (float) k2 * 0.25F + f3).color(1.0F, 1.0F, 1.0F, f5).lightmap(j3).endVertex();
        return i1;
    }

    //三叉戟激流坠地效果
    public static void TridentRiptide(ServerPlayerEntity player1, ServerWorld world1, BlockPos posPlayer) {
        if (player1.getMotion().y <= -2.25 && world1.getBlockState(posPlayer.down()).getBlock().equals(Blocks.AIR)
                && Items.TRIDENT.equals(player1.getHeldItemMainhand().getItem())) {
            BlockPos centerPos = posPlayer.down();
            BlockPos.Mutable blockPos$Mutable = new BlockPos.Mutable();
            int radius = 7;
            Vector3d vector3d = new Vector3d(0, 10, 0);
            for (int y = 2; y >= -5 && radius > 0; y--) {
                for (int x = -radius; x <= radius; x++) {
                    for (int z = -radius; z <= radius; z++) {
                        if (x * x + z * z <= radius * radius) {
                            blockPos$Mutable.setPos(centerPos.add(x, y, z));
                            if (!world1.getBlockState(blockPos$Mutable).getBlock().equals(Blocks.AIR)) {
                                if (y >= 0) {
                                    Vector3d direction = new Vector3d(blockPos$Mutable.getX() - centerPos.getX(), 0, blockPos$Mutable.getZ() - centerPos.getZ());
                                    direction = direction.normalize();
                                    blockEntity blockEntity = new blockEntity(world1, blockPos$Mutable.getX() + 0.5, blockPos$Mutable.getY(), blockPos$Mutable.getZ() + 0.5, world1.getBlockState(blockPos$Mutable));
                                    world1.setBlockState(blockPos$Mutable, Blocks.AIR.getDefaultState());
                                    blockEntity.setMotion(direction.x * 0.15, 0.65, direction.z * 0.15);
                                    world1.addEntity(blockEntity);
                                    Network.INSTANCE.send(PacketDistributor.TRACKING_ENTITY.with(() -> blockEntity), new blockEntityRenderPack(blockEntity.getUniqueID(), blockEntity.blockState));
                                } else {
                                    world1.setBlockState(blockPos$Mutable, Blocks.AIR.getDefaultState());
                                }
                            }
                        }
                    }
                }
                if (y < 0) --radius;
            }
        }
    }
}
