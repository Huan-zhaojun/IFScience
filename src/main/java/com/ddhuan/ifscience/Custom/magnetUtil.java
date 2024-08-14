package com.ddhuan.ifscience.Custom;

import com.ddhuan.ifscience.common.Entity.MagnetAttractedBlockEntity;
import com.ddhuan.ifscience.common.Entity.entityTypeRegistry;
import com.ddhuan.ifscience.common.Item.itemRegistry;
import com.ddhuan.ifscience.network.Client.blockEntityRenderPack;
import com.ddhuan.ifscience.network.Client.playSoundPack;
import com.ddhuan.ifscience.network.Network;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MoverType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.*;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.fml.network.PacketDistributor;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.UUID;

//磁铁，吸铁石的玩法
public class magnetUtil {
    private magnetUtil() {
    }

    public static double speed = 0.5;//速度量
    public static int radius = 20;//作用半径
    public static byte collisionFlag = 0;//被磁吸方块反弹设置，默认碰撞设置为0，一律反弹为1，一律成掉落物为2
    public static HashSet<Block> magnetAttractedBlocks = new HashSet<>(Arrays.asList(Blocks.RAIL, Blocks.POWERED_RAIL, Blocks.ACTIVATOR_RAIL, Blocks.DETECTOR_RAIL, Blocks.IRON_ORE));
    public static HashSet<EntityType<?>> magnetAttractedEntities = new HashSet<>(Arrays.asList(EntityType.IRON_GOLEM));

    public static RegistryObject<Item> horseshoeMagnet = itemRegistry.ITEMS.register("horseshoe_magnet",
            () -> new Item(new Item.Properties().group(itemRegistry.ifScience)) {

                @Override//右键长按
                public ActionResult<ItemStack> onItemRightClick(World worldIn, PlayerEntity playerIn, Hand handIn) {
                    playerIn.setActiveHand(handIn);
                    if (!worldIn.isRemote) {
                        BlockPos playerPos = playerIn.getPosition();
                        Iterable<BlockPos> it = BlockPos.getAllInBoxMutable(playerPos.add(radius, radius, radius), playerPos.add(-radius, -radius, -radius));
                        for (BlockPos blockPos : it) {
                            double p = Math.pow(blockPos.getX() - playerPos.getX(), 2) + Math.pow(blockPos.getY() - playerPos.getY(), 2) + Math.pow(blockPos.getZ() - playerPos.getZ(), 2);
                            if (p <= Math.pow(radius, 2)) {
                                //检测能被磁铁吸引的方块
                                BlockState blockState = worldIn.getBlockState(blockPos);
                                Material material = blockState.getMaterial();
                                if (Material.IRON.equals(material) || Material.ANVIL.equals(material) || magnetAttractedBlocks.contains(blockState.getBlock())) {
                                    MagnetAttractedBlockEntity blockEntity = new MagnetAttractedBlockEntity(worldIn, blockPos.getX() + 0.5, blockPos.getY(), blockPos.getZ() + 0.5, blockState, playerIn.getUniqueID());
                                    worldIn.setBlockState(blockPos, Blocks.AIR.getDefaultState());

                                    blockEntity.G = 0.05D;//重力加速度
                                    Vector3d positionVec = playerIn.getPositionVec().add(0, 0.5, 0).subtract(blockPos.getX() + 0.5, blockPos.getY(), blockPos.getZ() + 0.5);
                                    Vector3d speedVec = positionVec.normalize().scale(speed);
                                    double speedY = 0.5 * blockEntity.G * Math.sqrt((Math.pow(positionVec.x, 2) + Math.pow(positionVec.z, 2)) / ((Math.pow(speedVec.x, 2) + Math.pow(speedVec.z, 2))));
                                    blockEntity.setMotion(speedVec.add(0, speedY, 0));
                                    worldIn.addEntity(blockEntity);
                                    Network.INSTANCE.send(PacketDistributor.TRACKING_ENTITY.with(() -> blockEntity), new blockEntityRenderPack(blockEntity.getUniqueID(), blockEntity.blockState));
                                }
                            }
                        }
                        List<Entity> entityList = worldIn.getEntitiesInAABBexcluding(playerIn, new AxisAlignedBB(playerPos.add(radius, radius, radius), playerPos.add(-radius, -radius, -radius)), null);
                        for (Entity entity : entityList) {
                            //检测没有被磁吸而在飞的被磁吸方块实体
                            if (entity instanceof MagnetAttractedBlockEntity) {
                                MagnetAttractedBlockEntity entity1 = (MagnetAttractedBlockEntity) entity;
                                if (!entity1.isAttracted && entity1.noAttractedTick <= 0) {
                                    entity1.setMagnetAttractor(playerIn.getUniqueID());
                                }
                            }
                            try {
                                //检测到可以被磁吸，但未处于正在被磁吸的生物
                                if (entity instanceof LivingEntity && (magnetAttractedEntities.contains(entity.getType()) ||
                                        (boolean) entity.getClass().getField("canAttract").get(entity) || isIronEquipment((LivingEntity) entity)) &&
                                        !(boolean) entity.getClass().getField("isAttracted").get(entity)) {
                                    entity.getClass().getMethod("setMagnetAttractor", UUID.class).invoke(entity, playerIn.getUniqueID());
                                }
                            } catch (NoSuchFieldException | IllegalAccessException | NoSuchMethodException |
                                     InvocationTargetException e) {
                                throw new RuntimeException(e);
                            }
                        }
                    }
                    return super.onItemRightClick(worldIn, playerIn, handIn);
                }

                @Override//右键取消
                public void onPlayerStoppedUsing(ItemStack stack, World worldIn, LivingEntity entityLiving, int timeLeft) {
                    if (!worldIn.isRemote && entityLiving instanceof PlayerEntity) {
                        Vector3d positionVec = entityLiving.getPositionVec();
                        PlayerEntity player = (PlayerEntity) entityLiving;
                        List<Entity> entityList = worldIn.getEntitiesInAABBexcluding(entityLiving, new AxisAlignedBB(positionVec.add(radius, radius, radius), positionVec.add(-radius, -radius, -radius)), null);
                        for (Entity entity : entityList) {
                            if (entity instanceof MagnetAttractedBlockEntity) {//是被磁吸的方块
                                MagnetAttractedBlockEntity entity1 = (MagnetAttractedBlockEntity) entity;
                                if (player.getUniqueID().equals(entity1.getMagnetAttractor())) {
                                    entity1.isAttracted = false;//松手取消磁吸物体
                                }
                            }
                            try {
                                //是能被磁吸的生物、是正在处于被磁吸和是这个玩家磁吸的生物
                                if (entity instanceof LivingEntity && (magnetAttractedEntities.contains(entity.getType()) ||
                                        (boolean) entity.getClass().getField("canAttract").get(entity) || isIronEquipment((LivingEntity) entity)) &&
                                        (boolean) entity.getClass().getField("isAttracted").get(entity)) {
                                    PlayerEntity magnetAttractor = (PlayerEntity) entity.getClass().getField("magnetAttractor").get(entity);
                                    if (magnetAttractor != null && player.getUniqueID().equals(magnetAttractor.getUniqueID())) {
                                        entity.getClass().getField("isAttracted").set(entity, false);//松手取消磁吸生物
                                    }
                                }
                            } catch (NoSuchFieldException | IllegalAccessException e) {
                                throw new RuntimeException(e);
                            }
                        }
                    }
                }

                @Override
                public int getUseDuration(ItemStack stack) {
                    return 72000;
                }

                @Override
                public UseAction getUseAction(ItemStack stack) {
                    return UseAction.BOW;
                }
            });

