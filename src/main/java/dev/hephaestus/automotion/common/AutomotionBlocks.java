package dev.hephaestus.automotion.common;

import dev.hephaestus.automotion.Automotion;
import dev.hephaestus.automotion.common.block.ConveyorBeltBlock;
import dev.hephaestus.automotion.common.block.EntityDetectorBlock;
import dev.hephaestus.automotion.common.block.entity.ConveyorBeltBlockEntity;
import dev.hephaestus.automotion.common.block.entity.EntityDetectorBlockEntity;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.minecraft.block.Block;
import net.minecraft.block.Material;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.util.registry.Registry;

public class AutomotionBlocks {
    public static final Block CONVEYOR_BELT = Registry.register(Registry.BLOCK, Automotion.id("conveyor_belt"), new ConveyorBeltBlock(0.1, FabricBlockSettings.of(Material.METAL)));
    public static final Block BASIC_ENTITY_DETECTOR = Registry.register(Registry.BLOCK, Automotion.id("basic_entity_detector"), new EntityDetectorBlock(FabricBlockSettings.of(Material.METAL)));

    public static final BlockEntityType<ConveyorBeltBlockEntity> CONVEYOR_BELT_BLOCK_ENTITY = Registry.register(Registry.BLOCK_ENTITY_TYPE, Automotion.id("conveyor_belt"), FabricBlockEntityTypeBuilder.create(ConveyorBeltBlockEntity::new, CONVEYOR_BELT).build());
    public static final BlockEntityType<EntityDetectorBlockEntity> BASIC_ENTITY_DETECTOR_BLOCK_ENTITY = Registry.register(Registry.BLOCK_ENTITY_TYPE, Automotion.id("basic_entity_detector"), FabricBlockEntityTypeBuilder.create(EntityDetectorBlockEntity::new, BASIC_ENTITY_DETECTOR).build());

    public static void init() {

    }
}
