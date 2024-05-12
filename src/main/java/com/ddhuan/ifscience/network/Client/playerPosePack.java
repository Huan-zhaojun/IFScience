package com.ddhuan.ifscience.network.Client;

import com.ddhuan.ifscience.network.IModPack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Pose;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.UUID;
import java.util.function.Supplier;

public class playerPosePack implements IModPack {
    private UUID uuid;
    private Pose pose;

    public playerPosePack(UUID uuid, Pose pose) {
        this.uuid = uuid;
        this.pose = pose;
    }

    public playerPosePack(PacketBuffer buffer) {
        uuid = buffer.readUniqueId();
        int ordinal = buffer.readInt();
        if (ordinal >= 0 && ordinal < Pose.values().length) {
            pose = Pose.values()[ordinal];
        } else if (ordinal == -1) pose = null;
    }

    @Override
    public void toBytes(PacketBuffer buf) {
        buf.writeUniqueId(uuid);
        if (pose != null) buf.writeInt(pose.ordinal());
        else buf.writeInt(-1);
    }

    @Override
    public void handler(Supplier<NetworkEvent.Context> ctx) {
        NetworkEvent.Context context = ctx.get();
        context.enqueueWork(() -> {
            ClientWorld world = Minecraft.getInstance().world;
            if (world != null && world.isRemote) {
                PlayerEntity player = world.getPlayerByUuid(uuid);
                if (player != null) {
                    player.setForcedPose(pose);
                }
            }
        });
        context.setPacketHandled(true);
    }
}
