package hephaestus.dev.automotion.common.block;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

import static net.minecraft.state.property.Properties.*;
import static net.minecraft.state.property.Properties.DOWN;

public class GlassDuctBlock extends DuctBlock {
	public GlassDuctBlock(Block block) {
		super(block);
	}

	protected BlockState makeConnections(BlockState state, World world, BlockPos pos) {
		state = state.with(NORTH, !(world.getBlockState(pos.north()).getBlock() instanceof DuctBlock));

		state = state.with(SOUTH, !(world.getBlockState(pos.south()).getBlock() instanceof DuctBlock));

		state = state.with(EAST, !(world.getBlockState(pos.east()).getBlock() instanceof DuctBlock));

		state = state.with(WEST, !((world.getBlockState(pos.west()).getBlock() instanceof DuctBlock)));

		state = state.with(UP, !((world.getBlockState(pos.up()).getBlock() instanceof DuctBlock)));

		state = state.with(DOWN, !((world.getBlockState(pos.down()).getBlock() instanceof DuctBlock)));

		return state;
	}

	@Override
	public boolean canConnect(BlockState state, Connectable other, Direction direction) {
		return other instanceof DuctBlock;
	}
}
