package com.ddhuan.ifscience.common.Entity;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.network.IPacket;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;
import net.minecraftforge.fml.network.NetworkHooks;

import java.util.Optional;

public class BlockEntity extends Entity {
    public BlockState blockState = Blocks.AIR.getDefaultState();
    public static final DataParameter<Optional<BlockState>> blockStateData = EntityDataManager.createKey(BlockEntity.class, DataSerializers.OPTIONAL_BLOCK_STATE);
    public CompoundNBT tileEntityNbt = new CompoundNBT();
    public static final DataParameter<CompoundNBT> tileEntityNbtData = EntityDataManager.createKey(BlockEntity.class, DataSerializers.COMPOUND_NBT);
    public TileEntity tileEntity = null;

    //public int count = 40;

    public BlockEntity(EntityType<?> entityTypeIn, World worldIn) {
        super(entityTypeIn, worldIn);
    }

    public BlockEntity(World worldIn, double x, double y, double z, BlockState blockState) {
        this(entityTypeRegistry.blockEntity.get(), worldIn);
        this.setPosition(x, y, z);
        this.prevPosX = x;
        this.prevPosY = y;
        this.prevPosZ = z;
        this.blockState = blockState;
    }

    public BlockEntity(EntityType<? extends BlockEntity> entityTypeIn, World worldIn, double x, double y, double z, BlockState blockState, CompoundNBT tileEntityNbt) {
        this(entityTypeIn, worldIn);
        this.setPosition(x, y, z);
        this.prevPosX = x;
        this.prevPosY = y;
        this.prevPosZ = z;
        this.blockState = blockState;
        this.tileEntityNbt = tileEntityNbt;
        this.setTileEntity();//方块实体数据设置，防止渲染为空
    }

    @Override
    protected void registerData() {
        this.dataManager.register(blockStateData, Optional.ofNullable(blockState));
        this.dataManager.register(tileEntityNbtData, new CompoundNBT());
    }

    @Override
    protected void readAdditional(CompoundNBT compound) {
        blockState = NBTUtil.readBlockState((CompoundNBT) compound.get("blockState"));
        this.dataManager.set(blockStateData, Optional.of(blockState));//同步数据到客户端

        tileEntityNbt = compound.getCompound("tileEntityNbt");
        this.dataManager.set(tileEntityNbtData, tileEntityNbt);
        setTileEntity();//方块实体数据设置，防止渲染为空
    }

    @Override
    protected void writeAdditional(CompoundNBT compound) {
        compound.put("blockState", NBTUtil.writeBlockState(blockState));
        compound.put("tileEntityNbt", tileEntityNbt);
    }

    public void synchBlockState() {//用于初始化和同步数据到客户端
        if (!world.isRemote) {
            this.dataManager.set(blockStateData, Optional.of(blockState));//同步数据到客户端
            this.dataManager.set(tileEntityNbtData, tileEntityNbt);
        }
        if (/*blockState.equals(Blocks.AIR.getDefaultState()) &&*/ world.isRemote) {
            if (this.dataManager.get(blockStateData).isPresent())
                blockState = this.dataManager.get(blockStateData).get();
            tileEntityNbt = this.dataManager.get(tileEntityNbtData);
            setTileEntity();//方块实体数据设置，防止渲染为空
        }
    }

    public void setTileEntity() {//方块实体数据设置，防止渲染为空
        if (tileEntityNbt.isEmpty()) return;
        this.tileEntity = TileEntity.readTileEntity(blockState, tileEntityNbt);
        if (this.tileEntity != null) {
            this.tileEntity.setWorldAndPos(world, getPosition());
            try {
                ObfuscationReflectionHelper.findField(TileEntity.class, "field_195045_e"/*只能使用SRG名*/).set(tileEntity, blockState);
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        }
    }

    @Override
    public void tick() {
        super.tick();
        this.synchBlockState();//初始化同步数据
        /*if (!this.hasNoGravity()) {
            this.setMotion(this.getMotion().add(0.0D, -0.04D, 0.0D));
        }

        this.move(MoverType.SELF, this.getMotion());
        this.setMotion(this.getMotion().scale(0.98D));*/
        /*if (this.onGround) {
            count--;
            this.setMotion(0, 0, 0);
            if (count < 0) this.remove();
        }*/
    }

    @Override
    public IPacket<?> createSpawnPacket() {
        return NetworkHooks.getEntitySpawningPacket(this);
    }
}
