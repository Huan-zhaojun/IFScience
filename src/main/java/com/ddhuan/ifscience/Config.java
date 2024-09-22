package com.ddhuan.ifscience;

import net.minecraftforge.common.ForgeConfigSpec;

public class Config {
    public static ForgeConfigSpec COMMON_CONFIG;
    //附魔
    public static ForgeConfigSpec.BooleanValue ENCHANTING_ALL;//附魔不受物品类型和魔咒冲突的限制

    static {

    }

    public static void init() {
        ForgeConfigSpec.Builder COMMON_BUILDER = new ForgeConfigSpec.Builder();
        COMMON_BUILDER.comment("General settings");
        //附魔
        ENCHANTING_ALL = COMMON_BUILDER.comment("Enchanting is not limited by Type of item or Enchantment conflicts", "附魔不受物品类型和魔咒冲突的限制")
                .translation("config.ifscience.enchantment.enchanting_all")
                .define("enchantingAll", false);

        Rain(COMMON_BUILDER);////雨天玩法配置
        MagnetAttracted(COMMON_BUILDER);//磁吸玩法配置设置
        Minecart(COMMON_BUILDER);//矿车-寒冰铁轨玩法配置
        CutBlock(COMMON_BUILDER);//切割方块玩法配置
        Torch(COMMON_BUILDER);//火把玩法配置设置

        COMMON_CONFIG = COMMON_BUILDER.build();
    }

    //雨天玩法配置
    public static ForgeConfigSpec.BooleanValue RAIN, PUDDLE, SOLIDIFY_LAVA, FIRE_RAIN, THUNDER;
    public static ForgeConfigSpec.IntValue PUDDLE_TIME, THUNDER_TIME, THUNDER_PLAYER_WEIGHT;

    public static void Rain(ForgeConfigSpec.Builder COMMON_BUILDER) {
        COMMON_BUILDER.push("Rain-下雨");
        RAIN = COMMON_BUILDER.comment("Activate Rain Science gameplay", "开启下雨科学的玩法")
                .translation("config.ifscience.rain.gameplay")
                .define("gameplay", true);
        PUDDLE = COMMON_BUILDER.comment("Rain cause Puddle on the ground", "下雨会产生地面积水")
                .translation("config.ifscience.rain.puddle")
                .define("puddle", true);
        PUDDLE_TIME = COMMON_BUILDER.comment("The time interval for puddle, unit:tick", "积水产生的时间间隔，单位：刻")
                .translation("config.ifscience.rain.puddle_time")
                .defineInRange("puddleTime", 40, 1, Integer.MAX_VALUE);
        SOLIDIFY_LAVA = COMMON_BUILDER.comment("Rain causes Lava to solidify", "下雨会导致岩浆凝固")
                .translation("config.ifscience.rain.solidify_lava")
                .define("solidifyLava", true);
        FIRE_RAIN = COMMON_BUILDER.comment("drop fire rain", "下火雨")
                .translation("config.ifscience.rain.fire_rain")
                .define("fireRain", true);
        THUNDER = COMMON_BUILDER.comment("More realistic thunder and lightning in rainy weather:The flatter the terrain, the easier it is for players to be struck by lightning and The higher the terrain, the easier it is to be struck by lightning", "更加真实的雨天打雷闪电：越平坦的地方玩家越容易被劈，越高的地方越容易被劈")
                .translation("config.ifscience.rain.thunder")
                .define("thunder", true);
        THUNDER_TIME = COMMON_BUILDER.comment("Time interval of lightning strikes, unit:tick", "雷劈的时间间隔，单位：刻")
                .translation("config.ifscience.rain.thunder_time")
                .defineInRange("thunderTime", 200, 1, Integer.MAX_VALUE);
        THUNDER_PLAYER_WEIGHT = COMMON_BUILDER.comment("The extra Weight of the player being struck by lightning", "玩家被雷劈的额外权重")
                .translation("config.ifscience.rain.player_weight")
                .defineInRange("playerWeight", 50, 0, Short.MAX_VALUE);
        COMMON_BUILDER.pop();
    }

    //火把玩法配置设置
    public static ForgeConfigSpec.BooleanValue TORCH, EXTINGUISH, BURN;
    public static ForgeConfigSpec.IntValue TORCH_FIRE;

    private static void Torch(ForgeConfigSpec.Builder COMMON_BUILDER) {
        COMMON_BUILDER.push("Torch-火把");
        TORCH = COMMON_BUILDER.comment("Activate Torch Science gameplay", "开启火把科学玩法")
                .translation("config.ifscience.torch.gameplay")
                .define("gameplay", true);
        EXTINGUISH = COMMON_BUILDER.comment("The torch will extinguish", "火把会熄灭")
                .translation("config.ifscience.torch.extinguish")
                .define("extinguish", true);
        BURN = COMMON_BUILDER.comment("The torch can burn creatures", "火把会烫伤生物")
                .translation("config.ifscience.torch.burn")
                .define("burn", true);
        TORCH_FIRE = COMMON_BUILDER.comment("Probability of a torch causing a fire per second", "每秒火把导致着火的概率")
                .translation("config.ifscience.torch.fire")
                .defineInRange("fire", 100, 0, 1000);
        COMMON_BUILDER.pop();
    }

    //切割方块玩法配置
    public static ForgeConfigSpec.IntValue REBOUND1, REBOUND2;
    public static ForgeConfigSpec.IntValue LEVEL_CUT_TICK, VERTICAL_CUT_TICK, CUT_BLOCK_TICK, CUTBLOCK_MAX_LIFE_TICK;
    public static ForgeConfigSpec.BooleanValue CUT_EVERYTHING;

    private static void CutBlock(ForgeConfigSpec.Builder COMMON_BUILDER) {
        COMMON_BUILDER.push("CutBlock-切割方块");
        CUT_EVERYTHING = COMMON_BUILDER.comment("Is cut-all-blocks mode on?", "是否开启可切割一切方块模式？")
                .translation("config.ifscience.cutblock.cut_everything")
                .define("cutEverything", false);
        REBOUND1 = COMMON_BUILDER.comment("Probability of rebound caused by cutting blocks with lower hardness", "切割硬度更小的方块导致反弹的概率")
                .translation("config.ifscience.cutblock.rebound1")
                .defineInRange("rebound1", 10, 0, 1000);
        REBOUND2 = COMMON_BUILDER.comment("Probability of rebound caused by cutting blocks with equal hardness", "切割硬度相等的方块导致反弹的概率")
                .translation("config.ifscience.cutblock.rebound2")
                .defineInRange("rebound2", 100, 0, 1000);
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
    }

    //矿车-寒冰铁轨玩法配置
    public static ForgeConfigSpec.BooleanValue IS_COLLISION_FLY;
    public static ForgeConfigSpec.DoubleValue MAX_SPEED;
    public static ForgeConfigSpec.DoubleValue SLOW_RATIO;

    private static void Minecart(ForgeConfigSpec.Builder COMMON_BUILDER) {
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
    }

    //磁吸玩法配置设置
    public static ForgeConfigSpec.BooleanValue CAN_FEED_IRON, CHECK_IRON_EQUIPMENT;
    public static ForgeConfigSpec.DoubleValue SPEED_DEFAULT;
    public static ForgeConfigSpec.IntValue RADIUS_DEFAULT, Attack_AMOUNT, COLLISION_FLAG_DEFAULT;

    private static void MagnetAttracted(ForgeConfigSpec.Builder COMMON_BUILDER) {
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
    }
}
