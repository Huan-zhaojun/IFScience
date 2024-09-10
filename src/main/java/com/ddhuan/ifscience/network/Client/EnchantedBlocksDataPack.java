package com.ddhuan.ifscience.network.Client;

import com.ddhuan.ifscience.common.Enchantment.EnchantedBlocksData;
import com.ddhuan.ifscience.network.IModPack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

public class EnchantedBlocksDataPack implements IModPack {
    private HashMap<BlockPos, ListNBT> enchantedBlocks = new HashMap<>();
    private int flag = 0;//默认0全部发送，1添加，2删除
    private BlockPos blockPos;
    private ListNBT listNBT;

    public EnchantedBlocksDataPack(HashMap<BlockPos, ListNBT> enchantedBlocks) {
        this.enchantedBlocks = enchantedBlocks;
        flag = 0;
    }

    public EnchantedBlocksDataPack(BlockPos blockPos) {
        this.blockPos = blockPos;
        flag = 2;
    }

    public EnchantedBlocksDataPack(BlockPos blockPos, ListNBT listNBT) {
        this.blockPos = blockPos;
        this.listNBT = listNBT;
        flag = 1;
    }


    public EnchantedBlocksDataPack(PacketBuffer buffer) {
        flag = buffer.readInt();
        switch (flag) {
            case 0:
                ListNBT blockList = (ListNBT) buffer.readCompoundTag().get("EnchantedBlocks");
                if (blockList != null) {
                    for (INBT inbt : blockList) {
                        CompoundNBT blockData = (CompoundNBT) inbt;
                        enchantedBlocks.put(NBTUtil.readBlockPos(blockData.getCompound("BlockPos")),
                                (ListNBT) blockData.get("Enchantments"));
                    }
                }
                break;
            case 1:
                CompoundNBT nbt2 = buffer.readCompoundTag();
                this.blockPos = NBTUtil.readBlockPos(nbt2.getCompound("BlockPos"));
                this.listNBT = (ListNBT) nbt2.get("Enchantments");
                break;
            case 2:
                this.blockPos = buffer.readBlockPos();
                break;
        }
    }

    @Override
    public void toBytes(PacketBuffer buf) {
        buf.writeInt(flag);
        switch (flag) {
            case 0:
                ListNBT blockList = new ListNBT();
                for (Map.Entry<BlockPos, ListNBT> entry : enchantedBlocks.entrySet()) {
                    CompoundNBT blockData = new CompoundNBT();
                    blockData.put("BlockPos", NBTUtil.writeBlockPos(entry.getKey()));
                    blockData.put("Enchantments", entry.getValue());
                    blockList.add(blockData);
                }
                CompoundNBT nbt = new CompoundNBT();
                nbt.put("EnchantedBlocks", blockList);
                buf.writeCompoundTag(nbt);
                break;
            case 1:
                CompoundNBT nbt2 = new CompoundNBT();
                nbt2.put("BlockPos", NBTUtil.writeBlockPos(blockPos));
                nbt2.put("Enchantments", listNBT);
                buf.writeCompoundTag(nbt2);
                break;
            case 2:
                buf.writeBlockPos(blockPos);
                break;
        }
    }

    @Override
    public void handler(Supplier<NetworkEvent.Context> ctx) {
        NetworkEvent.Context context = ctx.get();
        context.enqueueWork(() -> {
            ClientWorld world = Minecraft.getInstance().world;
            if (world != null && world.isRemote) {
                switch (flag) {
                    case 0:
                        EnchantedBlocksData.EnchantedBlocksClientData.enchantedBlocks = enchantedBlocks;
                        break;
                    case 1:
                        EnchantedBlocksData.EnchantedBlocksClientData.enchantedBlocks.put(blockPos, listNBT);
                        break;
                    case 2:
                        EnchantedBlocksData.EnchantedBlocksClientData.enchantedBlocks.remove(blockPos);
                        break;
                }
            }
        });
        context.setPacketHandled(true);
    }
}
