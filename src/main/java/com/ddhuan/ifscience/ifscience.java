package com.ddhuan.ifscience;

import com.ddhuan.ifscience.common.Item.itemRegistry;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
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
        modEventBus.addListener(this::setup);
        itemRegistry.ITEMS.register(modEventBus);
    }

    private void setup(final FMLCommonSetupEvent event) {
        LOGGER.info("test!!!");
    }
}
