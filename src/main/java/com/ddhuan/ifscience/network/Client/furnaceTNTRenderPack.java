package com.ddhuan.ifscience.network.Client;

import com.ddhuan.ifscience.common.Entity.furnaceTNTEntity;
import com.ddhuan.ifscience.network.IModPack;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.client.Minecraft;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.Supplier;

//用于同步渲染相应的熔炉方块状态
public class furnaceTNTRenderPack implements IModPack {
    private BlockPos pos = new BlockPos(0, 0, 0);
    private BlockState blockState = Blocks.AIR.getDefaultState();

    public furnaceTNTRenderPack(BlockPos pos) {
        this.pos = pos;
    }

    public furnaceTNTRenderPack(BlockState blockState) {
        this.blockState = blockState;
    }

    public furnaceTNTRenderPack(PacketBuffer buffer) {
        this.pos = buffer.readBlockPos();
        this.blockState = Block.getStateById(buffer.readInt());
    }

    @Override
    public void toBytes(PacketBuffer buf) {
        buf.writeBlockPos(pos);
        buf.writeInt(Block.getStateId(blockState));
    }

    @Override
    public void handler(Supplier<NetworkEvent.Context> ctx) {
        NetworkEvent.Context context = ctx.get();
        context.enqueueWork(() -> {
            ClientWorld world = Minecraft.getInstance().world;
            if (world != null && world.isRemote) {
                for (Entity entity : world.getAllEntities()) {
                    if (entity instanceof furnaceTNTEntity) {
                        furnaceTNTEntity furnaceTNT = (furnaceTNTEntity) entity;
                        furnaceTNT.furnace = blockState.equals(Blocks.AIR.getDefaultState()) ? world.getBlockState(pos) : blockState;
                    }
                }
            }
        });
        context.setPacketHandled(true);
    }
}
