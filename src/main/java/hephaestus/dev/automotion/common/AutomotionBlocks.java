package hephaestus.dev.automotion.common;

import hephaestus.dev.automotion.common.block.*;
import hephaestus.dev.automotion.common.block.conveyors.ConveyorBelt;
import hephaestus.dev.automotion.common.block.conveyors.DetectorConveyorBelt;
import hephaestus.dev.automotion.common.block.conveyors.HeatedConveyorBelt;
import hephaestus.dev.automotion.common.block.entity.FanBlockEntity;
import hephaestus.dev.automotion.common.block.entity.GoldenHopperBlockEntity;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.minecraft.block.Block;
import net.minecraft.block.Material;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.state.property.Properties;
import net.minecraft.util.registry.Registry;

import static hephaestus.dev.automotion.common.Automotion.newID;

public class AutomotionBlocks {
	public static final Block CONVEYOR_BELT = new ConveyorBelt(FabricBlockSettings.of(Material.METAL), 0.125);
	public static final Block GOLDEN_CONVEYOR_BELT = new ConveyorBelt(FabricBlockSettings.of(Material.METAL), 0.5);
	public static final Block DETECTOR_CONVEYOR_BELT = new DetectorConveyorBelt(FabricBlockSettings.of(Material.METAL), 0.125);
	public static final Block HEATED_CONVEYOR_BELT = new HeatedConveyorBelt(FabricBlockSettings.of(Material.METAL).lightLevel((state) -> state.get(Properties.POWERED) ? 15 : 0), 0.125);
	public static final Block GOLDEN_HOPPER = new GoldenHopper(FabricBlockSettings.of(Material.METAL));
	public static final Block IRON_CHUTE = new ChuteBlock(FabricBlockSettings.of(Material.METAL));
	public static final Block IRON_CHUTE_CAP = new ChuteCapBlock(FabricBlockSettings.of(Material.METAL));
	public static final Block GLASS_CHUTE = new GlassChuteBlock(FabricBlockSettings.of(Material.GLASS).nonOpaque());
	public static final Block WEAK_FAN = new FanBlock(FabricBlockSettings.of(Material.METAL).nonOpaque(), 1);
	public static final Block STRONG_FAN = new FanBlock(FabricBlockSettings.of(Material.METAL).nonOpaque(), 7);

	public static final BlockEntityType<GoldenHopperBlockEntity> GOLDEN_HOPPER_TYPE = BlockEntityType.Builder.create(GoldenHopperBlockEntity::new, GOLDEN_HOPPER).build(null);
	public static final BlockEntityType<FanBlockEntity> FAN_TYPE = BlockEntityType.Builder.create(FanBlockEntity::new, WEAK_FAN, STRONG_FAN).build(null);

	public static void init() {
		Registry.register(Registry.BLOCK, newID("conveyor_belt"), CONVEYOR_BELT);
		Registry.register(Registry.BLOCK, newID("golden_conveyor_belt"), GOLDEN_CONVEYOR_BELT);
		Registry.register(Registry.BLOCK, newID("detector_conveyor_belt"), DETECTOR_CONVEYOR_BELT);
		Registry.register(Registry.BLOCK, newID("heated_conveyor_belt"), HEATED_CONVEYOR_BELT);
		Registry.register(Registry.BLOCK, newID("golden_hopper"), GOLDEN_HOPPER);
		Registry.register(Registry.BLOCK, newID("iron_chute"), IRON_CHUTE);
		Registry.register(Registry.BLOCK, newID("iron_chute_cap"), IRON_CHUTE_CAP);
		Registry.register(Registry.BLOCK, newID("glass_chute"), GLASS_CHUTE);
		Registry.register(Registry.BLOCK, newID("weak_fan"), WEAK_FAN);
		Registry.register(Registry.BLOCK, newID("strong_fan"), STRONG_FAN);

		Registry.register(Registry.BLOCK_ENTITY_TYPE, newID("golden_hopper"), GOLDEN_HOPPER_TYPE);
		Registry.register(Registry.BLOCK_ENTITY_TYPE, newID("fan"), FAN_TYPE);
	}
}
