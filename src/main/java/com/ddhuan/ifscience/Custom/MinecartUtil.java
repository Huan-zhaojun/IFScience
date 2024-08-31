package com.ddhuan.ifscience.Custom;

import com.ddhuan.ifscience.Config;
import com.ddhuan.ifscience.common.customDamage;
import com.ddhuan.ifscience.network.Client.entityMotionPack;
import com.ddhuan.ifscience.network.Network;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.item.minecart.AbstractMinecartEntity;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.PacketDistributor;

public class MinecartUtil {
    public static boolean IS_COLLISION_FLY = true;//是否撞飞生物造成伤害
    public static double MAX_SPEED = 3.0D;//矿车最大速度
    public static double SLOW_RATIO = 0.15D;//寒冰铁轨减速比例

    public static void updateConfig_StaticValue() {
        IS_COLLISION_FLY = Config.IS_COLLISION_FLY.get();
        MAX_SPEED = Config.MAX_SPEED.get();
        SLOW_RATIO = Config.SLOW_RATIO.get();
    }

    public static void knockEntity(Vector3d speedVec, AbstractMinecartEntity minecart) {
        World world = minecart.world;
        if (!world.isRemote) {
            for (Entity entity : world.getEntitiesWithinAABBExcludingEntity(minecart, new AxisAlignedBB(minecart.getPositionVec(),minecart.getPositionVec()).grow(0.5, 0, 0.5))) {
                if (entity instanceof LivingEntity && !minecart.isPassenger(entity)) {
                    Vector3d vec = new Vector3d(Math.copySign(Math.min(Math.abs(speedVec.x), 2), speedVec.x), 0, Math.copySign(Math.min(Math.abs(speedVec.z), 2), speedVec.z))
                            .add(Math.random() * 0.5, 0.5, Math.random() * 0.5);
                    entity.setMotion(vec);//撞飞生物
                    Network.INSTANCE.send(PacketDistributor.TRACKING_ENTITY_AND_SELF.with(() -> entity), new entityMotionPack(entity.getUniqueID(), vec));
                    entity.attackEntityFrom(customDamage.MinecartBump1, Math.max((float) speedVec.length() * 3, 6));//撞死生物
                    for (Entity passenger : minecart.getPassengers()) {
                        passenger.attackEntityFrom(customDamage.MinecartBump2, Math.max((float) speedVec.length() * 1.5f, 4f));
                    }
                }
            }
        }
    }
}
