package com.ddhuan.ifscience.network;

import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.Supplier;

public abstract class NetworkPack {
    public NetworkPack() {
    }

    public NetworkPack(PacketBuffer buffer) {
    }

    public abstract void toBytes(PacketBuffer buf);

    public abstract void handler(Supplier<NetworkEvent.Context> ctx);
}
