package com.ddhuan.ifscience.common.Entity;

import net.minecraft.block.Blocks;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.item.TNTEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.IPacket;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.NetworkHooks;

import javax.annotation.Nullable;

public class touchdownTNTEntity extends TNTEntity {
    private static final DataParameter<Integer> CanExplodeFuse = EntityDataManager.createKey(touchdownTNTEntity.class, DataSerializers.VARINT);
    public int canExplodeFuse;

    public touchdownTNTEntity(EntityType<? extends TNTEntity> type, World worldIn) {
        super(type, worldIn);
    }

    public touchdownTNTEntity(World worldIn, double x, double y, double z, @Nullable LivingEntity igniter) {
        this(entityTypeRegistry.touchdownTNT.get(), worldIn);
        this.setPosition(x, y, z);
        this.prevPosX = x;
        this.prevPosY = y;
        this.prevPosZ = z;
    }

    @Override
    protected void readAdditional(CompoundNBT compound) {
        super.readAdditional(compound);
        this.dataManager.set(CanExplodeFuse, compound.getInt("CanExplodeFuse"));
    }

    @Override
    protected void writeAdditional(CompoundNBT compound) {
        super.writeAdditional(compound);
        compound.putInt("CanExplodeFuse", this.dataManager.get(CanExplodeFuse));
    }

    @Override
    protected void registerData() {
        super.registerData();
        this.dataManager.register(CanExplodeFuse, 0);
    }

    @Override
    public void tick() {
        if (!world.isRemote) {
            if (canExplodeFuse > 20 && (!Blocks.AIR.getDefaultState().equals(world.getBlockState(getPosition().add(0, -1, 0))) ||
                    !Blocks.AIR.getDefaultState().equals(world.getBlockState(getPosition().add(1, -1, 0))) ||
                    !Blocks.AIR.getDefaultState().equals(world.getBlockState(getPosition().add(1, -1, 1))) ||
                    !Blocks.AIR.getDefaultState().equals(world.getBlockState(getPosition().add(0, -1, 1))) ||
                    !Blocks.AIR.getDefaultState().equals(world.getBlockState(getPosition().add(0, -1, -1))) ||
                    !Blocks.AIR.getDefaultState().equals(world.getBlockState(getPosition().add(-1, -1, -1))) ||
                    !Blocks.AIR.getDefaultState().equals(world.getBlockState(getPosition().add(-1, -1, 0))) ||
                    !Blocks.AIR.getDefaultState().equals(world.getBlockState(getPosition().add(-1, -1, 1))) ||
                    !Blocks.AIR.getDefaultState().equals(world.getBlockState(getPosition().add(1, -1, -1))))) {
                setFuse(0);
            }
            canExplodeFuse++;
        }
        super.tick();
    }

    @Override
    public IPacket<?> createSpawnPacket() {
        return NetworkHooks.getEntitySpawningPacket(this);
    }
}