    public static final RegistryObject<EntityType<MagnetAttractedBlockEntity>> MagnetAttractedBlockEntity = entityTypeRegistry.MagnetAttractedBlockEntity;

    //生物穿戴铁质装备和拿着铁质工具
    public static boolean isIronEquipment(LivingEntity entity) {
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
            }
        }
        return false;
    }

    //给生物喂铁锭上磁性
    public static void LivingEntity_setMagnetAttract(PlayerInteractEvent.EntityInteract event, World world, Entity entity, PlayerEntity player) throws NoSuchFieldException, IllegalAccessException {
        if (entity instanceof LivingEntity && !(entity instanceof PlayerEntity)
                && !magnetAttractedEntities.contains(entity.getType())) {
            ItemStack heldItem = player.getHeldItem(event.getHand());
            if (Items.IRON_INGOT.equals(heldItem.getItem())) {
                entity.getClass().getField("canAttract").set(entity, true);
                heldItem.shrink(1);
                Network.INSTANCE.send(PacketDistributor.ALL.noArg(),
                        new playSoundPack(entity.getPosX(), entity.getPosY(), entity.getPosZ(),
                                "block.anvil.use", "VOICE",
                                1F, world.rand.nextFloat() * 0.1F + 0.9F,
                                false));
            }
        }
    }

    //生物被磁吸时候移动
    public static void LivingEntity_MagnetAttractMove(LivingEntity entity, boolean isAttracted, PlayerEntity magnetAttractor, Vector3d magnetAttractor_lastPos, CallbackInfo ci) {
        if (isAttracted) {//处在被磁吸者磁吸时
            if (magnetAttractor != null && Math.pow(magnetAttractor.getPosX() - entity.getPosX(), 2) + Math.pow(magnetAttractor.getPosY() - entity.getPosY(), 2) + Math.pow(magnetAttractor.getPosZ() - entity.getPosZ(), 2) > Math.pow(magnetUtil.radius, 2)) {
                isAttracted = false;//超出磁铁作用半径
            } else if (magnetAttractor != null && (magnetAttractor_lastPos == null || magnetAttractor_lastPos.subtract(magnetAttractor.getPositionVec()).length() >= 0.1)) {
                magnetAttractor_lastPos = magnetAttractor.getPositionVec();
                Vector3d positionVec = magnetAttractor.getPositionVec().add(0, 0.5, 0).subtract(entity.getPosX(), entity.getPosY(), entity.getPosZ());
                Vector3d speedVec = positionVec.normalize().scale(magnetUtil.speed);
                double speedY = 0.5 * 0.05D * Math.sqrt((Math.pow(positionVec.x, 2) + Math.pow(positionVec.z, 2)) / ((Math.pow(speedVec.x, 2) + Math.pow(speedVec.z, 2))));
                entity.setMotion(speedVec.add(0, speedY, 0));
                entity.move(MoverType.SELF, entity.getMotion());
                ci.cancel();
            }
        }
    }
}
