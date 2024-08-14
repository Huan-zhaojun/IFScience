package com.ddhuan.ifscience.network.Client;

import com.ddhuan.ifscience.network.IModPack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraftforge.fml.network.NetworkEvent;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.function.Supplier;

//播放声音通用网络包
//soundEvents填写相对于的ResourceLocation路径名(参考SoundEvents类中)
//soundCategory填写soundCategory枚举的枚举名
public class playSoundPack implements IModPack {
    double x, y, z;
    String soundEvents, soundCategory;
    float volume, pitch;
    boolean distanceDelay;

    public playSoundPack(double x, double y, double z, String soundEvents, String soundCategory, float volume, float pitch, boolean distanceDelay) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.soundEvents = soundEvents;
        this.soundCategory = soundCategory;
        this.volume = volume;
        this.pitch = pitch;
        this.distanceDelay = distanceDelay;
    }

    public playSoundPack(PacketBuffer buffer) {
      this.x = buffer.readDouble();
      this.y = buffer.readDouble();
      this.z = buffer.readDouble();
      this.soundEvents = buffer.readString();
      this.soundCategory = buffer.readString();
      this.volume = buffer.readFloat();
      this.pitch = buffer.readFloat();
      this.distanceDelay = buffer.readBoolean();
    }

    @Override
    public void toBytes(PacketBuffer buf) {
        buf.writeDouble(x);
        buf.writeDouble(y);
        buf.writeDouble(z);
        buf.writeString(soundEvents);
        buf.writeString(soundCategory);
        buf.writeFloat(volume);
        buf.writeFloat(pitch);
        buf.writeBoolean(distanceDelay);
    }

    @Override
    public void handler(Supplier<NetworkEvent.Context> ctx) {
        NetworkEvent.Context context = ctx.get();
        context.enqueueWork(() -> {
            ClientWorld world = Minecraft.getInstance().world;
            if (world != null && world.isRemote) {
                SoundEvent soundEvent = ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation(soundEvents));
                if (soundEvent == null) {
                    System.out.println("错误！soundEvent没有找到！！！");
                    return;
                }
                SoundCategory soundCategory1 = SoundCategory.valueOf(soundCategory);
                if (soundCategory1 == null) {
                    System.out.println("错误！soundCategory1没有找到！！！");
                    return;
                }
                world.playSound(x, y, z, soundEvent, soundCategory1, volume, pitch, distanceDelay);
            }
        });
        context.setPacketHandled(true);
    }
}
