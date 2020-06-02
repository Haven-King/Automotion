package hephaestus.dev.automotion.block;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.*;
import net.minecraft.block.enums.StairShape;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ai.pathing.NavigationType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.Properties;
import net.minecraft.util.ActionResult;
import net.minecraft.util.BlockMirror;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.explosion.Explosion;

import java.util.Random;

import static net.minecraft.state.property.Properties.STAIR_SHAPE;
import static net.minecraft.state.property.Properties.WATERLOGGED;

public class WallBlock extends HorizontalFacingBlock implements Waterloggable {
	private static final VoxelShape NORTH_SHAPE = Block.createCuboidShape(0, 0, 0, 16, 16, 2);
	private static final VoxelShape EAST_SHAPE 	= Block.createCuboidShape(14, 0, 0, 16, 16, 16);
	private static final VoxelShape SOUTH_SHAPE = Block.createCuboidShape(0, 0, 14, 16, 16, 16);
	private static final VoxelShape WEST_SHAPE 	= Block.createCuboidShape(0, 0, 0, 2, 16, 16);

	private static final VoxelShape NORTH_WEST_CORNER = Block.createCuboidShape(0,0,0, 2, 16, 2);
	private static final VoxelShape NORTH_EAST_CORNER = Block.createCuboidShape(14, 0, 0, 16, 16, 2);
	private static final VoxelShape SOUTH_EAST_CORNER = Block.createCuboidShape(14, 0, 14, 16, 16, 16);
	private static final VoxelShape SOUTH_WEST_CORNER = Block.createCuboidShape(0, 0, 14, 2, 16, 16);

	private final Block baseBlock;
	private final BlockState baseBlockState;

	public WallBlock(BlockState baseBlockState, AbstractBlock.Settings settings) {
		super(settings);
		this.setDefaultState(this.stateManager.getDefaultState().with(FACING, Direction.NORTH).with(WATERLOGGED, false).with(STAIR_SHAPE, StairShape.STRAIGHT));
		this.baseBlock = baseBlockState.getBlock();
		this.baseBlockState = baseBlockState;
	}

	@Override
	public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
		StairShape shape = state.get(STAIR_SHAPE);
		Direction facing = state.get(FACING);

		if (shape == StairShape.STRAIGHT) {
			switch (facing) {
				case NORTH: return NORTH_SHAPE;
				case WEST: return WEST_SHAPE;
				case EAST: return EAST_SHAPE;
				case SOUTH: return SOUTH_SHAPE;
			}
		}

		if (facing == Direction.EAST) {
			switch (shape) {
				case OUTER_LEFT:	return NORTH_EAST_CORNER;
				case OUTER_RIGHT:	return SOUTH_EAST_CORNER;

				case INNER_LEFT:	return VoxelShapes.union(NORTH_SHAPE, EAST_SHAPE);
				case INNER_RIGHT:	return VoxelShapes.union(SOUTH_SHAPE, EAST_SHAPE);
			}
		}

		if (facing == Direction.WEST) {
			switch (shape) {
				case OUTER_LEFT:	return SOUTH_WEST_CORNER;
				case OUTER_RIGHT:	return NORTH_WEST_CORNER;

				case INNER_LEFT:	return VoxelShapes.union(SOUTH_SHAPE, WEST_SHAPE);
				case INNER_RIGHT:	return VoxelShapes.union(NORTH_SHAPE, WEST_SHAPE);
			}
		}

		if (facing == Direction.NORTH) {
			switch (shape) {
				case OUTER_LEFT:	return NORTH_WEST_CORNER;
				case OUTER_RIGHT:	return NORTH_EAST_CORNER;

				case INNER_LEFT:	return VoxelShapes.union(NORTH_SHAPE, WEST_SHAPE);
				case INNER_RIGHT:	return VoxelShapes.union(NORTH_SHAPE, EAST_SHAPE);
			}
		}

		if (facing == Direction.SOUTH) {
			switch (shape) {
				case OUTER_LEFT:	return SOUTH_EAST_CORNER;
				case OUTER_RIGHT:	return SOUTH_WEST_CORNER;

				case INNER_LEFT:	return VoxelShapes.union(SOUTH_SHAPE, EAST_SHAPE);
				case INNER_RIGHT:	return VoxelShapes.union(SOUTH_SHAPE, WEST_SHAPE);
			}
		}

