package dev.hephaestus.automotion.common;

import dev.hephaestus.automotion.Automotion;
import dev.hephaestus.automotion.common.block.ConveyorBeltBlock;
import dev.hephaestus.automotion.common.block.EntityDetectorBlock;
import dev.hephaestus.automotion.common.block.entity.ConveyorBeltBlockEntity;
import dev.hephaestus.automotion.common.block.entity.EntityDetectorBlockEntity;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.minecraft.block.Block;
import net.minecraft.block.MapColor;
import net.minecraft.block.Material;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.util.registry.Registry;

public class AutomotionBlocks {
    public static final Block CONVEYOR_BELT = register("conveyor_belt", new ConveyorBeltBlock(0.03, FabricBlockSettings.of(Material.METAL)));
    public static final Block BASIC_ENTITY_DETECTOR = register("basic_entity_detector", new EntityDetectorBlock(FabricBlockSettings.of(Material.METAL)));
    public static final Block STEEL_BLOCK = register("steel_block", new Block(FabricBlockSettings.of(Material.METAL, MapColor.BLACK).requiresTool().strength(5.0F, 6.0F).sounds(BlockSoundGroup.METAL)));
    public static final Block LIMESTONE = register("limestone", new Block(FabricBlockSettings.of(Material.STONE, MapColor.OFF_WHITE).requiresTool().strength(1.2F, 3F).sounds(BlockSoundGroup.STONE)));

    public static final BlockEntityType<ConveyorBeltBlockEntity> CONVEYOR_BELT_BLOCK_ENTITY = Registry.register(Registry.BLOCK_ENTITY_TYPE, Automotion.id("conveyor_belt"), FabricBlockEntityTypeBuilder.create(ConveyorBeltBlockEntity::new, CONVEYOR_BELT).build());
    public static final BlockEntityType<EntityDetectorBlockEntity> BASIC_ENTITY_DETECTOR_BLOCK_ENTITY = Registry.register(Registry.BLOCK_ENTITY_TYPE, Automotion.id("basic_entity_detector"), FabricBlockEntityTypeBuilder.create(EntityDetectorBlockEntity::new, BASIC_ENTITY_DETECTOR).build());

    public static void init() {

    }

    private static Block register(String id, Block block) {
        return Registry.register(Registry.BLOCK, Automotion.id(id), block);
    }
}
