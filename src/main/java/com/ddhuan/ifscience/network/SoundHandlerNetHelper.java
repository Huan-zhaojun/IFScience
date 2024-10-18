package com.ddhuan.ifscience.network;

import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.ISound;
import net.minecraft.client.audio.SimpleSound;
import net.minecraft.client.audio.SoundHandler;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.network.NetworkEvent;
import net.minecraftforge.fml.network.PacketDistributor;
import net.minecraftforge.fml.network.simple.SimpleChannel;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Objects;
import java.util.function.Supplier;

//执行客户端SoundHandler的方法，自动同步网络包
public class SoundHandlerNetHelper {
    public static void play(double x, double y, double z, SoundEvent sound, SoundCategory category, float volume, float pitch, boolean repeat, int repeatDelay) {
        Network.INSTANCE.send(PacketDistributor.ALL.noArg(), new SimpleSoundPack(true, x, y, z, sound, category, volume, pitch, repeat, repeatDelay));
    }

    public static void playDelayed(double x, double y, double z, SoundEvent sound, SoundCategory category, float volume, float pitch, boolean repeat, int repeatDelay) {

    }

    public static void pause() {
        Network.INSTANCE.send(PacketDistributor.ALL.noArg(), new OperatePack(0));
    }

    public static void stop() {
        Network.INSTANCE.send(PacketDistributor.ALL.noArg(), new OperatePack(1));
    }

    public static void resume() {
        Network.INSTANCE.send(PacketDistributor.ALL.noArg(), new OperatePack(2));
    }

    public static void stop(double x, double y, double z, SoundEvent sound, SoundCategory category, float volume, float pitch, boolean repeat, int repeatDelay) {
        Network.INSTANCE.send(PacketDistributor.ALL.noArg(), new SimpleSoundPack(false, x, y, z, sound, category, volume, pitch, repeat, repeatDelay));
    }

    public static void stop(ResourceLocation id, @Nullable SoundCategory category) {
        Network.INSTANCE.send(PacketDistributor.ALL.noArg(), new StopPack(id, category));
    }

    public static void register(final SimpleChannel INSTANCE) {
        INSTANCE.registerMessage(Network.nextID(), SimpleSoundPack.class, SimpleSoundPack::toBytes, SimpleSoundPack::new, SimpleSoundPack::handler);
        INSTANCE.registerMessage(Network.nextID(), StopPack.class, StopPack::toBytes, StopPack::new, StopPack::handler);
        INSTANCE.registerMessage(Network.nextID(), OperatePack.class, OperatePack::toBytes, OperatePack::new, OperatePack::handler);
    }

    private static final class SimpleSoundPack extends NetworkPack {
        boolean playOrStop = true;
        double x, y, z;
        SoundEvent sound;
        SoundCategory category;
        float volume, pitch;
        boolean repeat;
        int repeatDelay;

        public SimpleSoundPack(boolean playOrStop, double x, double y, double z, SoundEvent sound, SoundCategory category, float volume, float pitch, boolean repeat, int repeatDelay) {
            this.playOrStop = playOrStop;
            this.x = x;
            this.y = y;
            this.z = z;
            this.sound = sound;
            this.category = category;
            this.volume = volume;
            this.pitch = pitch;
            this.repeat = repeat;
            this.repeatDelay = repeatDelay;
        }

        public SimpleSoundPack(PacketBuffer buffer) {
            super(buffer);
            this.playOrStop = buffer.readBoolean();
            this.x = buffer.readDouble();
            this.y = buffer.readDouble();
            this.z = buffer.readDouble();
            this.sound = new SoundEvent(buffer.readResourceLocation());
            this.category = SoundCategory.valueOf(buffer.readString().toUpperCase());
            this.volume = buffer.readFloat();
            this.pitch = buffer.readFloat();
            this.repeat = buffer.readBoolean();
            this.repeatDelay = buffer.readInt();
        }

        @Override
        public void toBytes(PacketBuffer buf) {
            buf.writeBoolean(playOrStop);
            buf.writeDouble(x);
            buf.writeDouble(y);
            buf.writeDouble(z);
            buf.writeResourceLocation(sound.getName());
            buf.writeString(category.toString());
            buf.writeFloat(volume);
            buf.writeFloat(pitch);
            buf.writeBoolean(repeat);
            buf.writeInt(repeatDelay);
        }

        @OnlyIn(Dist.CLIENT)
        private static final HashMap<Integer, SimpleSound> map = new HashMap<>();

        @Override
        public void handler(Supplier<NetworkEvent.Context> ctx) {
            NetworkEvent.Context context = ctx.get();
            context.enqueueWork(() -> {
                SoundHandler soundHandler = Minecraft.getInstance().getSoundHandler();
                int hash = Objects.hash(x, y, z, sound.getName().hashCode(), category.getName().hashCode(), volume, pitch, repeat, repeatDelay);
                SimpleSound simpleSound;
                if (!map.containsKey(hash)) {
                    simpleSound = new SimpleSound(sound.getName(), category, volume, pitch, repeat, repeatDelay, ISound.AttenuationType.LINEAR, x, y, z, false);
                    map.put(hash, simpleSound);
                } else simpleSound = map.get(hash);
                if (playOrStop) {
                    if (!soundHandler.isPlaying(simpleSound)) soundHandler.play(simpleSound);//正在播放的就不再会播放
                } else soundHandler.stop(simpleSound);
            });
            context.setPacketHandled(true);
        }
    }

    private static final class StopPack extends NetworkPack {
        ResourceLocation id;
        SoundCategory category;
        boolean SoundCategoryIsNull = false;

        public StopPack(ResourceLocation id, @Nullable SoundCategory category) {
            this.id = id;
            this.category = category;
            if (category == null) SoundCategoryIsNull = true;
        }

        public StopPack(PacketBuffer buffer) {
            super(buffer);
            this.id = buffer.readResourceLocation();
            this.SoundCategoryIsNull = buffer.readBoolean();
            this.category = SoundCategoryIsNull ? null : SoundCategory.valueOf(buffer.readString().toUpperCase());
        }

        @Override
        public void toBytes(PacketBuffer buf) {
            buf.writeResourceLocation(id);
            buf.writeBoolean(SoundCategoryIsNull);
            if (!SoundCategoryIsNull) buf.writeString(category.toString());
        }

        @Override
        public void handler(Supplier<NetworkEvent.Context> ctx) {
            NetworkEvent.Context context = ctx.get();
            context.enqueueWork(() -> {
                SoundHandler soundHandler = Minecraft.getInstance().getSoundHandler();
                soundHandler.stop(id, category);
            });
            context.setPacketHandled(true);
        }
    }

    private static final class OperatePack extends NetworkPack {
        int flag;

        public OperatePack(int flag) {
            this.flag = flag;
        }

        public OperatePack(PacketBuffer buffer) {
            super(buffer);
            this.flag = buffer.readInt();
        }

        @Override
        public void toBytes(PacketBuffer buf) {
            buf.writeInt(flag);
        }

        @Override
        public void handler(Supplier<NetworkEvent.Context> ctx) {
            NetworkEvent.Context context = ctx.get();
            context.enqueueWork(() -> {
                SoundHandler soundHandler = Minecraft.getInstance().getSoundHandler();
                switch (flag) {
                    case 0:
                        soundHandler.pause();
                        break;
                    case 1:
                        soundHandler.stop();
                        break;
                    case 2:
                        soundHandler.resume();
                        break;
                }
            });
            context.setPacketHandled(true);
        }
    }
}