		return NORTH_SHAPE;
	}

	public boolean hasSidedTransparency(BlockState state) {
		return true;
	}

	@Environment(EnvType.CLIENT)
	public void randomDisplayTick(BlockState state, World world, BlockPos pos, Random random) {
		this.baseBlock.randomDisplayTick(state, world, pos, random);
	}

	public void onBlockBreakStart(BlockState state, World world, BlockPos pos, PlayerEntity player) {
		this.baseBlockState.onBlockBreakStart(world, pos, player);
	}

	public void onBroken(WorldAccess world, BlockPos pos, BlockState state) {
		this.baseBlock.onBroken(world, pos, state);
	}

	public float getBlastResistance() {
		return this.baseBlock.getBlastResistance();
	}

	public void onBlockAdded(BlockState state, World world, BlockPos pos, BlockState oldState, boolean notify) {
		if (!state.isOf(state.getBlock())) {
			this.baseBlockState.neighborUpdate(world, pos, Blocks.AIR, pos, false);
			this.baseBlock.onBlockAdded(this.baseBlockState, world, pos, oldState, false);
		}
	}

	public void onStateReplaced(BlockState state, World world, BlockPos pos, BlockState newState, boolean notify) {
		if (!state.isOf(newState.getBlock())) {
			this.baseBlockState.onStateReplaced(world, pos, newState, notify);
		}
	}

	public void onSteppedOn(World world, BlockPos pos, Entity entity) {
		this.baseBlock.onSteppedOn(world, pos, entity);
	}

	public boolean hasRandomTicks(BlockState state) {
		return this.baseBlock.hasRandomTicks(state);
	}

	public void randomTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
		this.baseBlock.randomTick(state, world, pos, random);
	}

	public void scheduledTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
		this.baseBlock.scheduledTick(state, world, pos, random);
	}

	public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
		return this.baseBlockState.onUse(world, player, hand, hit);
	}

	public void onDestroyedByExplosion(World world, BlockPos pos, Explosion explosion) {
		this.baseBlock.onDestroyedByExplosion(world, pos, explosion);
	}

	public BlockState getPlacementState(ItemPlacementContext ctx) {
		BlockPos blockPos = ctx.getBlockPos();
		FluidState fluidState = ctx.getWorld().getFluidState(blockPos);

		Direction facing = ctx.getPlayerFacing();

		Vec3d hitPos = ctx.getHitPos();
		double t;
		switch (facing) {
			case EAST:
				t = Math.abs(hitPos.getX() % 1);
				facing = t >= 0.5D ? facing : facing.getOpposite();
				break;

			case NORTH:
				t = Math.abs(hitPos.getZ() % 1);
				facing = t >= 0.5D ? facing.getOpposite() : facing;
				break;

			case WEST:
				t = Math.abs(hitPos.getX() % 1);
				facing = t >= 0.5D ? facing.getOpposite() : facing;
				break;

			case SOUTH:
				t = Math.abs(hitPos.getZ() % 1);
				facing = t >= 0.5D ? facing : facing.getOpposite();
				break;
		}

		BlockState blockState = this.getDefaultState().with(FACING, facing).with(WATERLOGGED, fluidState.getFluid() == Fluids.WATER);
		return blockState.with(Properties.STAIR_SHAPE, getWallShape(blockState, ctx.getWorld(), blockPos));
	}

	@Override
	public BlockState getStateForNeighborUpdate(BlockState state, Direction direction, BlockState newState, WorldAccess world, BlockPos pos, BlockPos posFrom) {
		if (state.get(WATERLOGGED)) {
			world.getFluidTickScheduler().schedule(pos, Fluids.WATER, Fluids.WATER.getTickRate(world));
		}

		return direction.getAxis().isHorizontal() ? state.with(Properties.STAIR_SHAPE, getWallShape(state, world, pos)) : super.getStateForNeighborUpdate(state, direction, newState, world, pos, posFrom);
	}

	private static boolean method_10678(BlockState state, BlockView world, BlockPos pos, Direction dir) {
		BlockState blockState = world.getBlockState(pos.offset(dir));
		return !(blockState.getBlock() instanceof WallBlock) || blockState.get(FACING) != state.get(FACING);
	}

	private StairShape getWallShape(BlockState state, BlockView world, BlockPos pos) {
		Direction direction = state.get(FACING);
		BlockState blockState = world.getBlockState(pos.offset(direction));
		if (blockState.getBlock() instanceof WallBlock) {
			Direction direction2 = blockState.get(FACING);
			if (direction2.getAxis() != state.get(FACING).getAxis() && method_10678(state, world, pos, direction2.getOpposite())) {
				if (direction2 == direction.rotateYCounterclockwise()) {
					return StairShape.OUTER_LEFT;
				}

				return StairShape.OUTER_RIGHT;
			}
		}

		BlockState blockState2 = world.getBlockState(pos.offset(direction.getOpposite()));
		if (blockState2.getBlock() instanceof WallBlock) {
			Direction direction3 = blockState2.get(FACING);
			if (direction3.getAxis() != state.get(FACING).getAxis() && method_10678(state, world, pos, direction3)) {
				if (direction3 == direction.rotateYCounterclockwise()) {
					return StairShape.INNER_LEFT;
				}

				return StairShape.INNER_RIGHT;
			}
		}

		return StairShape.STRAIGHT;
	}

	public BlockState rotate(BlockState state, BlockRotation rotation) {
		return state.with(FACING, rotation.rotate(state.get(FACING)));
	}

	public BlockState mirror(BlockState state, BlockMirror mirror) {
		Direction direction = state.get(FACING);
		StairShape stairShape = state.get(STAIR_SHAPE);
		switch(mirror) {
			case LEFT_RIGHT:
				if (direction.getAxis() == Direction.Axis.Z) {
					switch(stairShape) {
						case INNER_LEFT:
							return state.rotate(BlockRotation.CLOCKWISE_180).with(STAIR_SHAPE, StairShape.INNER_RIGHT);
						case INNER_RIGHT:
							return state.rotate(BlockRotation.CLOCKWISE_180).with(STAIR_SHAPE, StairShape.INNER_LEFT);
						case OUTER_LEFT:
							return state.rotate(BlockRotation.CLOCKWISE_180).with(STAIR_SHAPE, StairShape.OUTER_RIGHT);
						case OUTER_RIGHT:
							return state.rotate(BlockRotation.CLOCKWISE_180).with(STAIR_SHAPE, StairShape.OUTER_LEFT);
						default:
							return state.rotate(BlockRotation.CLOCKWISE_180);
					}
				}
				break;
			case FRONT_BACK:
				if (direction.getAxis() == Direction.Axis.X) {
					switch(stairShape) {
						case INNER_LEFT:
							return state.rotate(BlockRotation.CLOCKWISE_180).with(STAIR_SHAPE, StairShape.INNER_LEFT);
						case INNER_RIGHT:
							return state.rotate(BlockRotation.CLOCKWISE_180).with(STAIR_SHAPE, StairShape.INNER_RIGHT);
						case OUTER_LEFT:
							return state.rotate(BlockRotation.CLOCKWISE_180).with(STAIR_SHAPE, StairShape.OUTER_RIGHT);
						case OUTER_RIGHT:
							return state.rotate(BlockRotation.CLOCKWISE_180).with(STAIR_SHAPE, StairShape.OUTER_LEFT);
						case STRAIGHT:
							return state.rotate(BlockRotation.CLOCKWISE_180);
					}
				}
		}

		return super.mirror(state, mirror);
	}

	protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
		builder.add(FACING, STAIR_SHAPE, WATERLOGGED);
	}

	public FluidState getFluidState(BlockState state) {
		return state.get(WATERLOGGED) ? Fluids.WATER.getStill(false) : super.getFluidState(state);
	}

	public boolean canPathfindThrough(BlockState state, BlockView world, BlockPos pos, NavigationType type) {
		return false;
	}
}
