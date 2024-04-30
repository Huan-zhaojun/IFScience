package com.ddhuan.ifscience.common.Entity;

import com.ddhuan.ifscience.ifscience;
import net.minecraft.entity.EntityClassification;
import net.minecraft.entity.EntityType;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class entityTypeRegistry {
    public static final DeferredRegister<EntityType<?>> ENTITIES = DeferredRegister.create(ForgeRegistries.ENTITIES, ifscience.MOD_ID);
    public static final RegistryObject<EntityType<furnaceTNTEntity>> furnaceTNT = ENTITIES.register("furnace_tnt",
            () -> EntityType.Builder.<furnaceTNTEntity>create(furnaceTNTEntity::new, EntityClassification.MISC).immuneToFire().size(0.98F, 0.98F).trackingRange(10).updateInterval(10).build("furnace_tnt"));
}
