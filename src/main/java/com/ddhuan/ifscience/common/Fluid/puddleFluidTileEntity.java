package com.ddhuan.ifscience.common.Fluid;

import com.ddhuan.ifscience.common.TileEntity.TileEntityTypeRegistry;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.client.Minecraft;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.TileEntity;

import java.util.Random;

public class puddleFluidTileEntity extends TileEntity implements ITickableTileEntity {
    private int tick = 0;
    private final int tickRandom = new Random().nextInt(201) + 60;

    public puddleFluidTileEntity() {
        super(TileEntityTypeRegistry.puddleFluidTileEntity.get());
    }

    public void increase() {
        tick++;
        markDirty();
    }

    @Override
    public void read(BlockState state, CompoundNBT nbt) {
        tick = nbt.getInt("puddleTick");
        super.read(state, nbt);
    }

    @Override
    public CompoundNBT write(CompoundNBT compound) {
        compound.putInt("puddleTick", tick);
        return super.write(compound);
    }

    @Override
    public void tick() {
        if (world != null && !world.isRemote) {
            if (!world.isRaining()) {
                world.setBlockState(pos, Blocks.AIR.getDefaultState());
            } else if (tick >= tickRandom) {
                world.setBlockState(pos, Blocks.AIR.getDefaultState());
                if (Minecraft.getInstance().world != null) {
                    Minecraft.getInstance().world.addParticle(ParticleTypes.CLOUD, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, 0, 0.05, 0);
                    Minecraft.getInstance().world.addParticle(ParticleTypes.CLOUD, pos.getX() + 0.5, pos.getY() + 0.6, pos.getZ() + 0.5, 0, 0.05, 0);
                    Minecraft.getInstance().world.addParticle(ParticleTypes.CLOUD, pos.getX() + 0.5, pos.getY() + 0.4, pos.getZ() + 0.5, 0, 0.05, 0);
                    Minecraft.getInstance().world.addParticle(ParticleTypes.CLOUD, pos.getX() + 0.5, pos.getY() + 0.55, pos.getZ() + 0.5, 0, 0.05, 0);
                }
            }
        }
        increase();
    }
}
