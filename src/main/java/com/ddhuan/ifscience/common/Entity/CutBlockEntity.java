package com.ddhuan.ifscience.common.Entity;

import com.ddhuan.ifscience.Custom.DataSerializersRegistry;
import net.minecraft.block.BlockState;
import net.minecraft.entity.EntityType;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.IPacket;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.network.datasync.IDataSerializer;
import net.minecraft.world.World;

public class CutBlockEntity extends BlockEntity {
    public Direction direction;
    public static final DataParameter<Direction> directionData = EntityDataManager.createKey(CutBlockEntity.class, (IDataSerializer<Direction>) DataSerializersRegistry.CutBlockEntity_Direction.get().getSerializer());

    public CutBlockEntity(EntityType<?> entityTypeIn, World worldIn) {
        super(entityTypeIn, worldIn);
    }

    public CutBlockEntity(World worldIn, double x, double y, double z, BlockState blockState, net.minecraft.util.Direction direction) {
        super(entityTypeRegistry.CutBlockEntity.get(), worldIn, x, y, z, blockState);
        this.direction = Direction.valueOf(direction.name());
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
    }

    @Override
    protected void readAdditional(CompoundNBT compound) {
        super.readAdditional(compound);
        direction = Direction.get(compound.getByte("CutBlock&direction"));
        if (this.direction != null) {
            this.dataManager.set(directionData, this.direction);
        }
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
        /*public static double moveX(Direction direction){

        }*/
    }
}