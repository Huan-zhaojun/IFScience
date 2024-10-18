package com.ddhuan.ifscience.common;

import com.ddhuan.ifscience.ifscience;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public final class SoundEventRegistry {
    public static final DeferredRegister<SoundEvent> SOUNDS = DeferredRegister.create(ForgeRegistries.SOUND_EVENTS, ifscience.MOD_ID);
    public static final RegistryObject<SoundEvent> glassBurst = SOUNDS.register("glass_burst",
            () -> new SoundEvent(new ResourceLocation(ifscience.MOD_ID, "glass_burst")));
}
