package com.ddhuan.ifscience.common.Entity;

import com.ddhuan.ifscience.Custom.magnetUtil;
import com.ddhuan.ifscience.common.customDamage;
import com.ddhuan.ifscience.network.Client.entityMotionPack;
import com.ddhuan.ifscience.network.Client.playSoundPack;
import com.ddhuan.ifscience.network.Network;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.*;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.IPacket;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.NetworkHooks;
import net.minecraftforge.fml.network.PacketDistributor;

import java.util.UUID;

public class MagnetAttractedBlockEntity extends BlockEntity {
    public boolean isAttracted = false;//是否正在被磁吸
    private PlayerEntity magnetAttractor;//磁吸者
    private Vector3d magnetAttractor_lastPos;//磁吸者上一个位置
    public double G = 0.04D;//重力加速度
    public int noAttractedTick = 0;//不被磁吸的时刻
    public int collisionFlag = magnetUtil.collisionFlag;

    public MagnetAttractedBlockEntity(EntityType<?> entityTypeIn, World worldIn) {
        super(entityTypeIn, worldIn);
    }

    public MagnetAttractedBlockEntity(World worldIn, double x, double y, double z, BlockState blockState, UUID magnetAttractor_uuid) {
        super(entityTypeRegistry.MagnetAttractedBlockEntity.get(),worldIn, x, y, z, blockState);
        isAttracted = true;
        this.magnetAttractor = worldIn.getPlayerByUuid(magnetAttractor_uuid);
        if (magnetAttractor != null) {
            this.magnetAttractor_lastPos = magnetAttractor.getPositionVec();
        }
        count = 999999;
    }

    @Override
    protected void registerData() {
        super.registerData();
    }

    @Override
    protected void readAdditional(CompoundNBT compound) {
        magnetAttractor = world.getPlayerByUuid(compound.getUniqueId("magnetAttractor"));
        G = compound.getDouble("G");
        super.readAdditional(compound);
    }

    @Override
    protected void writeAdditional(CompoundNBT compound) {
        compound.putUniqueId("magnetAttractor", magnetAttractor.getUniqueID());
        compound.putDouble("G", G);
        super.writeAdditional(compound);
    }


    @Override
    public void tick() {
        if (!this.hasNoGravity()) {
            this.setMotion(this.getMotion().add(0.0D, -G, 0.0D));
        }

        if (isAttracted) {//处在被磁吸者磁吸时
            if (magnetAttractor != null && Math.pow(magnetAttractor.getPosX() - getPosX(), 2) + Math.pow(magnetAttractor.getPosY() - getPosY(), 2) + Math.pow(magnetAttractor.getPosZ() - getPosZ(), 2) > Math.pow(magnetUtil.radius, 2)) {
                isAttracted = false;//超出磁铁作用半径
            } else if (noAttractedTick <= 0 && magnetAttractor != null && magnetAttractor_lastPos.subtract(magnetAttractor.getPositionVec()).length() >= 0.1) {
                magnetAttractor_lastPos = magnetAttractor.getPositionVec();//变更方向
                Vector3d positionVec = magnetAttractor.getPositionVec().add(0, 0.5, 0).subtract(getPosX(), getPosY(), getPosZ());
                Vector3d speedVec = positionVec.normalize().scale(magnetUtil.speed);
                double speedY = 0.5 * G * Math.sqrt((Math.pow(positionVec.x, 2) + Math.pow(positionVec.z, 2)) / ((Math.pow(speedVec.x, 2) + Math.pow(speedVec.z, 2))));
                this.setMotion(speedVec.add(0, speedY, 0));
            }
        } else {//当前没有被磁铁吸引
            this.setMotion(getMotion().x * 0.98, getMotion().y * 0.95, getMotion().z * 0.98);
        }
        if (noAttractedTick > 0) noAttractedTick--;

        this.move(MoverType.SELF, this.getMotion());

        if (!world.isRemote) {//方块实体被磁吸，对附近的实体造成伤害，自身变成掉落物
            Vector3d positionVec = this.getPositionVec();
            double x = positionVec.getX(), y = positionVec.getY(), z = positionVec.getZ();
            LivingEntity livingEntity = world.getClosestEntityWithinAABB(
                    LivingEntity.class,
                    new EntityPredicate().setDistance(0.5),
                    null,
                    x, y, z,
                    new AxisAlignedBB(x - 0.5, y - 0.5, z - 0.5, x + 0.5, y + 0.5, z + 0.5));
            if (livingEntity != null) {
                //物体碰撞反向速度
                EntitySize size = livingEntity.getType().getSize();
                //撞小物体互相反弹
                if (collisionFlag != 2 && (collisionFlag == 1 || size.width * size.width * size.height < 1.0)) {
                    noAttractedTick = 20;
                    isAttracted = false;
                    livingEntity.setMotion(livingEntity.getMotion().add(getMotion().x * 2, Math.max(Math.abs(getMotion().y), 0.5), getMotion().z * 2));
                    setMotion(getMotion().x * -0.5, Math.max(Math.abs(getMotion().y), 0.5), getMotion().z * -0.5);
                    Network.INSTANCE.send(PacketDistributor.TRACKING_ENTITY_AND_SELF.with(() -> livingEntity), new entityMotionPack(livingEntity.getUniqueID(), livingEntity.getMotion()));
                }
                livingEntity.attackEntityFrom(customDamage.StoneAttractMagnet, 8);
                //声音
                Network.INSTANCE.send(PacketDistributor.ALL.noArg(),
                        new playSoundPack(getPosX(), getPosY(), getPosZ(),
                                "block.anvil.land", "BLOCKS",
                                1F, this.world.rand.nextFloat() * 0.1F + 0.9F,
                                false));
                if (collisionFlag != 1 && (collisionFlag == 2 || size.width * size.width * size.height >= 1.0)) {//撞大物体自身变成掉落物
                    this.entityDropItem(blockState.getBlock());
                    this.remove();
                }
            }
        }
        count--;
        if (count <= 0) this.remove();

        if (!isAttracted && onGround) {//掉地上
            this.setMotion(Vector3d.ZERO);
            if (!world.isRemote) {
                BlockPos[] directions = {getPosition(), getPosition().east(), getPosition().south(), getPosition().west(), getPosition().north()};
                boolean blockPlaced = false;
                for (BlockPos pos : directions) {
                    if (world.getBlockState(pos).getBlock().equals(Blocks.AIR)) {
                        world.setBlockState(pos, blockState);
                        //声音
                        Network.INSTANCE.send(PacketDistributor.ALL.noArg(),
                                new playSoundPack(getPosX(), getPosY(), getPosZ(),
                                        "block.anvil.land", "BLOCKS",
                                        1F, this.world.rand.nextFloat() * 0.1F + 0.9F,
                                        false));
                        blockPlaced = true;
                        break;  // 找到一个位置后立即退出循环
                    }
                }
                if (!blockPlaced) {
                    this.entityDropItem(blockState.getBlock());
                }
                this.remove();
            }
        }


        if (!world.isRemote) {
            this.setFlag(6, this.isGlowing());
        }
        this.baseTick();
    }

    @Override
    public IPacket<?> createSpawnPacket() {
        return NetworkHooks.getEntitySpawningPacket(this);
    }

    public void setMagnetAttractor(UUID uuid) {
        magnetAttractor = world.getPlayerByUuid(uuid);
        isAttracted = true;
        if (magnetAttractor_lastPos == null && magnetAttractor != null) {
            magnetAttractor_lastPos = magnetAttractor.getPositionVec();
        }
    }

    public UUID getMagnetAttractor() {
        return magnetAttractor.getUniqueID();
    }
}
