package com.ddhuan.ifscience.mixin;

import com.ddhuan.ifscience.common.Entity.furnaceTNTEntity;
import com.ddhuan.ifscience.common.customDamage;
import com.ddhuan.ifscience.network.Client.cloudParticlePack;
import com.ddhuan.ifscience.network.Client.fireRenderPack;
import com.ddhuan.ifscience.network.Client.furnaceTNTRenderPack;
import com.ddhuan.ifscience.network.Network;
import com.google.common.collect.ArrayListMultimap;
import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.container.Container;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.AbstractFurnaceTileEntity;
import net.minecraft.tileentity.LockableTileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.fml.network.PacketDistributor;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

import static com.ddhuan.ifscience.otherMod.urineFluid;

@Mixin(AbstractFurnaceTileEntity.class)
public abstract class AbstractFurnaceTileEntityMixin extends LockableTileEntity {
    @Shadow
    protected abstract boolean isBurning();

    @Inject(method = "tick", at = @At("HEAD"))
    public void tick(CallbackInfo ci) {
        if (this.world != null && !this.world.isRemote) {
            if (this.isBurning()) {
                //熔炉燃烧时遇到水和尿液会爆炸
                BlockPos.getAllInBox(new AxisAlignedBB(this.pos).grow(0.5)).forEach(blockPos -> {
                    if (world != null && (Blocks.WATER.equals(world.getBlockState(blockPos).getBlock()) ||
                            (urineFluid != null && urineFluid.equals(world.getBlockState(blockPos).getBlock())))) {
                        //清除附近液体
                        BlockPos.getAllInBox(new AxisAlignedBB(this.pos).grow(5)).forEach(blockPos2 -> {
                            if (Blocks.WATER.equals(world.getBlockState(blockPos2).getBlock()) ||
                                    (urineFluid != null && urineFluid.equals(world.getBlockState(blockPos2).getBlock())))
                                world.setBlockState(blockPos2, Blocks.AIR.getDefaultState());
                        });

                        furnaceTNTEntity furnace = new furnaceTNTEntity(world, (double) pos.getX() + 0.5, (double) pos.getY(), (double) pos.getZ() + 0.5, world.getBlockState(pos));
                        furnace.setFuse(30);
                        world.addEntity(furnace);
                        Network.INSTANCE.send(PacketDistributor.ALL.noArg(), new furnaceTNTRenderPack(pos));
                        world.playSound(null, furnace.getPosX(), furnace.getPosY(), furnace.getPosZ(), SoundEvents.ENTITY_TNT_PRIMED, SoundCategory.BLOCKS, 1.0F, 1.0F);
                        world.setBlockState(this.pos, Blocks.AIR.getDefaultState(), 11);//清除熔炉方块
                    }
                });

                //烧伤生物
                List<Entity> entityList = this.world.getEntitiesInAABBexcluding(null, new AxisAlignedBB(this.pos).grow(0.25), null);
                for (Entity entity : entityList) {
                    if (entity instanceof LivingEntity) {//是活动的生物
                        LivingEntity entity1 = (LivingEntity) entity;
                        entity1.attackEntityFrom(customDamage.FurnaceBurn, entity1.getMaxHealth() / 5.0f);
                        if (entity1 instanceof PlayerEntity) {
                            ServerPlayerEntity player = (ServerPlayerEntity) entity1;
                            if (!player.isCreative()) {
                                Network.INSTANCE.send(PacketDistributor.PLAYER.with(() -> player), new fireRenderPack(player.getUniqueID()));
                            }
                        } else
                            Network.INSTANCE.send(PacketDistributor.TRACKING_ENTITY_AND_SELF.with(() -> entity1), new fireRenderPack(entity1.getUniqueID()));
                    }
                }
            }
        }
    }

    @Inject(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/tileentity/AbstractFurnaceTileEntity;getBurnTime(Lnet/minecraft/item/ItemStack;)I"))
    public void tick2(CallbackInfo ci) {
        //当熔炉点燃时蒸发附近的水
        BlockPos.getAllInBox(new AxisAlignedBB(this.pos).grow(2)).forEach(blockPos -> {
            if (world != null && Blocks.WATER.equals(world.getBlockState(blockPos).getBlock())) {
                world.setBlockState(blockPos, Blocks.AIR.getDefaultState());

                //粒子渲染
                ArrayListMultimap<Vector3d, Double[]> cloudParticles = ArrayListMultimap.create();
                for (int i = 0; i < 5; i++) {
                    cloudParticles.put(new Vector3d(pos.getX() + 0.5 + 0.25 * Math.random(),
                            pos.getY() + 0.5 + 0.15 * Math.random(),
                            pos.getZ() + 0.5 + 0.25 * Math.random()), new Double[]{0.0, 0.1, 0.0});
                }
                Network.INSTANCE.send(PacketDistributor.ALL.noArg(), new cloudParticlePack(cloudParticles));
            }
        });
    }

    @Unique
    public boolean isBurning_Mixin() {
        return isBurning();
    }

    protected AbstractFurnaceTileEntityMixin(TileEntityType<?> typeIn) {
        super(typeIn);
    }

    protected abstract ITextComponent getDefaultName();

    protected abstract Container createMenu(int id, PlayerInventory player);

    @Shadow
    public abstract int getSizeInventory();

    @Shadow
    public abstract boolean isEmpty();

    @Shadow
    public abstract ItemStack getStackInSlot(int index);

    @Shadow
    public abstract ItemStack decrStackSize(int index, int count);

    @Shadow
    public abstract ItemStack removeStackFromSlot(int index);

    @Shadow
    public abstract void setInventorySlotContents(int index, ItemStack stack);

    @Shadow
    public abstract boolean isUsableByPlayer(PlayerEntity player);

    @Shadow
    public abstract void clear();
}
