package com.ddhuan.ifscience.common.Entity;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.item.TNTEntity;
import net.minecraft.network.IPacket;
import net.minecraft.world.Explosion;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.NetworkHooks;

import javax.annotation.Nullable;

public class furnaceTNTEntity extends TNTEntity {
    public furnaceTNTEntity(EntityType<? extends TNTEntity> type, World worldIn) {
        super(type, worldIn);
    }

    public furnaceTNTEntity(World worldIn, double x, double y, double z, @Nullable LivingEntity igniter) {
        super(worldIn, x, y, z, igniter);
    }

    public int fuse = 60;

    @Override
    protected void explode() {
        float f = 5.0F;
        this.world.createExplosion(this, this.getPosX(), this.getPosYHeight(0.0625D), this.getPosZ(), f, true, Explosion.Mode.BREAK);
    }

    @Override
    public IPacket<?> createSpawnPacket() {
        return NetworkHooks.getEntitySpawningPacket(this);
    }
}
