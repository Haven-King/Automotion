package hephaestus.dev.automotion.common.block;

import net.minecraft.block.*;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.Properties;
import net.minecraft.tag.FluidTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.WorldView;
import org.jetbrains.annotations.Nullable;

import static net.minecraft.state.property.Properties.WATERLOGGED;

public abstract class DuctAttachmentBlock extends Block implements Waterloggable, FluidDrainable, Connectable {
	public DuctAttachmentBlock(AbstractBlock.Settings settings) {
		super(settings);
		this.setDefaultState(this.getDefaultState().with(Properties.FACING, Direction.NORTH).with(Properties.WATERLOGGED, false));
	}

	@Override
	protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
		builder.add(Properties.FACING, Properties.WATERLOGGED);
	}

	@Override
	public boolean canPlaceAt(BlockState state, WorldView world, BlockPos pos) {
		Direction facing = state.get(Properties.FACING);
		BlockState neighbor = world.getBlockState(pos.offset(facing));
		return neighbor.getBlock() instanceof DuctBlock && ((Connectable)neighbor.getBlock()).canConnect(neighbor, this, facing.getOpposite());
	}

	@Override
	public @Nullable BlockState getPlacementState(ItemPlacementContext ctx) {
		return this.getDefaultState().with(Properties.FACING, ctx.getSide().getOpposite()).with(Properties.WATERLOGGED, ctx.getWorld().getFluidState(ctx.getBlockPos()).isIn(FluidTags.WATER));
	}

	@Override
	public FluidState getFluidState(BlockState state) {
		return state.get(WATERLOGGED) ? Fluids.WATER.getStill(false) : super.getFluidState(state);
	}

	@Override
	public Fluid tryDrainFluid(WorldAccess world, BlockPos pos, BlockState state) {
		if (state.get(WATERLOGGED)) {
			world.setBlockState(pos, state.with(WATERLOGGED, false), 11);
			return Fluids.WATER;
		} else {
			return Fluids.EMPTY;
		}
	}

	@Override
	public void neighborUpdate(BlockState state, World world, BlockPos pos, Block block, BlockPos fromPos, boolean notify) {
		if (!canPlaceAt(state, world, pos)) {
			world.breakBlock(pos, true, null);
			world.updateNeighborsAlways(pos, this);
		}
	}

	public BlockState getStateForNeighborUpdate(BlockState state, Direction direction, BlockState newState, WorldAccess world, BlockPos pos, BlockPos posFrom) {
		if (state.get(WATERLOGGED)) {
			world.getFluidTickScheduler().schedule(pos, Fluids.WATER, Fluids.WATER.getTickRate(world));
		}

		return super.getStateForNeighborUpdate(state, direction, newState, world, pos, posFrom);
	}

	@Override
	public boolean canConnect(BlockState state, Connectable other, Direction direction) {
		return other instanceof DuctBlock;
	}
}
