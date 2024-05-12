package com.ddhuan.ifscience.event;

import com.ddhuan.ifscience.Custom.rainingUtil;
import net.minecraft.world.World;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber()
public class worldEvent {

    @SubscribeEvent
    public static void onWorldTick(TickEvent.WorldTickEvent event) {
        World world = event.world;
        if (!world.isRemote()) {
            rainingUtil.placePuddle(world);//下雨产生地面积水
        }
    }
}