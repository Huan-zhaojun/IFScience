package com.ddhuan.ifscience.event;

import com.ddhuan.ifscience.Custom.rainingUtil;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.lang.reflect.InvocationTargetException;

import static com.ddhuan.ifscience.Config.*;

@Mod.EventBusSubscriber()
public class worldEvent {

    @SubscribeEvent
    public static void onWorldTick(TickEvent.WorldTickEvent event) throws InvocationTargetException, IllegalAccessException, NoSuchMethodException {
        World world = event.world;
        if (!world.isRemote()) {
            ServerWorld world1 = (ServerWorld) world;
            if (RAIN.get()) {
                if (PUDDLE.get())
                    rainingUtil.placePuddle(world);//下雨产生地面积水
                if (THUNDER.get())
                    rainingUtil.thunder(world1);//打雷
                if (FIRE_RAIN.get())
                    rainingUtil.fireRainHurt(world1);//火雨伤害
            }
        }
    }
}