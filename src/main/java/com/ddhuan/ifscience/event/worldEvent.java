package com.ddhuan.ifscience.event;

import com.ddhuan.ifscience.Custom.rainingUtil;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.lang.reflect.InvocationTargetException;

@Mod.EventBusSubscriber()
public class worldEvent {

    @SubscribeEvent
    public static void onWorldTick(TickEvent.WorldTickEvent event) throws InvocationTargetException, IllegalAccessException, NoSuchMethodException {
        World world = event.world;
        if (!world.isRemote()) {
            ServerWorld world1 = (ServerWorld) world;
            rainingUtil.placePuddle(world);//下雨产生地面积水
            rainingUtil.thunder(world1);//打雷
            rainingUtil.fireRainHurt(world1);//火雨伤害
        }
    }
}