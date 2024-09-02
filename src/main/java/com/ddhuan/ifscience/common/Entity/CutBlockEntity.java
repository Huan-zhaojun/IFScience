package com.ddhuan.ifscience.common.Entity;

import com.ddhuan.ifscience.Custom.DataSerializersRegistry;
import com.ddhuan.ifscience.common.Item.itemRegistry;
import net.minecraft.block.BlockState;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.IPacket;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.network.datasync.IDataSerializer;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.UUID;

public class CutBlockEntity extends BlockEntity {
    public Direction direction;
    public static final DataParameter<Direction> directionData = EntityDataManager.createKey(CutBlockEntity.class, (IDataSerializer<Direction>) DataSerializersRegistry.CutBlockEntity_Direction.get().getSerializer());

    @OnlyIn(Dist.CLIENT)
    public int animationTick = 0;//动画时间计数
    @OnlyIn(Dist.CLIENT)
    public long lastTime = 0;

    public static int maxLifeTick = 80;
    public int lifeTick = maxLifeTick;//生命时长

    public UUID playerUuid = null;
    public boolean angleGrinder = true;

    public CutBlockEntity(EntityType<?> entityTypeIn, World worldIn) {
        super(entityTypeIn, worldIn);
    }

    public CutBlockEntity(World worldIn, double x, double y, double z, BlockState blockState, net.minecraft.util.Direction direction, UUID playerUuid) {
        super(entityTypeRegistry.CutBlockEntity.get(), worldIn, x, y, z, blockState);
        this.direction = Direction.valueOf(direction.name());
        this.playerUuid = playerUuid;
    }

    @Override
    public void synchBlockState() {
        super.synchBlockState();
        if (!world.isRemote) this.dataManager.set(directionData, this.direction);
        if (world.isRemote) this.direction = this.dataManager.get(directionData);
    }

    @Override
    public void tick() {
        super.tick();
        if (!world.isRemote) {
            if (lifeTick <= 0) {
                this.entityDropItem(this.blockState.getBlock());
                this.remove();
            }
            PlayerEntity player = world.getPlayerByUuid(playerUuid);
            if (angleGrinder && lifeTick <= maxLifeTick - 40 && player != null) {
                player.addItemStackToInventory(new ItemStack(itemRegistry.angleGrinder.get()));
                angleGrinder = false;
            }
        }
        lifeTick--;
    }

    @Override
    protected void registerData() {
        super.registerData();
        this.dataManager.register(directionData, this.direction);
    }

    @Override
    protected void writeAdditional(CompoundNBT compound) {
        super.writeAdditional(compound);
        compound.putByte("CutBlock&direction", direction.index);
        compound.putUniqueId("CutBlock&playerUuid", playerUuid);
        compound.putBoolean("CutBlock&angleGrinder", angleGrinder);
    }

    @Override
    protected void readAdditional(CompoundNBT compound) {
        super.readAdditional(compound);
        direction = Direction.get(compound.getByte("CutBlock&direction"));
        if (this.direction != null) {
            this.dataManager.set(directionData, this.direction);
        }
        playerUuid = compound.getUniqueId("CutBlock&playerUuid");
        angleGrinder = compound.getBoolean("CutBlock&angleGrinder");
    }


    @Override
    public IPacket<?> createSpawnPacket() {
        return super.createSpawnPacket();
    }

    public enum Direction {
        NORTH((byte) 1, +0.0625 * 2.0D, -0.25D/*+1.0D*/, -90f),
        SOUTH((byte) 2, -0.0625 * 2.0D, +0.25D/*-1.0D*/, 90f),
        EAST((byte) 3, +0.25D/*-1.0D*/, +0.0625 * 2.0D, 0f),
        WEST((byte) 4, -0.25D/*+1.0D*/, -0.0625 * 2.0D, -180f);

        public final byte index;//序号
        public final double translateX;
        public final double translateZ;
        public final float degreesYN;

        Direction(byte index, double translateX, double translateZ, float degreesYN) {
            this.index = index;
            this.translateX = translateX;
            this.translateZ = translateZ;
            this.degreesYN = degreesYN;
        }

        public static Direction get(byte index) {
            for (Direction direction : Direction.values()) {
                if (direction.index == index) {
                    return direction;
                }
            }
            return null;
        }

        //在x轴方向的移动
        public double moveX(int tick, int maxTick) {
            if (this.equals(Direction.NORTH) || this.equals(Direction.SOUTH)) return 0;
            else if (this.equals(Direction.WEST))
                return tick > maxTick ? 1 : ((double) tick / maxTick);
            else if (this.equals(Direction.EAST))
                return tick > maxTick ? -1 : -((double) tick / maxTick);
            else return 0;
        }

        //在z轴方向的移动
        public double moveZ(int tick, int maxTick) {
            if (this.equals(Direction.WEST) || this.equals(Direction.EAST)) return 0;
            else if (this.equals(Direction.NORTH))
                return tick > maxTick ? 1 : ((double) tick / maxTick);
            else if (this.equals(Direction.SOUTH))
                return tick > maxTick ? -1 : -((double) tick / maxTick);
            else return 0;
        }
    }
}