package hephaestus.dev.automotion.common.block.transportation;

import hephaestus.dev.automotion.common.block.Connectable;
import hephaestus.dev.automotion.common.block.transportation.DuctBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

public class GlassDuctBlock extends DuctBlock {
	public GlassDuctBlock(Block block) {
		super(block);
	}

	protected BlockState makeConnections(BlockState state, World world, BlockPos pos) {
		state = state.with(Properties.NORTH, !(world.getBlockState(pos.north()).getBlock() instanceof DuctBlock));

		state = state.with(Properties.SOUTH, !(world.getBlockState(pos.south()).getBlock() instanceof DuctBlock));

		state = state.with(Properties.EAST, !(world.getBlockState(pos.east()).getBlock() instanceof DuctBlock));

		state = state.with(Properties.WEST, !((world.getBlockState(pos.west()).getBlock() instanceof DuctBlock)));

		state = state.with(Properties.UP, !((world.getBlockState(pos.up()).getBlock() instanceof DuctBlock)));

		state = state.with(Properties.DOWN, !((world.getBlockState(pos.down()).getBlock() instanceof DuctBlock)));

		return state;
	}

	@Override
	public boolean canConnect(BlockState state, Connectable other, Direction direction) {
		return other instanceof DuctBlock;
	}
}
