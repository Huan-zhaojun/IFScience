package com.ddhuan.ifscience.network.Client;

import com.ddhuan.ifscience.network.IModPack;
import com.google.common.collect.ArrayListMultimap;
import net.minecraft.client.Minecraft;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.network.PacketBuffer;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.Supplier;

public class cloudParticlePack implements IModPack {
    ArrayListMultimap<Vector3d, Double[]> cloudParticles;

    public cloudParticlePack(ArrayListMultimap<Vector3d, Double[]> cloudParticles) {
        this.cloudParticles = cloudParticles;
    }

    public cloudParticlePack(PacketBuffer buffer) {
        int size = buffer.readInt();
        cloudParticles = ArrayListMultimap.create();
        for (int i = 0; i < size; i++) {
            cloudParticles.put(new Vector3d(buffer.readDouble(),buffer.readDouble(),buffer.readDouble()),
                    new Double[]{buffer.readDouble(), buffer.readDouble(), buffer.readDouble()});
        }
    }

    @Override
    public void toBytes(PacketBuffer buf) {
        buf.writeInt(cloudParticles.size());
        cloudParticles.forEach((vector3d, double1) -> {
            buf.writeDouble(vector3d.x);
            buf.writeDouble(vector3d.y);
            buf.writeDouble(vector3d.z);
            for (Double d : double1) {
                buf.writeDouble(d);
            }
        });
    }

    @Override
    public void handler(Supplier<NetworkEvent.Context> ctx) {
        NetworkEvent.Context context = ctx.get();
        context.enqueueWork(() -> {
            ClientWorld world = Minecraft.getInstance().world;
            if (world != null && world.isRemote) {
                cloudParticles.forEach((Vector3d, speeds) ->
                        world.addParticle(ParticleTypes.CLOUD, Vector3d.x, Vector3d.y, Vector3d.z, speeds[0], speeds[1], speeds[2]));
            }
        });
        context.setPacketHandled(true);
    }
}
