package com.ddhuan.ifscience.network.Client;

import com.ddhuan.ifscience.common.Entity.blockEntity;
import com.ddhuan.ifscience.network.IModPack;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.client.Minecraft;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.Objects;
import java.util.UUID;
import java.util.function.Supplier;

public class blockEntityRenderPack implements IModPack {
    private BlockState blockState = Blocks.AIR.getDefaultState();
    private UUID uuid;

    public blockEntityRenderPack(UUID uuid, BlockState blockState) {
        this.uuid = uuid;
        this.blockState = blockState;
    }

    public blockEntityRenderPack(PacketBuffer buffer) {
        this.uuid = buffer.readUniqueId();
        this.blockState = NBTUtil.readBlockState(Objects.requireNonNull(buffer.readCompoundTag()));
    }

    @Override
    public void toBytes(PacketBuffer buf) {
        buf.writeUniqueId(uuid);
        buf.writeCompoundTag(NBTUtil.writeBlockState(blockState));
    }

    @Override
    public void handler(Supplier<NetworkEvent.Context> ctx) {
        NetworkEvent.Context context = ctx.get();
        context.enqueueWork(() -> {
            ClientWorld world = Minecraft.getInstance().world;
            if (world != null && world.isRemote) {
                for (Entity entity : world.getAllEntities()) {
                    if (entity instanceof blockEntity) {
                        blockEntity blockEntity = (blockEntity) entity;
                        if (blockEntity.getUniqueID().equals(uuid)) blockEntity.blockState = blockState;
                    }
                }
            }
        });
        context.setPacketHandled(true);
    }
}
