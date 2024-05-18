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
    public static final RegistryObject<EntityType<touchdownTNTEntity>> touchdownTNT = ENTITIES.register("touchdown_tnt",
            () -> EntityType.Builder.<touchdownTNTEntity>create(touchdownTNTEntity::new, EntityClassification.MISC).immuneToFire().size(0.98F, 0.98F).trackingRange(10).updateInterval(10).build("touchdown_tnt"));

    public static final RegistryObject<EntityType<blockEntity>> blockEntity = ENTITIES.register("block_entity",
            () -> EntityType.Builder.<blockEntity>create(blockEntity::new, EntityClassification.MISC).immuneToFire().size(0.98F, 0.98F).trackingRange(10).updateInterval(10).build("block_entity"));
}
