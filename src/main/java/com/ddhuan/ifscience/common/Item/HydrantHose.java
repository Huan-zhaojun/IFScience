package com.ddhuan.ifscience.common.Item;

import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUseContext;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.util.*;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;

public class HydrantHose extends Item {
    long lastTime = System.currentTimeMillis();
    boolean isSound = true;

    public HydrantHose(Properties properties) {
        super(properties);
    }

    @Override
    public ActionResultType onItemUse(ItemUseContext context) {

        return super.onItemUse(context);
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(World worldIn, PlayerEntity playerIn, Hand handIn) {
        long delayTime = 25;//喷水效果间隔时间
        double distance = 50.0;//喷水距离
        double radius = 1;//灭火半径

        double x, y, z;
        Vector3d vector3d;
        for (int i = 1; i <= distance; i++) {//方块判定
            vector3d = playerIn.getPositionVec().add(playerIn.getLookVec().scale(i));
            x = vector3d.x;
            y = vector3d.y + playerIn.getEyeHeight() * 0.8D;
            z = vector3d.z;
            if (!worldIn.getBlockState(new BlockPos(x, y, z)).getBlock().equals(Blocks.AIR)
                    && !worldIn.getBlockState(new BlockPos(x, y, z)).getBlock().equals(Blocks.FIRE)) {//碰到方块！
                //球形半径灭火范围
                for (double j = x - radius; j <= x + radius; j += 1.0) {
                    for (double k = y - radius; k <= y + radius; k += 1.0) {
                        for (double l = z - radius; l <= z + radius; l += 1.0) {
                            if (Blocks.FIRE.equals(worldIn.getBlockState(new BlockPos(j, k, l)).getBlock())) {
                                worldIn.setBlockState(new BlockPos(j, k, l), Blocks.AIR.getDefaultState());
                                for (int m = 0; m < 10; m++) {
                                    worldIn.addParticle(ParticleTypes.POOF, x + Math.random(), y + Math.random() * 0.5, z + Math.random(), 0, 0.3, 0);
                                }
                                if (isSound) {
                                    worldIn.playSound(playerIn, new BlockPos(x, y, z), SoundEvents.BLOCK_FIRE_EXTINGUISH, SoundCategory.BLOCKS, 10f, 1f);
                                    isSound = false;
                                }
                            }
                        }
                    }
                }
                worldIn.playSound(playerIn, new BlockPos(x, y, z), SoundEvents.ENTITY_PLAYER_SPLASH_HIGH_SPEED, SoundCategory.AMBIENT, 10f, 5f);//玩家高速入水溅射声
                isSound = false;
                distance = i;
                break;
            } else if (Blocks.FIRE.equals(worldIn.getBlockState(new BlockPos(x, y, z)).getBlock())) {//灭火
                worldIn.setBlockState(new BlockPos(x, y, z), Blocks.AIR.getBlock().getDefaultState());
                for (int j = 0; j < 10; j++) {
                    worldIn.addParticle(ParticleTypes.POOF, x + Math.random(), y + Math.random() * 0.5, z + Math.random(), 0, 0.3, 0);
                }
                //球形半径灭火范围
                for (double j = x - radius; j <= x + radius; j += 1.0) {
                    for (double k = y - radius; k <= y + radius; k += 1.0) {
                        for (double l = z - radius; l <= z + radius; l += 1.0) {

                            if (Blocks.FIRE.equals(worldIn.getBlockState(new BlockPos(j, k, l)).getBlock())) {
                                worldIn.setBlockState(new BlockPos(j, k, l), Blocks.AIR.getDefaultState());
                                for (int m = 0; m < 10; m++) {
                                    worldIn.addParticle(ParticleTypes.POOF, x + Math.random(), y + Math.random() * 0.5, z + Math.random(), 0, 0.3, 0);
                                }
                            }
                        }
                    }
                }
                worldIn.playSound(playerIn, new BlockPos(x, y, z), SoundEvents.BLOCK_FIRE_EXTINGUISH, SoundCategory.BLOCKS, 10f, 1f);
                isSound = false;
                distance = i;
                break;
            }
            //对实体
            for (Entity entity : worldIn.getEntitiesWithinAABB(Entity.class, new AxisAlignedBB(new BlockPos(x - 1, y - 1, z - 1), new BlockPos(x + 1, y + 1, z + 1))
                    , (entity -> !(entity instanceof PlayerEntity)))) {
                entity.attackEntityFrom(DamageSource.GENERIC, 3);
                double knockBackX = playerIn.getLookVec().scale(15).x;
                double knockBackY = 3;
                double knockBackZ = playerIn.getLookVec().scale(15).z;
                double entityX = entity.getPosX();
                double entityY = entity.getPosY();
                double entityZ = entity.getPosZ();
                while (knockBackX > 1) {
                    entityX = entityX + knockBackX;
                    entityY = entityY + knockBackY;
                    entityZ = entityZ + knockBackZ;
                    entity.setPosition(entityX, entityY, entityZ);
                    knockBackX *= 0.8;
                    knockBackY *= 0.8;
                    knockBackZ *= 0.8;
                }
            }
        }
        for (double i = 0.1; i <= distance; i += 0.1) {//喷水的粒子绘制
            vector3d = playerIn.getPositionVec().add(playerIn.getLookVec().scale(i));
            x = vector3d.x;
            y = vector3d.y + playerIn.getEyeHeight() * 0.8D;
            z = vector3d.z;
            if (worldIn.isRemote) {
                worldIn.addParticle(ParticleTypes.RAIN, true, x, y, z, 0, 0, 0);
                worldIn.addParticle(ParticleTypes.RAIN, true, x, y, z, 0, 0, 0);
                worldIn.addParticle(ParticleTypes.RAIN, true, x, y, z, 0, 0, 0);
            }
        }
        if (isSound) {//正常的水管喷水声
            worldIn.playSound(playerIn, playerIn.getPosition(), SoundEvents.ENTITY_FISHING_BOBBER_SPLASH, SoundCategory.AMBIENT, 1f, 3f);
        }
        isSound = true;
        lastTime = System.currentTimeMillis();
        return super.onItemRightClick(worldIn, playerIn, handIn);
    }
}
