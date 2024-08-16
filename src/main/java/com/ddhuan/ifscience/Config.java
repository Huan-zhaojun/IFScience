package com.ddhuan.ifscience;

import net.minecraftforge.common.ForgeConfigSpec;

public class Config {
    public static ForgeConfigSpec COMMON_CONFIG;
    //磁吸玩法配置设置
    public static ForgeConfigSpec.DoubleValue SPEED_DEFAULT;
    public static ForgeConfigSpec.IntValue RADIUS_DEFAULT, COLLISION_FLAG_DEFAULT;

    static {

    }

    public static void init() {
        ForgeConfigSpec.Builder COMMON_BUILDER = new ForgeConfigSpec.Builder();
        COMMON_BUILDER.comment("General settings");

        //磁吸玩法配置设置
        COMMON_BUILDER.push("Magnet Attracted");
        SPEED_DEFAULT = COMMON_BUILDER.comment("The flight rate of an object when it is magnetically attracted", "物体被磁吸时的飞行速率量")
                .defineInRange("speed", 0.5, 0.01, 100);
        RADIUS_DEFAULT = COMMON_BUILDER.comment("The radius of an object attracted by a magnet", "磁铁吸引物体的半径")
                .defineInRange("radius", 20, 1, Byte.MAX_VALUE);
        COLLISION_FLAG_DEFAULT = COMMON_BUILDER.comment("MagnetAttracted block collision setting: The default is 0, \"All will rebound\" is 1, \"All will drops\" is 2", "被磁吸方块碰撞设置，默认设置为0，一律反弹为1，一律成掉落物为2")
                .defineInRange("collisionFlag", 0, 0, 2);
        COMMON_BUILDER.pop();
        COMMON_CONFIG = COMMON_BUILDER.build();
    }
}
