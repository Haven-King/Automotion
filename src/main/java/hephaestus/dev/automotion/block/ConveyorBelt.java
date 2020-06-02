package hephaestus.dev.automotion.block;

import hephaestus.dev.automotion.Automotion;
import net.minecraft.block.*;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.LivingEntity;
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
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;

import static net.minecraft.state.property.Properties.WATERLOGGED;

public class ConveyorBelt extends HorizontalFacingBlock implements Waterloggable {
	protected static final VoxelShape BOTTOM_OUTLINE = Block.createCuboidShape(0, 0, 0, 16, 3, 16);
	protected static final VoxelShape TOP_OUTLINE = Block.createCuboidShape(0, 13, 0, 16, 16, 16);

	protected static final VoxelShape BOTTOM_COLLISION = Block.createCuboidShape(0, -0.0625, 0, 16, 3.0625, 16);
	protected static final VoxelShape TOP_COLLISION = Block.createCuboidShape(0, 12.9375, 0, 16, 16.0625, 16);

	public static final EnumProperty<Kind> KIND = EnumProperty.of("kind", Kind.class);
	public static final EnumProperty<CenteringDirection> CENTERING_DIRECTION = EnumProperty.of("centering_direction", CenteringDirection.class);
	private final double speed;

	public ConveyorBelt(Settings settings, double speed) {
		super(settings);
		this.setDefaultState(this.stateManager.getDefaultState().with(FACING, Direction.NORTH).with(KIND, Kind.SINGLE).with(Properties.WATERLOGGED, false).with(CENTERING_DIRECTION, CenteringDirection.CENTER).with(Properties.BOTTOM, true));
		this.speed = speed;
	}

