package hephaestus.dev.automotion.common.block.conveyors;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;

import java.util.Random;

public class DetectorConveyorBelt extends ConveyorBelt {
	public DetectorConveyorBelt(Settings settings, double speed) {
		super(settings, speed);
		this.setDefaultState(this.getDefaultState().with(Properties.POWERED, false).with(Properties.BOTTOM, true));
	}

	@Override
	protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
		super.appendProperties(builder);
		builder.add(Properties.POWERED);
	}

	@Override
	public void onEntityCollision(BlockState state, World world, BlockPos pos, Entity entity) {
		world.setBlockState(pos, state.with(Properties.POWERED, true));
		world.updateNeighbor(pos, this, pos.down());
		world.getBlockTickScheduler().schedule(new BlockPos(pos), this, this.getTickRate());
		super.onEntityCollision(state, world, pos, entity);
	}

	@Override
	public int getWeakRedstonePower(BlockState state, BlockView world, BlockPos pos, Direction direction) {
		return state.get(Properties.POWERED) && direction == Direction.UP ? 15 : 0;
	}

	@Override
	public int getStrongRedstonePower(BlockState state, BlockView world, BlockPos pos, Direction direction) {
		return state.get(Properties.POWERED) && direction == Direction.UP ? 15 : 0;
	}

	@Override
	public boolean emitsRedstonePower(BlockState state) {
		return true;
	}

	protected int getTickRate() {
		return 5;
	}

	public void scheduledTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
		if (state.get(Properties.POWERED)) {
			if (world.getEntitiesByClass(null, new Box(pos), e -> true).isEmpty()) {
				world.setBlockState(pos, state.with(Properties.POWERED, false));
			} else {
				world.getBlockTickScheduler().schedule(new BlockPos(pos), this, this.getTickRate());
			}
		}

		world.updateNeighbor(pos, this, pos.down());
	}
}
