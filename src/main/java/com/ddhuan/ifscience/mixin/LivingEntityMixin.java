package com.ddhuan.ifscience.mixin;

import com.ddhuan.ifscience.common.customDamage;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.loot.LootContext;
import net.minecraft.loot.LootTable;
import net.minecraft.util.DamageSource;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin extends Entity {
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
}
