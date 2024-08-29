package com.ddhuan.ifscience.common.Entity;

import net.minecraft.block.BlockState;
import net.minecraft.entity.EntityType;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.IPacket;
import net.minecraft.world.World;

public class CutBlockEntity extends BlockEntity {
    public int count = 9999;

    public CutBlockEntity(EntityType<?> entityTypeIn, World worldIn) {
        super(entityTypeIn, worldIn);
    }

    public CutBlockEntity(World worldIn, double x, double y, double z, BlockState blockState) {
        super(entityTypeRegistry.CutBlockEntity.get(), worldIn, x, y, z, blockState);
    }

    @Override
    public void synchBlockState() {
        super.synchBlockState();
    }

    @Override
    public void tick() {
        super.tick();
    }

    @Override
    protected void writeAdditional(CompoundNBT compound) {
        super.writeAdditional(compound);
    }

    @Override
    protected void registerData() {
        super.registerData();
    }

    @Override
    protected void readAdditional(CompoundNBT compound) {
        super.readAdditional(compound);
    }

    @Override
    public IPacket<?> createSpawnPacket() {
        return super.createSpawnPacket();
    }
}
