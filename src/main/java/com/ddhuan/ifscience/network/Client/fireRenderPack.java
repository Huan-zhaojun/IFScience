package com.ddhuan.ifscience.network.Client;

import com.ddhuan.ifscience.network.IModPack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;

import java.lang.reflect.Field;
import java.util.UUID;
import java.util.function.Supplier;

//发包给客户端渲染屏幕火焰和生物附着火焰
public class fireRenderPack implements IModPack {
    private UUID id;

    public fireRenderPack(UUID id) {
        this.id = id;
    }

    public fireRenderPack(PacketBuffer buffer) {
        this.id = buffer.readUniqueId();
    }

    @Override
    public void toBytes(PacketBuffer buf) {
        buf.writeUniqueId(id);
    }

    @Override
    public void handler(Supplier<NetworkEvent.Context> ctx) {
        NetworkEvent.Context context = ctx.get();
        context.enqueueWork(() -> {
            ClientWorld world = Minecraft.getInstance().world;
            if (world != null && world.isRemote) {
                try {
                    Field isFireRenderField = Entity.class.getDeclaredField("fireRender");
                    for (Entity entity : world.getAllEntities()) {
                        if (entity.getUniqueID().equals(id)) isFireRenderField.set(entity, true);
                    }
                } catch (IllegalAccessException | NoSuchFieldException e) {
                    throw new RuntimeException(e);
                }
            }
        });
        context.setPacketHandled(true);
    }
}
