package com.ddhuan.ifscience;

import net.minecraftforge.common.ForgeConfigSpec;

public class Config {
    public static ForgeConfigSpec COMMON_CONFIG;
    //附魔
    public static ForgeConfigSpec.BooleanValue ENCHANTING_ALL;//附魔不受物品类型和魔咒冲突的限制
    //磁吸玩法配置设置
    public static ForgeConfigSpec.BooleanValue CAN_FEED_IRON, CHECK_IRON_EQUIPMENT;
    public static ForgeConfigSpec.DoubleValue SPEED_DEFAULT;
    public static ForgeConfigSpec.IntValue RADIUS_DEFAULT, Attack_AMOUNT, COLLISION_FLAG_DEFAULT;
    //矿车-寒冰铁轨玩法配置
    public static ForgeConfigSpec.BooleanValue IS_COLLISION_FLY;
    public static ForgeConfigSpec.DoubleValue MAX_SPEED;
    public static ForgeConfigSpec.DoubleValue SLOW_RATIO;
    //切割方块玩法配置
    public static ForgeConfigSpec.IntValue LEVEL_CUT_TICK, VERTICAL_CUT_TICK, CUT_BLOCK_TICK, CUTBLOCK_MAX_LIFE_TICK;
    public static ForgeConfigSpec.BooleanValue CUT_EVERYTHING;

    static {

    }

    public static void init() {
        ForgeConfigSpec.Builder COMMON_BUILDER = new ForgeConfigSpec.Builder();
        COMMON_BUILDER.comment("General settings");
        //附魔
        ENCHANTING_ALL = COMMON_BUILDER.comment("Enchanting is not limited by Type of item or Enchantment conflicts","附魔不受物品类型和魔咒冲突的限制")
                .translation("config.ifscience.enchantment.enchanting_all")
                .define("enchantingAll",false);

        //磁吸玩法配置设置
        COMMON_BUILDER.push("Magnet Attracted-磁吸");
        COMMON_BUILDER.push("Switch-开关");
        CAN_FEED_IRON = COMMON_BUILDER.comment("Whether to feed iron ingots to LivingEntity", "是否能给生物喂食铁锭")
                .translation("config.ifscience.magnet_attracted.canFeedIron")
                .define("canFeedIron", true);
        CHECK_IRON_EQUIPMENT = COMMON_BUILDER.comment("Whether to check whether the LivingEntity is carrying iron items", "是否检查生物携带铁制物品")
                .translation("config.ifscience.magnet_attracted.checkIronEquipment")
                .define("checkIronEquipment", true);
        COMMON_BUILDER.pop();
        COLLISION_FLAG_DEFAULT = COMMON_BUILDER.comment("MagnetAttracted block collision setting: The default is 0, \"All will rebound\" is 1, \"All will drops\" is 2", "被磁吸方块碰撞设置，默认设置为0，一律反弹为1，一律成掉落物为2")
                .translation("config.ifscience.magnet_attracted.collisionFlag")
                .defineInRange("collisionFlag", 0, 0, 2);
        Attack_AMOUNT = COMMON_BUILDER.comment("The amount of damage caused by the collision", "被碰撞到的伤害量")
                .translation("config.ifscience.magnet_attracted.attack_amount")
                .defineInRange("attack_amount", 8, 0, Integer.MAX_VALUE);
        SPEED_DEFAULT = COMMON_BUILDER.comment("The flight rate of an object when it is magnetically attracted", "物体被磁吸时的飞行速率量")
                .translation("config.ifscience.magnet_attracted.speed")
                .defineInRange("speed", 0.5, 0.01, 100);
        RADIUS_DEFAULT = COMMON_BUILDER.comment("The radius of an object attracted by a magnet", "磁铁吸引物体的半径")
                .translation("config.ifscience.magnet_attracted.radius")
                .defineInRange("radius", 20, 1, Byte.MAX_VALUE);
        COMMON_BUILDER.pop();
        //矿车-寒冰铁轨玩法配置
        COMMON_BUILDER.push("Minecart-矿车");
        IS_COLLISION_FLY = COMMON_BUILDER.comment("Minecart accident, collision and flying", "矿车出车祸发生碰撞和击飞")
                .translation("config.ifscience.minecart.isCollisionFly")
                .define("isCollisionFly", true);
        MAX_SPEED = COMMON_BUILDER.comment("Maximum speed of Minecart", "矿车最大速度")
                .translation("config.ifscience.minecart.maxSpeed")
                .defineInRange("maxSpeed", 3.0D, 0.1D, Integer.MAX_VALUE);
        SLOW_RATIO = COMMON_BUILDER.comment("Ice rail slowdown ratio.The smaller it is, the more obvious", "寒冰铁轨减速比例，越小减速越明显")
                .translation("config.ifscience.minecart.slowRatio")
                .defineInRange("slowRatio", 0.15D, 0, 1);
        COMMON_BUILDER.pop();

        //切割方块玩法配置
        COMMON_BUILDER.push("CutBlock-切割方块");
        CUT_EVERYTHING = COMMON_BUILDER.comment("Is cut-all-blocks mode on?", "是否开启可切割一切方块模式？")
                .translation("config.ifscience.cutblock.cut_everything")
                .define("cutEverything", false);
        LEVEL_CUT_TICK = COMMON_BUILDER.comment("Horizontal cutting time,Unit: tick", "水平切割时间，单位：tick")
                .translation("config.ifscience.cutblock.level_cut_tick")
                .defineInRange("levelCutTick", 20, 1, Integer.MAX_VALUE / 50);
        VERTICAL_CUT_TICK = COMMON_BUILDER.comment("Vertical cutting time,Unit: tick", "竖直切割时间，单位：tick")
                .translation("config.ifscience.cutblock.vertical_cut_tick")
                .defineInRange("verticalCutTick", 20, 1, Integer.MAX_VALUE / 50);
        CUT_BLOCK_TICK = COMMON_BUILDER.comment("Splitting time after cutting the block,Unit: tick", "切完方块的分裂时间，单位：tick")
                .translation("config.ifscience.cutblock.cut_block_tick")
                .defineInRange("cutBlockTick", 10, 1, Integer.MAX_VALUE / 50);
        CUTBLOCK_MAX_LIFE_TICK = COMMON_BUILDER.comment("Maximum lifespan of cut blocks,Unit: tick", "切割方块最大生命时间，单位：tick")
                .translation("config.ifscience.cutblock.maxlife_cutblock_tick")
                .defineInRange("cutBlock_maxLifeTick", 60, 1, Integer.MAX_VALUE);
        COMMON_BUILDER.pop();
        COMMON_CONFIG = COMMON_BUILDER.build();
    }
}
