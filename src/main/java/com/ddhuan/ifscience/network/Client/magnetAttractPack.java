package com.ddhuan.ifscience.network.Client;

import com.ddhuan.ifscience.common.Entity.MagnetAttractedBlockEntity;
import com.ddhuan.ifscience.network.IModPack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;

import java.lang.reflect.InvocationTargetException;
import java.util.UUID;
import java.util.function.Supplier;

public class magnetAttractPack implements IModPack {
    private boolean isisAttracted = true;
    private UUID entityUuid, magnetAttractorUuid;
    private int flag;

    public magnetAttractPack(Class<?> aclass, UUID entityUuid, UUID magnetAttractorUuid) {
        this.entityUuid = entityUuid;
        this.magnetAttractorUuid = magnetAttractorUuid;
        if (MagnetAttractedBlockEntity.class.isAssignableFrom(aclass)) {
            flag = 0;
        } else if (LivingEntity.class.isAssignableFrom(aclass)) {
            flag = 1;
        }
    }

    public magnetAttractPack(Class<?> aclass, UUID entityUuid) {
        this.isisAttracted = false;
        this.entityUuid = entityUuid;
        if (MagnetAttractedBlockEntity.class.isAssignableFrom(aclass)) {
            flag = 0;
        } else if (LivingEntity.class.isAssignableFrom(aclass)) {
            flag = 1;
        }
    }

    public magnetAttractPack(PacketBuffer buffer) {
        isisAttracted = buffer.readBoolean();
        entityUuid = buffer.readUniqueId();
        if (isisAttracted) magnetAttractorUuid = buffer.readUniqueId();
        flag = buffer.readInt();
    }

    @Override
    public void toBytes(PacketBuffer buf) {
        buf.writeBoolean(isisAttracted);
        buf.writeUniqueId(entityUuid);
        if (isisAttracted) buf.writeUniqueId(magnetAttractorUuid);
        buf.writeInt(flag);
    }

    @Override
    public void handler(Supplier<NetworkEvent.Context> ctx) {
        NetworkEvent.Context context = ctx.get();
        context.enqueueWork(() -> {
            ClientWorld world = Minecraft.getInstance().world;
            if (world != null && world.isRemote) {
                for (Entity entity : world.getAllEntities()) {
                    if (entity.getUniqueID().equals(entityUuid)) {
                        switch (flag) {
                            case 0:
                                MagnetAttractedBlockEntity entity1 = (MagnetAttractedBlockEntity) entity;
                                if (isisAttracted) {
                                    entity1.setMagnetAttractor(magnetAttractorUuid);
                                } else {
                                    entity1.isAttracted = false;
                                }
                                break;
                            case 1:
                                try {
                                    LivingEntity entity2 = (LivingEntity) entity;
                                    if (isisAttracted) {
                                        entity2.getClass().getMethod("setMagnetAttractor", UUID.class).invoke(entity, magnetAttractorUuid);
                                    } else {
                                        entity2.getClass().getField("isAttracted").set(entity2, false);
                                    }
                                } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException |
                                         NoSuchFieldException e) {
                                    throw new RuntimeException(e);
                                }
                                break;
                        }
                    }
                }
            }
        });
        context.setPacketHandled(true);
    }
}
