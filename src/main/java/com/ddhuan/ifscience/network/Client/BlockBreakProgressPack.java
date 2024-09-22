package com.ddhuan.ifscience.network.Client;

import com.ddhuan.ifscience.network.NetworkPack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.Supplier;

public class BlockBreakProgressPack extends NetworkPack {
    private BlockPos pos;
    private int progress;

    public BlockBreakProgressPack(BlockPos pos, int progress) {
        this.pos = pos;
        this.progress = progress;
    }

    public BlockBreakProgressPack(PacketBuffer buffer) {
        super(buffer);
        this.pos = buffer.readBlockPos();
        this.progress = buffer.readInt();
    }

    @Override
    public void toBytes(PacketBuffer buf) {
        buf.writeBlockPos(pos);
        buf.writeInt(progress);
    }

    @Override
    public void handler(Supplier<NetworkEvent.Context> ctx) {
        NetworkEvent.Context context = ctx.get();
        context.enqueueWork(() -> {
            Minecraft mc = Minecraft.getInstance();
            ClientWorld world = mc.world;
            if (world != null && world.isRemote && mc.player != null) {
                world.sendBlockBreakProgress(pos.hashCode(), pos, progress);
            }
        });
        context.setPacketHandled(true);
    }
}
