package hephaestus.dev.automotion.common;

import hephaestus.dev.automotion.client.gui.screen.ingame.DiamondHopperScreen;
import hephaestus.dev.automotion.common.block.*;
import hephaestus.dev.automotion.common.block.conveyors.ConveyorBelt;
import hephaestus.dev.automotion.common.block.conveyors.DetectorConveyorBelt;
import hephaestus.dev.automotion.common.block.conveyors.HeatedConveyorBelt;
import hephaestus.dev.automotion.common.block.entity.DiamondHopperBlockEntity;
import hephaestus.dev.automotion.common.block.entity.FanBlockEntity;
import hephaestus.dev.automotion.common.block.entity.GoldenHopperBlockEntity;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.fabricmc.fabric.api.client.screenhandler.v1.ScreenRegistry;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.Material;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.state.property.Properties;
import net.minecraft.util.registry.Registry;

import static hephaestus.dev.automotion.common.Automotion.newID;

public class AutomotionBlocks {
	public static final Block CONVEYOR_BELT = new ConveyorBelt(FabricBlockSettings.of(Material.METAL), 0.125, true);
	public static final Block GOLDEN_CONVEYOR_BELT = new ConveyorBelt(FabricBlockSettings.of(Material.METAL), 0.5, false);
	public static final Block DETECTOR_CONVEYOR_BELT = new DetectorConveyorBelt(FabricBlockSettings.of(Material.METAL), 0.125);
	public static final Block HEATED_CONVEYOR_BELT = new HeatedConveyorBelt(FabricBlockSettings.of(Material.METAL).lightLevel((state) -> state.get(Properties.POWERED) ? 15 : 0), 0.125);
	public static final Block GOLDEN_HOPPER = new GoldenHopper(FabricBlockSettings.copyOf(Blocks.GOLD_BLOCK).nonOpaque());
	public static final Block DIAMOND_HOPPER = new DiamondHopperBlock(FabricBlockSettings.copyOf(Blocks.DIAMOND_BLOCK).nonOpaque());
	public static final Block WEAK_FAN = new FanBlock(FabricBlockSettings.of(Material.METAL).nonOpaque(), 1);
	public static final Block STRONG_FAN = new FanBlock(FabricBlockSettings.of(Material.METAL).nonOpaque(), 7);

	public static final Block IRON_DUCT = new DuctBlock(Blocks.IRON_BLOCK);
	public static final Block GLASS_DUCT = new GlassDuctBlock(Blocks.GLASS);
	public static final Block IRON_DUCT_OPENING = new DuctOpeningBlock(Blocks.IRON_BLOCK);
	public static final Block IRON_DUCT_DOOR = new DuctDoorBlock(Blocks.IRON_BLOCK);

	public static final BlockEntityType<GoldenHopperBlockEntity> GOLDEN_HOPPER_TYPE = BlockEntityType.Builder.create(GoldenHopperBlockEntity::new, GOLDEN_HOPPER).build(null);
	public static final BlockEntityType<DiamondHopperBlockEntity> DIAMOND_HOPPER_TYPE = BlockEntityType.Builder.create(DiamondHopperBlockEntity::new, DIAMOND_HOPPER).build(null);
	public static final BlockEntityType<FanBlockEntity> FAN_TYPE = BlockEntityType.Builder.create(FanBlockEntity::new, WEAK_FAN, STRONG_FAN).build(null);

	public static void init() {
		Registry.register(Registry.BLOCK, newID("conveyor_belt"), CONVEYOR_BELT);
		Registry.register(Registry.BLOCK, newID("golden_conveyor_belt"), GOLDEN_CONVEYOR_BELT);
		Registry.register(Registry.BLOCK, newID("detector_conveyor_belt"), DETECTOR_CONVEYOR_BELT);
		Registry.register(Registry.BLOCK, newID("heated_conveyor_belt"), HEATED_CONVEYOR_BELT);
		Registry.register(Registry.BLOCK, newID("golden_hopper"), GOLDEN_HOPPER);
		Registry.register(Registry.BLOCK, newID("diamond_hopper"), DIAMOND_HOPPER);
		Registry.register(Registry.BLOCK, newID("weak_fan"), WEAK_FAN);
		Registry.register(Registry.BLOCK, newID("strong_fan"), STRONG_FAN);

		Registry.register(Registry.BLOCK, newID("iron_duct"), IRON_DUCT);
		Registry.register(Registry.BLOCK, newID("glass_duct"), GLASS_DUCT);
		Registry.register(Registry.BLOCK, newID("iron_duct_opening"), IRON_DUCT_OPENING);
		Registry.register(Registry.BLOCK, newID("iron_duct_door"), IRON_DUCT_DOOR);

		Registry.register(Registry.BLOCK_ENTITY_TYPE, newID("golden_hopper"), GOLDEN_HOPPER_TYPE);
		Registry.register(Registry.BLOCK_ENTITY_TYPE, newID("diamond_hopper"), DIAMOND_HOPPER_TYPE);
		Registry.register(Registry.BLOCK_ENTITY_TYPE, newID("fan"), FAN_TYPE);
	}

	public static void initClient() {
		BlockRenderLayerMap.INSTANCE.putBlock(AutomotionBlocks.WEAK_FAN, RenderLayer.getCutout());
		BlockRenderLayerMap.INSTANCE.putBlock(AutomotionBlocks.HEATED_CONVEYOR_BELT, RenderLayer.getCutout());
		BlockRenderLayerMap.INSTANCE.putBlock(AutomotionBlocks.GLASS_DUCT, RenderLayer.getCutout());
		BlockRenderLayerMap.INSTANCE.putBlock(AutomotionBlocks.STRONG_FAN, RenderLayer.getCutout());
		BlockRenderLayerMap.INSTANCE.putBlock(AutomotionBlocks.IRON_DUCT_DOOR, RenderLayer.getCutout());

		ScreenRegistry.register(Automotion.DIAMOND_HOPPER, DiamondHopperScreen::new);
	}
}
