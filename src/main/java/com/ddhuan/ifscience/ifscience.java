package com.ddhuan.ifscience;

import com.ddhuan.ifscience.common.Entity.entityTypeRegistry;
import com.ddhuan.ifscience.common.Entity.render.furnaceTNTRender;
import com.ddhuan.ifscience.common.Item.itemRegistry;
import com.ddhuan.ifscience.network.Network;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import static com.ddhuan.ifscience.ifscience.MOD_ID;


@Mod(MOD_ID)
public class ifscience {
    private static final Logger LOGGER = LogManager.getLogger();
    public static final String MOD_ID = "ifscience";

    public ifscience() {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
        MinecraftForge.EVENT_BUS.register(this);
        modEventBus.addListener(this::onCommonSetup);
        modEventBus.addListener(this::onClientSetUp);
        itemRegistry.ITEMS.register(modEventBus);
        entityTypeRegistry.ENTITIES.register(modEventBus);
    }

    private void onCommonSetup(final FMLCommonSetupEvent event) {
        //注册网络包
        Network.registerMessage();
    }

    @OnlyIn(Dist.CLIENT)
    private void onClientSetUp(FMLClientSetupEvent event) {
        //注册实体渲染器
        RenderingRegistry.registerEntityRenderingHandler(entityTypeRegistry.furnaceTNT.get(), furnaceTNTRender::new);
    }
}
