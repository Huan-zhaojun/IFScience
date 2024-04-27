package com.ddhuan.ifscience.network;

import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.Supplier;

public interface IModPack {
    void toBytes(PacketBuffer buf);
    void handler(Supplier<NetworkEvent.Context> ctx);
}
