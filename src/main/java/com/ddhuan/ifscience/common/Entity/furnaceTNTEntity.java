package com.ddhuan.ifscience.common.Entity;

import com.ddhuan.ifscience.common.customDamage;
import com.ddhuan.ifscience.network.Client.cloudParticlePack;
import com.ddhuan.ifscience.network.Network;
import com.google.common.collect.ArrayListMultimap;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.item.TNTEntity;
import net.minecraft.network.IPacket;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.Explosion;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.NetworkHooks;
import net.minecraftforge.fml.network.PacketDistributor;

import javax.annotation.Nullable;

public class furnaceTNTEntity extends TNTEntity {
    public BlockState furnace = Blocks.FURNACE.getDefaultState();

    public furnaceTNTEntity(EntityType<? extends TNTEntity> type, World worldIn) {
        super(type, worldIn);
    }

    public furnaceTNTEntity(World worldIn, double x, double y, double z, @Nullable LivingEntity igniter) {
        super(worldIn, x, y, z, igniter);
    }

    public furnaceTNTEntity(World worldIn, double x, double y, double z, BlockState furnace) {
        this(entityTypeRegistry.furnaceTNT.get(), worldIn);
        this.setPosition(x, y, z);
        double d0 = worldIn.rand.nextDouble() * (double) ((float) Math.PI * 2F);
        this.setMotion(-Math.sin(d0) * 0.02D, (double) 0.2F, -Math.cos(d0) * 0.02D);
        this.prevPosX = x;
        this.prevPosY = y;
        this.prevPosZ = z;
        this.furnace = furnace;
    }

    @Override
    public void explode() {
        float f = 5.0F;
        DamageSource damageSource = null;
        if (furnace.getBlock().equals(Blocks.FURNACE)) damageSource = customDamage.FurnaceExplosion;
        else if (furnace.getBlock().equals(Blocks.BLAST_FURNACE)) damageSource = customDamage.BlastFurnaceExplosion;
        else if (furnace.getBlock().equals(Blocks.SMOKER)) damageSource = customDamage.SmokerExplosion;
        this.world.createExplosion(this, damageSource, null, this.getPosX(), this.getPosYHeight(0.0625D), this.getPosZ(), f, true, Explosion.Mode.BREAK);
    }

    @Override
    public void tick() {
        //粒子渲染
        ArrayListMultimap<Vector3d, Double[]> cloudParticles = ArrayListMultimap.create();
        cloudParticles.put(getPositionVec().add(0, 1.2, 0), new Double[]{0.0, 0.1, 0.0});
        cloudParticles.put(getPositionVec().add(0, 1.2, 0), new Double[]{0.0, 0.1, 0.0});
        Network.INSTANCE.send(PacketDistributor.ALL.noArg(), new cloudParticlePack(cloudParticles));
        super.tick();
    }

    @Override
    public IPacket<?> createSpawnPacket() {
        return NetworkHooks.getEntitySpawningPacket(this);
    }
}
