package com.ddhuan.ifscience.mixin;

import com.ddhuan.ifscience.common.customDamage;
import com.ddhuan.ifscience.network.Client.fireRenderPack;
import com.ddhuan.ifscience.network.Network;
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
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.fml.network.PacketDistributor;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(AbstractFurnaceTileEntity.class)
public abstract class AbstractFurnaceTileEntityMixin extends LockableTileEntity {
    @Shadow
    protected abstract boolean isBurning();

    @Inject(method = "tick", at = @At("HEAD"))
    public void tick(CallbackInfo ci) {
        if (this.world != null && !this.world.isRemote) {
            if (this.isBurning()) {
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
