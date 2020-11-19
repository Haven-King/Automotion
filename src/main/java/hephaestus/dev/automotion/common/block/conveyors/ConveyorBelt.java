package hephaestus.dev.automotion.common.block.conveyors;

import hephaestus.dev.automotion.common.Automotion;
import hephaestus.dev.automotion.common.block.Connectable;
import hephaestus.dev.automotion.common.item.Conveyable;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.ShapeContext;
import net.minecraft.block.Waterloggable;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.StringIdentifiable;
import net.minecraft.util.math.*;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;

import static net.minecraft.state.property.Properties.WATERLOGGED;

public class ConveyorBelt extends Block implements Waterloggable, Connectable {
	protected static final VoxelShape FLAT = Block.createCuboidShape(0, 0, 0, 16, 3, 16);
	protected static final VoxelShape TOP_OUTLINE = Block.createCuboidShape(0, 13, 0, 16, 16, 16);

	protected static final VoxelShape NORTH_DOWN_SOUTH_UP;
	protected static final VoxelShape NORTH_UP_SOUTH_DOWN;
	protected static final VoxelShape EAST_DOWN_WEST_UP;
	protected static final VoxelShape EAST_UP_WEST_DOWN;

	static {
		VoxelShape shape = VoxelShapes.empty();

		for (int i = 0; i < 16; ++i) {
			shape = VoxelShapes.union(shape, Block.createCuboidShape(0, i, i, 16, i + 3, i + 1));
		}

		NORTH_DOWN_SOUTH_UP = shape;

		shape = VoxelShapes.empty();

		for (int i = 0; i < 16; ++i) {
			shape = VoxelShapes.union(shape, Block.createCuboidShape(0, 16 - i, i, 16, 19 - i, i + 1));
		}

		NORTH_UP_SOUTH_DOWN = shape;

		shape = VoxelShapes.empty();

		for (int i = 0; i < 16; ++i) {
			shape = VoxelShapes.union(shape, Block.createCuboidShape(i, 16 - i, 0, i + 1, 19 - i, 16));
		}

		EAST_DOWN_WEST_UP = shape;

		shape = VoxelShapes.empty();

		for (int i = 0; i < 16; ++i) {
			shape = VoxelShapes.union(shape, Block.createCuboidShape(i, i, 0, i + 1, i + 3, 16));
		}

		EAST_UP_WEST_DOWN = shape;
	}

	public static final EnumProperty<Direction> FACING = Properties.HOPPER_FACING;
	public static final EnumProperty<Kind> KIND = EnumProperty.of("kind", Kind.class);
	public static final EnumProperty<CenteringDirection> CENTERING_DIRECTION = EnumProperty.of("centering_direction", CenteringDirection.class);
	public static final EnumProperty<Angle> ANGLE = EnumProperty.of("angle", Angle.class);
	private final double speed;
	public final boolean canSlope;

	public ConveyorBelt(Settings settings, double speed, boolean canSlope) {
		super(settings);
		this.speed = speed;
		this.canSlope = canSlope;

		StateManager.Builder<Block, BlockState> builder = new StateManager.Builder<>(this);
		this.appendProperties(builder);
		this.stateManager = builder.build(Block::getDefaultState, BlockState::new);

		this.setDefaultState(this.stateManager.getDefaultState()
				.with(FACING, Direction.NORTH)
				.with(KIND, Kind.SINGLE)
				.with(Properties.WATERLOGGED, false)
				.with(CENTERING_DIRECTION, CenteringDirection.CENTER)
		);

		if (this.canSlope) {
			this.setDefaultState(this.stateManager.getDefaultState().with(ANGLE, Angle.FLAT));
		}
	}

	@Override
	protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
		builder.add(FACING, Properties.ENABLED, Properties.WATERLOGGED, KIND, CENTERING_DIRECTION);

