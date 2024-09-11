package com.ddhuan.ifscience.mixin;

import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.AbstractRepairContainer;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.inventory.container.RepairContainer;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IWorldPosCallable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import javax.annotation.Nullable;

@Mixin(RepairContainer.class)
public abstract class RepairContainerMixin extends AbstractRepairContainer {
    public RepairContainerMixin(@Nullable ContainerType<?> p_i231587_1_, int p_i231587_2_, PlayerInventory p_i231587_3_, IWorldPosCallable p_i231587_4_) {
        super(p_i231587_1_, p_i231587_2_, p_i231587_3_, p_i231587_4_);
    }

    //铁砧的使用修改
    /*@ModifyVariable(method = "updateRepairOutput", at = @At(value = "STORE"), index = 8)
    public boolean updateRepairOutput$Mixin1(boolean value) {
        ItemStack itemstack2 = this.field_234643_d_.getStackInSlot(1);
        return itemstack2.getItem() == itemRegistry.modEnchantedBook.get() && !EnchantedBookItem.getEnchantments(itemstack2).isEmpty();
    }*/

    @Redirect(method = "updateRepairOutput", at = @At(value = "INVOKE", target = "Lnet/minecraft/item/ItemStack;isDamageable()Z", ordinal = 1))
    public boolean updateRepairOutput$Mixin1(ItemStack instance) {
        return true;
    }

    @Redirect(method = "updateRepairOutput", at = @At(value = "INVOKE", target = "Lnet/minecraft/item/ItemStack;setRepairCost(I)V"))
    public void updateRepairOutput$Mixin2(ItemStack instance, int cost) {
        if (!(instance.getItem() instanceof BlockItem))//取消方块物品的附魔花费累计
            instance.setRepairCost(cost);
    }
}
