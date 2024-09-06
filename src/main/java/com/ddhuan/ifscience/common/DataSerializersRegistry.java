package com.ddhuan.ifscience.common;

import com.ddhuan.ifscience.common.Entity.CutBlockEntity;
import com.ddhuan.ifscience.ifscience;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.datasync.IDataSerializer;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DataSerializerEntry;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class DataSerializersRegistry {
    public static final DeferredRegister<DataSerializerEntry> DATA_SERIALIZERS = DeferredRegister.create(ForgeRegistries.DATA_SERIALIZERS, ifscience.MOD_ID);

    public static RegistryObject<DataSerializerEntry> CutBlockEntity_Direction = DATA_SERIALIZERS.register("cutblockentity_direction",
            () -> new DataSerializerEntry(new IDataSerializer<CutBlockEntity.Direction>() {
                @Override
                public void write(PacketBuffer buf, CutBlockEntity.Direction direction) {
                    buf.writeByte(direction.index);
                }

                @Override
                public CutBlockEntity.Direction read(PacketBuffer buf) {
                    return CutBlockEntity.Direction.get(buf.readByte());
                }

                @Override
                public CutBlockEntity.Direction copyValue(CutBlockEntity.Direction direction) {
                    return direction;
                }
            }));
}
