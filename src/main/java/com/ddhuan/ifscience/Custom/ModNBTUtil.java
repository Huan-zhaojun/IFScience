package com.ddhuan.ifscience.Custom;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.PacketBuffer;

public final class ModNBTUtil {
    /**
     *@see PacketBuffer#writeItemStack(ItemStack)
     */
    public static CompoundNBT writeItemStack(CompoundNBT compound, ItemStack stack) {
        return writeItemStack(compound, stack, true);
    }

    /**
     *@see PacketBuffer#writeItemStack(ItemStack, boolean)
     */
    public static CompoundNBT writeItemStack(CompoundNBT compound, ItemStack stack, boolean limitedTag) {
        if (stack.isEmpty()) {
            compound.putBoolean("ItemStack$isEmpty", false);
        } else {
            compound.putBoolean("ItemStack$isEmpty", true);
            Item item = stack.getItem();
            compound.putInt("ItemStack$Id", Item.getIdFromItem(item));
            compound.putByte("ItemStack$Count", (byte) stack.getCount());
            if (item.isDamageable(stack) || item.shouldSyncTag()) {
                compound.putBoolean("ItemStack$isTag", true);
                compound.put("ItemStack$Tag", limitedTag ? stack.getShareTag() : stack.getTag());
            } else compound.putBoolean("ItemStack$isTag", false);
        }
        return compound;
    }

    public static ItemStack readItemStack(CompoundNBT compound) {
        if (!compound.getBoolean("ItemStack$isEmpty")) {
            return ItemStack.EMPTY;
        } else {
            int i = compound.getInt("ItemStack$Id");
            int j = compound.getByte("ItemStack$Count");
            ItemStack itemstack = new ItemStack(Item.getItemById(i), j);
            if (compound.getBoolean("ItemStack$isTag"))
                itemstack.readShareTag(compound.getCompound("ItemStack$Tag"));
            return itemstack;
        }
    }
}
