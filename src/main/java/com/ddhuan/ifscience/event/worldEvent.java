package com.ddhuan.ifscience.event;

import net.minecraft.block.Blocks;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.chunk.IChunk;
import net.minecraft.world.gen.Heightmap;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.world.ChunkEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.HashSet;

@Mod.EventBusSubscriber()
public class worldEvent {
    @SubscribeEvent
    public static void onWorldTick(TickEvent.WorldTickEvent event) {
        World world = event.world;
        if (!world.isRemote()) {
            /*if (world.isRaining()) {
                rainingTick(world);
            }*/
        }
    }

    static HashSet<IChunk> chunkHashSets = new HashSet<>();

    //进行下雨时候的操作
    private static void rainingTick(World world) {
        BlockPos.Mutable blockpos$mutable = new BlockPos.Mutable();
        //world.getHeight(Heightmap.Type.MOTION_BLOCKING, )
    }
}
