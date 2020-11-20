package hephaestus.dev.automotion.common.block.transportation;

import hephaestus.dev.automotion.common.block.DuctAttachmentBlock;
import hephaestus.dev.automotion.common.block.transportation.DuctBlock;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.ShapeContext;
import net.minecraft.state.property.Properties;
import net.minecraft.util.function.BooleanBiFunction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;

public class DuctOpeningBlock extends DuctAttachmentBlock {
	public static final VoxelShape UP = VoxelShapes.combine(DuctBlock.UP, DuctBlock.FRAME, BooleanBiFunction.AND);
	public static final VoxelShape DOWN = VoxelShapes.combine(DuctBlock.DOWN, DuctBlock.FRAME, BooleanBiFunction.AND);
	public static final VoxelShape NORTH = VoxelShapes.combine(DuctBlock.NORTH, DuctBlock.FRAME, BooleanBiFunction.AND);
	public static final VoxelShape SOUTH = VoxelShapes.combine(DuctBlock.SOUTH, DuctBlock.FRAME, BooleanBiFunction.AND);
	public static final VoxelShape EAST = VoxelShapes.combine(DuctBlock.EAST, DuctBlock.FRAME, BooleanBiFunction.AND);
	public static final VoxelShape WEST = VoxelShapes.combine(DuctBlock.WEST, DuctBlock.FRAME, BooleanBiFunction.AND);

	public DuctOpeningBlock(Block block) {
		super(FabricBlockSettings.copyOf(block).nonOpaque());
	}

	@Override
	public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
		switch (state.get(Properties.FACING)) {
			case DOWN:	return DOWN;
			case UP:	return UP;
			case NORTH:	return NORTH;
			case SOUTH: return SOUTH;
			case WEST:	return WEST;
			case EAST:	return EAST;
			default: throw new IllegalStateException("Unexpected value: " + state.get(Properties.FACING));
		}
	}

	@Override
	public VoxelShape getCollisionShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
		switch (state.get(Properties.FACING)) {
			case DOWN:	return DOWN;
			case UP:	return UP;
			case NORTH:	return NORTH;
			case SOUTH: return SOUTH;
			case WEST:	return WEST;
			case EAST:	return EAST;
			default: throw new IllegalStateException("Unexpected value: " + state.get(Properties.FACING));
		}
	}
}