		if (this.canSlope) {
			builder.add(ANGLE);
		}
	}

	@Override
	public BlockState getPlacementState(ItemPlacementContext ctx) {
		if (ctx == null || ctx.getPlayer() == null) return this.getDefaultState();

		Direction direction = ctx.getSide();
		BlockPos pos = ctx.getBlockPos();

		Direction facing = Automotion.isAlternate(ctx.getPlayer()) ? ctx.getPlayerFacing().getOpposite() : ctx.getPlayerFacing();

		return getAngle(getPartState(this.getDefaultState().with(FACING, facing).with(Properties.ENABLED, true), ctx.getWorld(), ctx.getBlockPos()).with(WATERLOGGED,ctx.getWorld().getFluidState(ctx.getBlockPos()).getFluid() == Fluids.WATER), ctx.getWorld(), pos);
	}

	@Override
	public void onPlaced(World world, BlockPos pos, BlockState state, LivingEntity placer, ItemStack itemStack) {
		this.updateState(world, pos, state);

		BlockPos.Mutable.iterate(pos.down().north(), pos.up().south()).forEach(neighbor -> {
			if (!neighbor.equals(pos)) {
				BlockState neighborState = world.getBlockState(neighbor);

				if (neighborState.getBlock() instanceof ConveyorBelt) {
					neighborState.getBlock().neighborUpdate(neighborState, world, neighbor, state.getBlock(), pos, true);
				}
			}
		});
	}

	@Override
	public BlockState getStateForNeighborUpdate(BlockState state, Direction direction, BlockState newState, WorldAccess world, BlockPos pos, BlockPos posFrom) {
		if (state.get(WATERLOGGED)) {
			world.getFluidTickScheduler().schedule(pos, Fluids.WATER, Fluids.WATER.getTickRate(world));
		}

		return super.getStateForNeighborUpdate(state, direction, newState, world, pos, posFrom);
	}

	@Override
	public FluidState getFluidState(BlockState state) {
		return state.get(WATERLOGGED) ? Fluids.WATER.getStill(false) : super.getFluidState(state);
	}

	@Override
	public void neighborUpdate(BlockState state, World world, BlockPos pos, Block block, BlockPos fromPos, boolean notify) {
		this.updateState(world, pos, state);

		BlockState newState = world.getBlockState(pos);

		if (newState != state) {
			BlockPos.Mutable.iterate(pos.down().north(), pos.up().south()).forEach(neighbor -> {
				if (!neighbor.equals(pos)) {
					BlockState neighborState = world.getBlockState(neighbor);

					if (neighborState.getBlock() instanceof ConveyorBelt) {
						neighborState.getBlock().neighborUpdate(neighborState, world, neighbor, block, pos, notify);
					}
				}
			});
		}
	}

	private static boolean isContinuous(BlockState state, Direction facing) {
		return state.getBlock() instanceof ConveyorBelt && state.get(FACING) == facing;
	}

	private static BlockState getPartState(BlockState state, World world, BlockPos pos) {
		Direction facing = state.get(FACING);

		BlockPos leftPos = pos.offset(facing.rotateYCounterclockwise());
		BlockState leftState = world.getBlockState(leftPos);
		boolean left = (isContinuous(leftState, facing.rotateYClockwise()) || isContinuous(leftState, facing)) && matchesAngle(leftState, state);

		BlockPos rightPos = pos.offset(facing.rotateYClockwise());
		BlockState rightState = world.getBlockState(rightPos);
		boolean right = (isContinuous(rightState, facing.rotateYCounterclockwise()) || isContinuous(rightState, facing)) && matchesAngle(rightState, state);

		state = state.with(KIND, left == right && left ? Kind.MIDDLE : left == right ? Kind.SINGLE : left ? Kind.RIGHT : Kind.LEFT);

		left = isContinuous(leftState, facing) && matchesAngle(leftState, state);
		right = isContinuous(rightState, facing) && matchesAngle(rightState, state);
		if (left && !right)
			return state.with(CENTERING_DIRECTION, CenteringDirection.LEFT);
		else if (right && !left)
			return state.with(CENTERING_DIRECTION, CenteringDirection.RIGHT);
		else if (!left)
			return state.with(CENTERING_DIRECTION, CenteringDirection.CENTER);
		else {
			while (true) {
				leftState = world.getBlockState(leftPos);
				left = isContinuous(leftState, facing) && matchesAngle(leftState, state);

				rightState = world.getBlockState(rightPos);
				right = isContinuous(rightState, facing) && matchesAngle(rightState, state);

				if ((!left) && (!right)) {
					return state.with(CENTERING_DIRECTION, CenteringDirection.CENTER);
				} else if (left && !right) {
					return state.with(CENTERING_DIRECTION, CenteringDirection.LEFT);
				} else if (!left) {
					return state.with(CENTERING_DIRECTION, CenteringDirection.RIGHT);
				}

				leftPos = leftPos.offset(facing.rotateYCounterclockwise());
				rightPos = rightPos.offset(facing.rotateYClockwise());
			}
		}
	}

	private static boolean matchesAngle(BlockState state1, BlockState state2) {
		if (state1.getBlock() instanceof ConveyorBelt && state2.getBlock() instanceof ConveyorBelt) {
			ConveyorBelt belt1 = (ConveyorBelt) state1.getBlock();
			ConveyorBelt belt2 = (ConveyorBelt) state2.getBlock();

			return (belt1.canSlope != belt2.canSlope) || !belt1.canSlope || state1.get(ANGLE) == state2.get(ANGLE);
		}

		return false;
	}

	private BlockState getAngle(BlockState state, World world, BlockPos pos) {
		if (this.canSlope) {
			Direction facing = state.get(FACING);
			BlockState forwardUp = world.getBlockState(pos.offset(facing).offset(Direction.UP));
			BlockState backwards = world.getBlockState(pos.offset(facing.getOpposite()));
			BlockState backwardsDown = world.getBlockState(pos.offset(facing.getOpposite()).offset(Direction.DOWN));
			BlockState forwardDown = world.getBlockState(pos.offset(facing).offset(Direction.DOWN));
			BlockState forwards = world.getBlockState(pos.offset(facing));
			BlockState backwardsUp = world.getBlockState(pos.offset(facing.getOpposite()).offset(Direction.UP));

			if (forwards.getBlock() instanceof ConveyorBelt && backwards.getBlock() instanceof ConveyorBelt && forwards.get(FACING) == facing && backwards.get(FACING) == facing &&
					(!((ConveyorBelt) backwards.getBlock()).canSlope || backwards.get(ANGLE) != Angle.UP) &&
					(!((ConveyorBelt) forwards.getBlock()).canSlope || forwards.get(ANGLE) != Angle.DOWN)) {
				return state.with(ANGLE, Angle.FLAT);
			}

			if (forwardUp.getBlock() instanceof ConveyorBelt && (!forwardUp.get(FACING).equals(facing.getOpposite()) && !world.getBlockState(pos.up()).isSolidBlock(world, pos)) && (!((ConveyorBelt) forwardUp.getBlock()).canSlope || forwardUp.get(ANGLE) != Angle.DOWN) && (
					(backwardsDown.getBlock() instanceof ConveyorBelt && backwardsDown.get(FACING).equals(facing) && !world.getBlockState(pos.offset(facing.getOpposite())).isSolidBlock(world, pos))
					|| (backwards.getBlock() instanceof ConveyorBelt && backwards.get(FACING).equals(facing) && (!((ConveyorBelt) backwards.getBlock()).canSlope || backwards.get(ANGLE) != Angle.UP))
					)) {
				return state.with(ANGLE, Angle.UP);
			}

			if (backwardsUp.getBlock() instanceof ConveyorBelt && backwardsUp.get(FACING).equals(facing) && !world.getBlockState(pos.up()).isSolidBlock(world, pos) && (!((ConveyorBelt) backwardsUp.getBlock()).canSlope || backwardsUp.get(ANGLE) != Angle.UP) && (
					(forwardDown.getBlock() instanceof ConveyorBelt && forwardDown.get(FACING).equals(facing) && !world.getBlockState(pos.offset(facing)).isSolidBlock(world, pos))
					|| (forwards.getBlock() instanceof ConveyorBelt && (!((ConveyorBelt) forwards.getBlock()).canSlope || forwards.get(ANGLE) != Angle.DOWN))
					)) {
				return state.with(ANGLE, Angle.DOWN);
			}

			return state.with(ANGLE, Angle.FLAT);
		}

		return state;
	}

	private static boolean checkSlope(BlockState state, Angle angle) {
		Block block = state.getBlock();

		if (block instanceof ConveyorBelt && ((ConveyorBelt) block).canSlope) {
			return state.get(ANGLE) == angle;
		}

		return angle == Angle.FLAT;
	}

	private void updateState(World world, BlockPos pos, BlockState state) {
		boolean bl = !world.isReceivingRedstonePower(pos);
		if (bl != state.get(Properties.ENABLED)) {
				state = state.with(Properties.ENABLED, bl);
		}

		world.setBlockState(pos, getAngle(getPartState(state, world, pos), world, pos));
	}

	@Override
	public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
		Angle angle;

		if (!this.canSlope || (angle = state.get(ANGLE)) == Angle.FLAT) {
			return FLAT;
		} else {
			final Direction dir = state.get(FACING);

			if (angle == Angle.DOWN && dir == Direction.NORTH || angle == Angle.UP && dir == Direction.SOUTH) {
				return NORTH_DOWN_SOUTH_UP;
			} else if (angle == Angle.DOWN && dir == Direction.SOUTH || angle == Angle.UP && dir == Direction.NORTH) {
				return NORTH_UP_SOUTH_DOWN;
			} else if (angle == Angle.DOWN && dir == Direction.EAST || angle == Angle.UP && dir == Direction.WEST) {
				return EAST_DOWN_WEST_UP;
			} else if (angle == Angle.DOWN && dir == Direction.WEST || angle == Angle.UP && dir == Direction.EAST) {
				return EAST_UP_WEST_DOWN;
			}
		}

		return FLAT;
	}

	private static final double threshold = 0.1D;
	@Override
	public void onEntityCollision(BlockState state, World world, BlockPos pos, Entity entity) {
		if (state.get(Properties.ENABLED) && !entity.canAvoidTraps() && entity.getY() > pos.getY()) {
			if (entity instanceof MobEntity) {
				((MobEntity) entity).goalSelector.disableControl(Goal.Control.MOVE);
				((MobEntity) entity).goalSelector.disableControl(Goal.Control.JUMP);
			}

			Direction facing = state.get(FACING);
			CenteringDirection centeringDirection = state.get(CENTERING_DIRECTION);
			Vec3i facingVector = facing.getVector();
			Vec3d conveyanceVector = Vec3d.of(facingVector);

			double entityCenter;
			double center;
			switch (facing.getAxis()) {
				case Z:
					entityCenter = entity.getX();
					center = pos.getX() + 0.5;
					break;

				case X:
					entityCenter = entity.getZ();
					center = pos.getZ() + 0.5;
					break;

				default:
					throw new IllegalStateException("Unexpected value: " + facing);
			}

			double mod = 0.0D;
			switch(centeringDirection) {
				case LEFT:
					mod -= facing == Direction.NORTH || facing == Direction.EAST ? 0.5D : -0.5D;
					center += mod * 2;
					break;

				case RIGHT:
					mod += facing == Direction.NORTH || facing == Direction.EAST ? 0.5D : -0.5D;
					center += mod * 2;
					break;

				case CENTER:
					if (entityCenter > center) {
						mod -= 0.5D;
					} else {
						mod += 0.5D;
					}
			}

			Vec3d centeringVector = Vec3d.ZERO;

			if (Math.abs(Math.abs(center) - Math.abs(entityCenter)) > threshold) {
				centeringVector = new Vec3d(facing.getAxis() == Direction.Axis.Z ? mod : 0, 0, facing.getAxis() == Direction.Axis.X ? mod : 0);
			}

			centeringVector = centeringVector.normalize().multiply(0.125);
			conveyanceVector = conveyanceVector.normalize().multiply(speed);

			conveyanceVector = conveyanceVector.add(centeringVector);

			if (entity instanceof Conveyable && conveyanceVector.length() > 0.1D) {
				((Conveyable) entity).convey(new Vec3d(conveyanceVector.x, entity.getVelocity() == null ? 0 : entity.getVelocity().y, conveyanceVector.z));
			}
		}

		super.onEntityCollision(state, world, pos, entity);
	}

	@Override
	public boolean canConnect(BlockState state, Connectable other, Direction direction) {
		Direction facing = state.get(FACING);
		return direction == facing || direction == facing.getOpposite();
	}

	public enum Kind implements StringIdentifiable {
		LEFT("left"),
		RIGHT("right"),
		SINGLE("single"),
		MIDDLE("middle");

		private final String kind;
		Kind(String kind) {
			this.kind = kind;
		}

		@Override
		public String asString() {
			return this.kind;
		}
	}

	public enum CenteringDirection implements StringIdentifiable {
		LEFT("left"),
		RIGHT("right"),
		CENTER("center");

		private final String type;

		CenteringDirection(String type) {
			this.type = type;
		}


		@Override
		public String asString() {
			return this.type;
		}
	}

	public enum Angle implements StringIdentifiable {
		UP("up"),
		DOWN("down"),
		FLAT("flat");

		private final String type;

		Angle(String type) {
			this.type = type;
		}

		@Override
		public String asString() {
			return this.type;
		}
	}
}