	@Override
	protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
		builder.add(FACING, Properties.ENABLED, Properties.WATERLOGGED, KIND, CENTERING_DIRECTION, Properties.BOTTOM);
	}

	@Override
	public BlockState getPlacementState(ItemPlacementContext ctx) {
		Direction direction = ctx.getSide();
		BlockPos blockPos = ctx.getBlockPos();

		Direction facing = Automotion.isAlternate(ctx.getPlayer()) ? ctx.getPlayerFacing().getOpposite() : ctx.getPlayerFacing();

		return getPartState(this.getDefaultState().with(FACING, facing).with(Properties.ENABLED, true), ctx.getWorld(), ctx.getBlockPos()).with(Properties.BOTTOM, direction != Direction.DOWN && (direction == Direction.UP || ctx.getHitPos().y - (double) blockPos.getY() <= 0.5D));
	}

	@Override
	public void onPlaced(World world, BlockPos pos, BlockState state, LivingEntity placer, ItemStack itemStack) {
		this.updateState(world, pos, state);
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
	}

	private static BlockState getPartState(BlockState state, World world, BlockPos pos) {
		Direction facing = state.get(FACING);

		BlockPos leftPos = pos.offset(facing.rotateYCounterclockwise());
		BlockState leftState = world.getBlockState(leftPos);
		boolean left = leftState.getBlock() instanceof ConveyorBelt && (leftState.get(FACING) == facing.rotateYClockwise() || leftState.get(FACING) == facing) && leftState.get(Properties.BOTTOM) == state.get(Properties.BOTTOM);

		BlockPos rightPos = pos.offset(facing.rotateYClockwise());
		BlockState rightState = world.getBlockState(rightPos);
		boolean right = rightState.getBlock() instanceof ConveyorBelt && (rightState.get(FACING) == facing.rotateYCounterclockwise() || rightState.get(FACING) == facing) && rightState.get(Properties.BOTTOM) == state.get(Properties.BOTTOM);

		state = state.with(KIND, left == right && left ? Kind.MIDDLE : left == right ? Kind.SINGLE : left ? Kind.RIGHT : Kind.LEFT);

		left = leftState.getBlock() instanceof ConveyorBelt && leftState.get(FACING) == facing && leftState.get(Properties.BOTTOM) == state.get(Properties.BOTTOM);
		right = rightState.getBlock() instanceof ConveyorBelt && rightState.get(FACING) == facing && rightState.get(Properties.BOTTOM) == state.get(Properties.BOTTOM);
		if (left && !right)
			return state.with(CENTERING_DIRECTION, CenteringDirection.LEFT);
		else if (right && !left)
			return state.with(CENTERING_DIRECTION, CenteringDirection.RIGHT);
		else if (!left)
			return state.with(CENTERING_DIRECTION, CenteringDirection.CENTER);
		else {
			while (true) {
				leftState = world.getBlockState(leftPos);
				left = leftState.getBlock() instanceof ConveyorBelt && leftState.get(FACING) == state.get(FACING) && leftState.get(Properties.BOTTOM) == state.get(Properties.BOTTOM);

				rightState = world.getBlockState(rightPos);
				right = rightState.getBlock() instanceof ConveyorBelt && rightState.get(FACING) == state.get(FACING) && rightState.get(Properties.BOTTOM) == state.get(Properties.BOTTOM);

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

	private void updateState(World world, BlockPos pos, BlockState state) {
		boolean bl = !world.isReceivingRedstonePower(pos);
		if (bl != state.get(Properties.ENABLED)) {
				state = state.with(Properties.ENABLED, bl);
		}

		world.setBlockState(pos, getPartState(state, world, pos));
	}

	@Override
	public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
		if (state.get(Properties.BOTTOM)) {
			return BOTTOM_OUTLINE;
		} else {
			return TOP_OUTLINE;
		}
	}

	@Override
	public VoxelShape getCollisionShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
		if (state.get(Properties.BOTTOM)) {
			return BOTTOM_COLLISION;
		} else {
			return TOP_COLLISION;
		}
	}

	private static final double threshold = 0.1D;
	@Override
	public void onEntityCollision(BlockState state, World world, BlockPos pos, Entity entity) {
		if (state.get(Properties.ENABLED) && !entity.canAvoidTraps()) {
			Direction facing = entity.getY() > pos.getY() + 0.5 ? state.get(FACING).getOpposite() : state.get(FACING);
			Box box = entity.getBoundingBox().expand(
				facing == Direction.NORTH || facing == Direction.SOUTH ? -0.25 : 0,
				0,
				facing == Direction.EAST || facing == Direction.WEST ? -0.25 : 0
			);

			int i = MathHelper.floor(box.minX);
			int j = MathHelper.ceil(box.maxX);
			int k = MathHelper.floor(box.minY);
			int l = MathHelper.ceil(box.maxY);
			int m = MathHelper.floor(box.minZ);
			int n = MathHelper.ceil(box.maxZ);


			if (world.isRegionLoaded(i, k, m, j, l, n)) {
				Vec3d conveyanceVector = Vec3d.ZERO;
				Vec3d centeringVector = Vec3d.ZERO;

				BlockPos.Mutable mutable = new BlockPos.Mutable();

				for (int p = i; p < j; ++p) {
					for (int q = k; q < l; ++q) {
						for (int r = m; r < n; ++r) {
							mutable.set(p, q, r);

							if (world.getBlockState(mutable).getBlock() instanceof ConveyorBelt) {
								facing = world.getBlockState(mutable).get(FACING);
								CenteringDirection centeringDirection = world.getBlockState(mutable).get(CENTERING_DIRECTION);
								Vec3i facingVector = facing.getVector();
								conveyanceVector = conveyanceVector.add(Vec3d.of(facingVector));

								double entityCenter;
								double center;
								switch (facing.getAxis()) {
									case Z:
										entityCenter = entity.getX();
										center = mutable.getX() + 0.5;
										break;

									case X:
										entityCenter = entity.getZ();
										center = mutable.getZ() + 0.5;
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


								if (Math.abs(Math.abs(center) - Math.abs(entityCenter)) > threshold)
									centeringVector = centeringVector.add(facing.getAxis() == Direction.Axis.Z ? mod : 0, 0, facing.getAxis() == Direction.Axis.X ? mod : 0);
							}
						}
					}
				}

				centeringVector = centeringVector.normalize().multiply(0.125);
				conveyanceVector = conveyanceVector.normalize().multiply(speed).add(centeringVector);

				if (conveyanceVector.length() > 0.0D) {
					if (entity instanceof ItemEntity) {
						((ItemEntity) entity).setPickupDelay(10);
					}

					entity.setVelocity(new Vec3d(conveyanceVector.x, entity.getVelocity().y, conveyanceVector.z));
				}
			}
		}

		super.onEntityCollision(state, world, pos, entity);
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
}
