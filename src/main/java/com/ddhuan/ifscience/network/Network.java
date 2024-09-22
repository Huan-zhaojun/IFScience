package com.ddhuan.ifscience.network;

import com.ddhuan.ifscience.ifscience;
import com.ddhuan.ifscience.network.Client.*;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.network.NetworkRegistry;
import net.minecraftforge.fml.network.simple.SimpleChannel;

public class Network {
    public static final String VERSION = "1.0";
    private static int ID = 0;

    public static SimpleChannel INSTANCE = NetworkRegistry.newSimpleChannel(
            new ResourceLocation(ifscience.MOD_ID, "network"),
            () -> VERSION, VERSION::equals, VERSION::equals);

    public static int nextID() {
        return ID++;
    }

    public static void registerMessage() {
        INSTANCE.registerMessage(nextID(), fireRenderPack.class, fireRenderPack::toBytes, fireRenderPack::new, fireRenderPack::handler);
        INSTANCE.registerMessage(nextID(), cloudParticlePack.class, cloudParticlePack::toBytes, cloudParticlePack::new, cloudParticlePack::handler);
        INSTANCE.registerMessage(nextID(), furnaceTNTRenderPack.class, furnaceTNTRenderPack::toBytes, furnaceTNTRenderPack::new, furnaceTNTRenderPack::handler);
        INSTANCE.registerMessage(nextID(), playerPosePack.class, playerPosePack::toBytes, playerPosePack::new, playerPosePack::handler);
        INSTANCE.registerMessage(nextID(), entityMotionPack.class, entityMotionPack::toBytes, entityMotionPack::new, entityMotionPack::handler);
        INSTANCE.registerMessage(nextID(), blockEntityRenderPack.class, blockEntityRenderPack::toBytes, blockEntityRenderPack::new, blockEntityRenderPack::handler);
        INSTANCE.registerMessage(nextID(), playSoundPack.class, playSoundPack::toBytes, playSoundPack::new, playSoundPack::handler);
        INSTANCE.registerMessage(nextID(), magnetAttractPack.class, magnetAttractPack::toBytes, magnetAttractPack::new, magnetAttractPack::handler);
        INSTANCE.registerMessage(nextID(), EnchantedBlocksDataPack.class, EnchantedBlocksDataPack::toBytes, EnchantedBlocksDataPack::new, EnchantedBlocksDataPack::handler);
        INSTANCE.registerMessage(nextID(), BlockBreakProgressPack.class, BlockBreakProgressPack::toBytes, BlockBreakProgressPack::new, BlockBreakProgressPack::handler);
    }
}
