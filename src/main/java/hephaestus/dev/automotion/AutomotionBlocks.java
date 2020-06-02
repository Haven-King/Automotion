package hephaestus.dev.automotion;

import hephaestus.dev.automotion.block.*;
import hephaestus.dev.automotion.block.entity.GoldenHopperBlockEntity;
import hephaestus.dev.automotion.block.entity.UpperBlockEntity;
import hephaestus.dev.automotion.item.ConveyanceBlockItem;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.Material;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.state.property.Properties;
import net.minecraft.util.registry.Registry;

import static hephaestus.dev.automotion.Automotion.newID;

public class AutomotionBlocks {
	public static final Block CONVEYOR_BELT = new ConveyorBelt(FabricBlockSettings.of(Material.METAL), 0.125);
	public static final BlockItem CONVEYOR_BELT_ITEM = new ConveyanceBlockItem(CONVEYOR_BELT, new Item.Settings().group(ItemGroup.MISC));

	public static final Block GOLDEN_CONVEYOR_BELT = new ConveyorBelt(FabricBlockSettings.of(Material.METAL), 0.5);
	public static final BlockItem GOLDEN_CONVEYOR_BELT_ITEM = new ConveyanceBlockItem(GOLDEN_CONVEYOR_BELT, new Item.Settings().group(ItemGroup.MISC));

	public static final Block DETECTOR_CONVEYOR_BELT = new DetectorConveyorBelt(FabricBlockSettings.of(Material.METAL), 0.125);
	public static final BlockItem DETECTOR_CONVEYOR_BELT_ITEM = new ConveyanceBlockItem(DETECTOR_CONVEYOR_BELT, new Item.Settings().group(ItemGroup.MISC));

	public static final Block HEATED_CONVEYOR_BELT = new HeatedConveyorBelt(FabricBlockSettings.of(Material.METAL).lightLevel((state) -> state.get(Properties.POWERED) ? 15 : 0), 0.125);
	public static final BlockItem HEATED_CONVEYOR_BELT_ITEM = new ConveyanceBlockItem(HEATED_CONVEYOR_BELT, new Item.Settings().group(ItemGroup.MISC));


	public static final Block STONE_WALL = new WallBlock(Blocks.STONE.getDefaultState(), AbstractBlock.Settings.copy(Blocks.STONE));
	public static final BlockItem STONE_WALL_ITEM = new ConveyanceBlockItem(STONE_WALL, new Item.Settings().group(ItemGroup.DECORATIONS));

	public static final Block IRON_WALL = new WallBlock(Blocks.IRON_BLOCK.getDefaultState(), AbstractBlock.Settings.copy(Blocks.IRON_BLOCK));
	public static final BlockItem IRON_WALL_ITEM = new ConveyanceBlockItem(IRON_WALL, new Item.Settings().group(ItemGroup.DECORATIONS));

	public static final Block GLASS_WALL = new WallBlock(Blocks.GLASS.getDefaultState(), AbstractBlock.Settings.copy(Blocks.GLASS).nonOpaque());
	public static final BlockItem GLASS_WALL_ITEM = new ConveyanceBlockItem(GLASS_WALL, new Item.Settings().group(ItemGroup.DECORATIONS));


	public static final Block GOLDEN_HOPPER = new GoldenHopper(FabricBlockSettings.of(Material.METAL));
	public static final BlockItem GOLDEN_HOPPER_ITEM = new BlockItem(GOLDEN_HOPPER, new Item.Settings().group(ItemGroup.DECORATIONS));
	public static final BlockEntityType<GoldenHopperBlockEntity> GOLDEN_HOPPER_TYPE = BlockEntityType.Builder.create(GoldenHopperBlockEntity::new, GOLDEN_HOPPER).build(null);

	public static final Block UPPER = new UpperBlock(FabricBlockSettings.of(Material.METAL));
	public static final BlockEntityType<UpperBlockEntity> UPPER_TYPE = BlockEntityType.Builder.create(UpperBlockEntity::new, UPPER).build(null);

	public static void init() {
		Registry.register(Registry.BLOCK, newID("conveyor_belt"), CONVEYOR_BELT);
		Registry.register(Registry.ITEM, newID("conveyor_belt"), CONVEYOR_BELT_ITEM);

		Registry.register(Registry.BLOCK, newID("golden_conveyor_belt"), GOLDEN_CONVEYOR_BELT);
		Registry.register(Registry.ITEM, newID("golden_conveyor_belt"), GOLDEN_CONVEYOR_BELT_ITEM);

		Registry.register(Registry.BLOCK, newID("detector_conveyor_belt"), DETECTOR_CONVEYOR_BELT);
		Registry.register(Registry.ITEM, newID("detector_conveyor_belt"), DETECTOR_CONVEYOR_BELT_ITEM);

		Registry.register(Registry.BLOCK, newID("heated_conveyor_belt"), HEATED_CONVEYOR_BELT);
		Registry.register(Registry.ITEM, newID("heated_conveyor_belt"), HEATED_CONVEYOR_BELT_ITEM);


		Registry.register(Registry.BLOCK, newID("stone_wall"), STONE_WALL);
		Registry.register(Registry.ITEM, newID("stone_wall"), STONE_WALL_ITEM);

		Registry.register(Registry.BLOCK, newID("iron_wall"), IRON_WALL);
		Registry.register(Registry.ITEM, newID("iron_wall"), IRON_WALL_ITEM);

		Registry.register(Registry.BLOCK, newID("glass_wall"), GLASS_WALL);
		Registry.register(Registry.ITEM, newID("glass_wall"), GLASS_WALL_ITEM);


		Registry.register(Registry.BLOCK, newID("golden_hopper"), GOLDEN_HOPPER);
		Registry.register(Registry.ITEM, newID("golden_hopper"), GOLDEN_HOPPER_ITEM);

		Registry.register(Registry.BLOCK_ENTITY_TYPE, newID("golden_hopper"), GOLDEN_HOPPER_TYPE);
		Registry.register(Registry.BLOCK_ENTITY_TYPE, newID("upper"), UPPER_TYPE);
	}
}
