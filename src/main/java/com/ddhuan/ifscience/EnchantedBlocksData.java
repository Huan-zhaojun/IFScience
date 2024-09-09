package com.ddhuan.ifscience;

import com.ddhuan.ifscience.network.Client.EnchantedBlocksDataPack;
import com.ddhuan.ifscience.network.Network;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraft.world.storage.DimensionSavedDataManager;
import net.minecraft.world.storage.WorldSavedData;
import net.minecraftforge.fml.network.PacketDistributor;

import java.util.HashMap;
import java.util.Map;

public class EnchantedBlocksData extends WorldSavedData {
    private static final String NAME = "EnchantedBlocksData";
    private final HashMap<BlockPos, ListNBT> enchantedBlocks = new HashMap<>();

    //获取当前维度的自定义数据实例
    public static EnchantedBlocksData get(World worldIn) {
        if (!(worldIn instanceof ServerWorld))
            throw new RuntimeException("Attempted to get the data from a client world. This is wrong.");
        ServerWorld world = worldIn.getServer().getWorld(worldIn.getDimensionKey());
        DimensionSavedDataManager storage = world.getSavedData();
        return storage.getOrCreate(EnchantedBlocksData::new, NAME);
    }

    //将某个方块的附魔数据保存到 HashMap 中
    public void addEnchantedBlock(BlockPos pos, ItemStack itemStack) {
        ListNBT listNBT = itemStack.getEnchantmentTagList();
        enchantedBlocks.put(pos, listNBT);
        markDirty(); //标记数据已修改，需要保存
        syncData(pos, listNBT);
    }

    //获取某个方块的所有附魔数据，不存在就返回空Map
    public Map<Enchantment, Integer> getEnchantedBlock(BlockPos pos) {
        ListNBT listNBT = enchantedBlocks.get(pos);
        return listNBT != null ? EnchantmentHelper.deserializeEnchantments(listNBT) : new HashMap<>();
    }

    //获取某个方块的某个附魔数据的数据，不存在就返回null
    public Integer getEnchantedBlock(BlockPos pos, Enchantment enchantment) {
        ListNBT listNBT = enchantedBlocks.get(pos);
        if (listNBT != null) {
            Integer i = EnchantmentHelper.deserializeEnchantments(listNBT).get(enchantment);
            return i;
        } else return null;
    }

    //删除某个方块的附魔数据
    public void removeEnchantedBlock(BlockPos pos) {
        enchantedBlocks.remove(pos);
        markDirty(); //标记数据已修改
        syncData(pos);
    }

    //同步数据
    public void syncData() {
        Network.INSTANCE.send(PacketDistributor.ALL.noArg(), new EnchantedBlocksDataPack(enchantedBlocks));
    }

    public void syncData(BlockPos blockPos) {
        Network.INSTANCE.send(PacketDistributor.ALL.noArg(), new EnchantedBlocksDataPack(blockPos));
    }

    public void syncData(BlockPos blockPos, ListNBT listNBT) {
        Network.INSTANCE.send(PacketDistributor.ALL.noArg(), new EnchantedBlocksDataPack(blockPos, listNBT));
    }

    @Override
    public void read(CompoundNBT nbt) {
        ListNBT blockList = (ListNBT) nbt.get("EnchantedBlocks");
        if (blockList != null) {
            for (INBT inbt : blockList) {
                CompoundNBT blockData = (CompoundNBT) inbt;
                enchantedBlocks.put(NBTUtil.readBlockPos(blockData.getCompound("BlockPos")),
                        (ListNBT) blockData.get("Enchantments"));
            }
        }
        syncData();
    }

    @Override
    public CompoundNBT write(CompoundNBT compound) {
        ListNBT blockList = new ListNBT();
        for (Map.Entry<BlockPos, ListNBT> entry : enchantedBlocks.entrySet()) {
            CompoundNBT blockData = new CompoundNBT();
            blockData.put("BlockPos", NBTUtil.writeBlockPos(entry.getKey()));
            blockData.put("Enchantments", entry.getValue());
            blockList.add(blockData);
        }
        compound.put("EnchantedBlocks", blockList);
        return compound;
    }

    public EnchantedBlocksData() {
        super(NAME);
    }

    public static class EnchantedBlocksClientData {
        public static HashMap<BlockPos, ListNBT> enchantedBlocks = new HashMap<>();
    }
}
