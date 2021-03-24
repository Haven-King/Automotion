package dev.hephaestus.automotion.common;

import dev.hephaestus.automotion.Automotion;
import dev.hephaestus.automotion.common.entity.PhysicsBlockEntity;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricEntityTypeBuilder;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityType;
import net.minecraft.util.registry.Registry;

public class AutomotionEntities {
    public static final EntityType<PhysicsBlockEntity> PHYSICS_BLOCK = Registry.register(Registry.ENTITY_TYPE, Automotion.id("physics_block"), FabricEntityTypeBuilder.<PhysicsBlockEntity>create().entityFactory(PhysicsBlockEntity::new).dimensions(EntityDimensions.fixed(1, 1)).build());

    public static void init() {

    }
}
