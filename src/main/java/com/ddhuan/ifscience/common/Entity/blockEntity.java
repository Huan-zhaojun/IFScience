package com.ddhuan.ifscience.common.Entity;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.MoverType;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.network.IPacket;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.NetworkHooks;

public class blockEntity extends Entity {
    public BlockState blockState = Blocks.AIR.getDefaultState();
    public int count = 40;

    public blockEntity(EntityType<?> entityTypeIn, World worldIn) {
        super(entityTypeIn, worldIn);
    }

    public blockEntity(World worldIn, double x, double y, double z, BlockState blockState) {
        this(entityTypeRegistry.blockEntity.get(), worldIn);
        this.setPosition(x, y, z);
        this.prevPosX = x;
        this.prevPosY = y;
        this.prevPosZ = z;
        this.blockState = blockState;
    }

    @Override
    protected void registerData() {

    }

    @Override
    protected void readAdditional(CompoundNBT compound) {
        blockState = NBTUtil.readBlockState((CompoundNBT) compound.get("blockState"));
    }

    @Override
    protected void writeAdditional(CompoundNBT compound) {
        compound.put("blockState", NBTUtil.writeBlockState(blockState));

    }

    @Override
    public void tick() {
        if (!this.hasNoGravity()) {
            this.setMotion(this.getMotion().add(0.0D, -0.04D, 0.0D));
        }

        this.move(MoverType.SELF, this.getMotion());
        this.setMotion(this.getMotion().scale(0.98D));
        if (this.onGround) {
            count--;
            this.setMotion(0, 0, 0);
            if (count < 0) this.remove();
        }
        super.tick();
    }

    @Override
    public IPacket<?> createSpawnPacket() {
        return NetworkHooks.getEntitySpawningPacket(this);
    }
}
