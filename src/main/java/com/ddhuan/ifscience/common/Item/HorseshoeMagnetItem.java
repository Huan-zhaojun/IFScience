package com.ddhuan.ifscience.common.Item;

import com.ddhuan.ifscience.Custom.magnetUtil;
import com.ddhuan.ifscience.common.Entity.MagnetAttractedBlockEntity;
import com.ddhuan.ifscience.network.Client.blockEntityRenderPack;
import com.ddhuan.ifscience.network.Client.magnetAttractPack;
import com.ddhuan.ifscience.network.Network;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.material.Material;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.UseAction;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.PacketDistributor;

import javax.annotation.Nullable;
import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.UUID;

public class HorseshoeMagnetItem extends Item {
    public HorseshoeMagnetItem(Properties properties) {
        super(properties);
    }

    @Override//右键长按
    public ActionResult<ItemStack> onItemRightClick(World worldIn, PlayerEntity playerIn, Hand handIn) {
        playerIn.setActiveHand(handIn);
        if (!worldIn.isRemote) {
            BlockPos playerPos = playerIn.getPosition();
            Iterable<BlockPos> it = BlockPos.getAllInBoxMutable(playerPos.add(magnetUtil.radius, magnetUtil.radius, magnetUtil.radius), playerPos.add(-magnetUtil.radius, -magnetUtil.radius, -magnetUtil.radius));
            for (BlockPos blockPos : it) {
                double p = Math.pow(blockPos.getX() - playerPos.getX(), 2) + Math.pow(blockPos.getY() - playerPos.getY(), 2) + Math.pow(blockPos.getZ() - playerPos.getZ(), 2);
                if (p <= Math.pow(magnetUtil.radius, 2)) {
                    //检测能被磁铁吸引的方块
                    BlockState blockState = worldIn.getBlockState(blockPos);
                    Material material = blockState.getMaterial();
                    if (Material.IRON.equals(material) || Material.ANVIL.equals(material) || magnetUtil.magnetAttractedBlocks.contains(blockState.getBlock())) {
                        MagnetAttractedBlockEntity blockEntity = new MagnetAttractedBlockEntity(worldIn, blockPos.getX() + 0.5, blockPos.getY(), blockPos.getZ() + 0.5, blockState, playerIn.getUniqueID());
                        worldIn.setBlockState(blockPos, Blocks.AIR.getDefaultState());

                        blockEntity.G = 0.05D;//重力加速度
                        Vector3d positionVec = playerIn.getPositionVec().add(0, 0.5, 0).subtract(blockPos.getX() + 0.5, blockPos.getY(), blockPos.getZ() + 0.5);
                        Vector3d speedVec = positionVec.normalize().scale(magnetUtil.speed);
                        double speedY = 0.5 * blockEntity.G * Math.sqrt((Math.pow(positionVec.x, 2) + Math.pow(positionVec.z, 2)) / ((Math.pow(speedVec.x, 2) + Math.pow(speedVec.z, 2))));
                        blockEntity.setMotion(speedVec.add(0, speedY, 0));
                        worldIn.addEntity(blockEntity);
                        //发包同步数据
                        Network.INSTANCE.send(PacketDistributor.TRACKING_ENTITY.with(() -> blockEntity), new blockEntityRenderPack(blockEntity.getUniqueID(), blockEntity.blockState));
                        Network.INSTANCE.send(PacketDistributor.TRACKING_ENTITY.with(() -> blockEntity), new magnetAttractPack(blockEntity.getClass(), blockEntity.getUniqueID(), playerIn.getUniqueID()));
                    }
                }
            }
            List<Entity> entityList = worldIn.getEntitiesInAABBexcluding(playerIn, new AxisAlignedBB(playerPos.add(magnetUtil.radius, magnetUtil.radius, magnetUtil.radius), playerPos.add(-magnetUtil.radius, -magnetUtil.radius, -magnetUtil.radius)), null);
            for (Entity entity : entityList) {
                //检测没有被磁吸而在飞的被磁吸方块实体
                if (entity instanceof MagnetAttractedBlockEntity) {
                    MagnetAttractedBlockEntity entity1 = (MagnetAttractedBlockEntity) entity;
                    if (!entity1.isAttracted && entity1.noAttractedTick <= 0) {
                        entity1.setMagnetAttractor(playerIn.getUniqueID());
                        Network.INSTANCE.send(PacketDistributor.TRACKING_ENTITY.with(() -> entity1), new magnetAttractPack(entity1.getClass(), entity1.getUniqueID(), playerIn.getUniqueID()));
                    }
                }
                try {
                    //检测到可以被磁吸，但未处于正在被磁吸的生物
                    if (entity instanceof LivingEntity && (magnetUtil.magnetAttractedEntities.contains(entity.getType()) ||
                            (boolean) entity.getClass().getField("canAttract").get(entity) || magnetUtil.isIronEquipment((LivingEntity) entity)) &&
                            !(boolean) entity.getClass().getField("isAttracted").get(entity)) {
                        entity.getClass().getMethod("setMagnetAttractor", UUID.class).invoke(entity, playerIn.getUniqueID());
                        Network.INSTANCE.send(PacketDistributor.TRACKING_ENTITY.with(() -> entity), new magnetAttractPack(entity.getClass(), entity.getUniqueID(), playerIn.getUniqueID()));
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
            List<Entity> entityList = worldIn.getEntitiesInAABBexcluding(entityLiving, new AxisAlignedBB(positionVec.add(magnetUtil.radius, magnetUtil.radius, magnetUtil.radius), positionVec.add(-magnetUtil.radius, -magnetUtil.radius, -magnetUtil.radius)), null);
            for (Entity entity : entityList) {
                if (entity instanceof MagnetAttractedBlockEntity) {//是被磁吸的方块
                    MagnetAttractedBlockEntity entity1 = (MagnetAttractedBlockEntity) entity;
                    if (player.getUniqueID().equals(entity1.getMagnetAttractor())) {
                        entity1.isAttracted = false;//松手取消磁吸物体
                        Network.INSTANCE.send(PacketDistributor.TRACKING_ENTITY.with(() -> entity1), new magnetAttractPack(entity1.getClass(), entity1.getUniqueID()));
                    }
                }
                try {
                    //是能被磁吸的生物、是正在处于被磁吸和是这个玩家磁吸的生物
                    if (entity instanceof LivingEntity && (magnetUtil.magnetAttractedEntities.contains(entity.getType()) ||
                            (boolean) entity.getClass().getField("canAttract").get(entity) || magnetUtil.isIronEquipment((LivingEntity) entity)) &&
                            (boolean) entity.getClass().getField("isAttracted").get(entity)) {
                        PlayerEntity magnetAttractor = (PlayerEntity) entity.getClass().getField("magnetAttractor").get(entity);
                        if (magnetAttractor != null && player.getUniqueID().equals(magnetAttractor.getUniqueID())) {
                            entity.getClass().getField("isAttracted").set(entity, false);//松手取消磁吸生物
                            Network.INSTANCE.send(PacketDistributor.TRACKING_ENTITY.with(() -> entity), new magnetAttractPack(entity.getClass(), entity.getUniqueID()));
                        }
                    }
                } catch (NoSuchFieldException | IllegalAccessException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }

    @Override
    public void addInformation(ItemStack stack, @Nullable World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn) {
        tooltip.add(new TranslationTextComponent("text.ifscience.horseshoe_magnet").mergeStyle(TextFormatting.RED));
    }

    @Override
    public int getUseDuration(ItemStack stack) {
        return 72000;
    }

    @Override
    public UseAction getUseAction(ItemStack stack) {
        return UseAction.BOW;
    }
}
