package com.ddhuan.ifscience.Custom;

import com.ddhuan.ifscience.Config;
import com.ddhuan.ifscience.common.Entity.MagnetAttractedBlockEntity;
import com.ddhuan.ifscience.common.Entity.entityTypeRegistry;
import com.ddhuan.ifscience.common.Item.HorseshoeMagnetItem;
import com.ddhuan.ifscience.common.Item.itemRegistry;
import com.ddhuan.ifscience.network.Client.playSoundPack;
import com.ddhuan.ifscience.network.Network;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MoverType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.*;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.fml.network.PacketDistributor;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.HashSet;

//磁铁，吸铁石的玩法
public class magnetUtil {
    private magnetUtil() {
    }

    public static boolean canFeedIron_LivingEntity = Config.CAN_FEED_IRON.get()/*是否能给生物喂食铁锭*/, checkIronEquipment = Config.CHECK_IRON_EQUIPMENT.get()/*是否检查生物携带铁制物品*/;
    public static double speed = Config.SPEED_DEFAULT.get();//速度量
    public static int radius = Config.RADIUS_DEFAULT.get();//作用半径
    public static int collisionFlag = Config.COLLISION_FLAG_DEFAULT.get();//被磁吸方块碰撞设置，默认设置为0，一律反弹为1，一律成掉落物为2
    public static HashSet<Block> magnetAttractedBlocks = new HashSet<>(Arrays.asList(Blocks.RAIL, Blocks.POWERED_RAIL,
            Blocks.ACTIVATOR_RAIL, Blocks.DETECTOR_RAIL));
    public static HashSet<EntityType<?>> magnetAttractedEntities = new HashSet<>(Arrays.asList(EntityType.IRON_GOLEM, EntityType.RAVAGER));
    public static HashSet<Item> magnetAttractedItems = new HashSet<>(Arrays.asList(Items.IRON_INGOT, Items.IRON_NUGGET,
            Items.IRON_HORSE_ARMOR, Items.BUCKET, Items.COMPASS, Items.CROSSBOW, Items.FLINT_AND_STEEL));

    public static RegistryObject<HorseshoeMagnetItem> horseshoeMagnet = itemRegistry.horseshoeMagnet;

    public static final RegistryObject<EntityType<MagnetAttractedBlockEntity>> MagnetAttractedBlockEntity = entityTypeRegistry.MagnetAttractedBlockEntity;

    public static void updateConfig_StaticValue() {
        magnetUtil.canFeedIron_LivingEntity = Config.CAN_FEED_IRON.get();
        magnetUtil.checkIronEquipment = Config.CHECK_IRON_EQUIPMENT.get();
        magnetUtil.speed = Config.SPEED_DEFAULT.get();
        magnetUtil.radius = Config.RADIUS_DEFAULT.get();
        magnetUtil.collisionFlag = Config.COLLISION_FLAG_DEFAULT.get();
    }

    //生物是否穿戴铁质装备和拿着铁质工具
    public static boolean isIronEquipment(LivingEntity entity) {
        if (!checkIronEquipment) return false;
        for (ItemStack itemStack : entity.getEquipmentAndArmor()) {
            Item item = itemStack.getItem();
            if (item instanceof ArmorItem) {
                ArmorItem armorItem = (ArmorItem) item;
                if (armorItem.getArmorMaterial().equals(ArmorMaterial.IRON) || armorItem.getArmorMaterial().equals(ArmorMaterial.CHAIN))
                    return true;
            } else if (item instanceof TieredItem) {
                TieredItem tieredItem = (TieredItem) item;
                if (tieredItem.getTier().equals(ItemTier.IRON))
                    return true;
            } else if (item instanceof BlockItem) {
                BlockItem blockItem = (BlockItem) item;
                Block block = blockItem.getBlock();
                Material material = block.getDefaultState().getMaterial();
                if (Material.IRON.equals(material) || Material.ANVIL.equals(material) || magnetAttractedBlocks.contains(block)) {
                    return true;
                }
            } else if (magnetAttractedItems.contains(item)) {
                return true;
            }
        }
        return false;
    }

    //给生物喂铁锭上磁性
    public static void LivingEntity_setMagnetAttract(PlayerInteractEvent.EntityInteract event, World world, Entity entity, PlayerEntity player) throws NoSuchFieldException, IllegalAccessException {
        ItemStack heldItem = player.getHeldItem(event.getHand());
        if (Items.IRON_INGOT.equals(heldItem.getItem()) && entity instanceof LivingEntity && !(entity instanceof PlayerEntity)
                && !magnetAttractedEntities.contains(entity.getType())) {
            LivingEntity livingEntity = (LivingEntity) entity;
            Field canAttractField = livingEntity.getClass().getField("canAttract");
            if ((boolean) canAttractField.get(livingEntity)) return;//喂过铁锭的不能再喂了
            canAttractField.set(entity, true);
            heldItem.shrink(1);
            Network.INSTANCE.send(PacketDistributor.ALL.noArg(),
                    new playSoundPack(livingEntity.getPosX(), livingEntity.getPosY(), livingEntity.getPosZ(),
                            "block.anvil.use", "VOICE",
                            1F, world.rand.nextFloat() * 0.1F + 0.9F,
                            false));
        }
    }

    //生物被磁吸时候移动
    public static void LivingEntity_MagnetAttractMove(LivingEntity entity, CallbackInfo ci) throws NoSuchFieldException, IllegalAccessException {
        Field isAttracted_Field = entity.getClass().getField("isAttracted");
        boolean isAttracted = (boolean) isAttracted_Field.get(entity);
        if (isAttracted) {//处在被磁吸者磁吸时
            PlayerEntity magnetAttractor = (PlayerEntity) entity.getClass().getField("magnetAttractor").get(entity);

            if (magnetAttractor != null && Math.pow(magnetAttractor.getPosX() - entity.getPosX(), 2) + Math.pow(magnetAttractor.getPosY() - entity.getPosY(), 2) + Math.pow(magnetAttractor.getPosZ() - entity.getPosZ(), 2) > Math.pow(magnetUtil.radius, 2)) {
                isAttracted_Field.set(entity, false);//超出磁铁作用半径
                return;
            } else if (magnetAttractor != null && !horseshoeMagnet.get().equals(magnetAttractor.getHeldItemMainhand().getItem()) &&
                    !horseshoeMagnet.get().equals(magnetAttractor.getHeldItemOffhand().getItem())) {
                isAttracted_Field.set(entity, false);//磁铁没有拿在手上
                return;
            }
            if (magnetAttractor != null && !entity.world.isRemote) {
                Vector3d positionVec = magnetAttractor.getPositionVec().add(0, 0.5, 0).subtract(entity.getPosX(), entity.getPosY(), entity.getPosZ());
                Vector3d speedVec = positionVec.normalize().scale(magnetUtil.speed);
                double speedY = 0.5 * 0.03D * Math.sqrt((Math.pow(positionVec.x, 2) + Math.pow(positionVec.z, 2)) / ((Math.pow(speedVec.x, 2) + Math.pow(speedVec.z, 2))));
                entity.setMotion(speedVec.add(0, speedY, 0));
                entity.move(MoverType.SELF, entity.getMotion());
            }

            ci.cancel();
        }
    }
}
