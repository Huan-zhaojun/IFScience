package com.ddhuan.ifscience.mixin.entity;

import com.ddhuan.ifscience.Custom.magnetUtil;
import com.ddhuan.ifscience.common.customDamage;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.loot.LootContext;
import net.minecraft.loot.LootTable;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.DamageSource;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.util.Random;
import java.util.UUID;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin extends Entity {
    @Unique
    public boolean canAttract = false;//可以被磁吸
    @Unique
    public boolean isAttracted = false;//是否正在被磁吸
    @Unique
    public PlayerEntity magnetAttractor = null;//磁吸者

    public LivingEntityMixin(EntityType<?> entityTypeIn, World worldIn) {
        super(entityTypeIn, worldIn);
    }

    @Inject(method = "dropLoot", at = @At(value = "INVOKE", target = "Lnet/minecraft/loot/LootTable;generate(Lnet/minecraft/loot/LootContext;)Ljava/util/List;"), cancellable = true, locals = LocalCapture.CAPTURE_FAILHARD)
    protected void dropLoot(DamageSource damageSourceIn, boolean attackedRecently, CallbackInfo ci, ResourceLocation resourcelocation, LootTable loottable, LootContext.Builder lootcontext$builder, LootContext ctx) {
        if (customDamage.FurnaceBurn.equals(damageSourceIn)) {
            for (ItemStack itemStack : loottable.generate(ctx)) {
                Item item = itemStack.getItem();
                int count = itemStack.getCount();
                if (Items.BEEF.equals(item)) {
                    entityDropItem(new ItemStack(Items.COOKED_BEEF, count));
                } else if (Items.CHICKEN.equals(item)) {
                    entityDropItem(new ItemStack(Items.COOKED_CHICKEN, count));
                } else if (Items.MUTTON.equals(item)) {
                    entityDropItem(new ItemStack(Items.COOKED_MUTTON, count));
                } else if (Items.PORKCHOP.equals(item)) {
                    entityDropItem(new ItemStack(Items.COOKED_PORKCHOP, count));
                } else if (Items.RABBIT.equals(item)) {
                    entityDropItem(new ItemStack(Items.COOKED_RABBIT, count));
                } else if (Items.COD.equals(item)) {
                    entityDropItem(new ItemStack(Items.COOKED_COD, count));
                } else if (Items.SALMON.equals(item)) {
                    entityDropItem(new ItemStack(Items.COOKED_SALMON, count));
                } else {
                    entityDropItem(itemStack);
                }
            }
            ci.cancel();
        }
    }

    @Inject(method = "travel", at = @At(value = "HEAD"), cancellable = true)
    public void travel(Vector3d travelVector, CallbackInfo ci) throws NoSuchFieldException, IllegalAccessException {
        //生物被磁吸时候移动
        magnetUtil.LivingEntity_MagnetAttractMove((LivingEntity) (Entity) this, ci);
    }


    @Inject(method = "dropLoot", at = @At(value = "TAIL"))
    protected void dropLoot(DamageSource damageSourceIn, boolean attackedRecently, CallbackInfo ci){
        if (canAttract) {//喂食过铁锭的生物随机掉落铁粒
            this.entityDropItem(new ItemStack(Items.IRON_NUGGET, new Random().nextInt(6)+3));
        }
    }

    @Inject(method = "readAdditional", at = @At(value = "TAIL"))
    public void readAdditional(CompoundNBT compound, CallbackInfo ci) {
        canAttract = compound.getBoolean("LivingEntityMixin$canAttract");
    }

    @Inject(method = "writeAdditional", at = @At(value = "TAIL"))
    public void writeAdditional(CompoundNBT compound, CallbackInfo ci) {
        compound.putBoolean("LivingEntityMixin$canAttract", canAttract);
    }

    @Unique//设置被磁吸
    public void setMagnetAttractor(UUID uuid) {
        magnetAttractor = world.getPlayerByUuid(uuid);
        isAttracted = true;
    }
}
