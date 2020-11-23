package hephaestus.dev.automotion.common.block.transportation;

import hephaestus.dev.automotion.common.block.Connectable;
import hephaestus.dev.automotion.common.util.BitField;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.minecraft.block.*;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.StringIdentifiable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import static net.minecraft.state.property.Properties.WATERLOGGED;

public class DuctBlock extends Block implements Waterloggable, FluidDrainable, Connectable {
	public static final EnumProperty<Drag> DRAG = EnumProperty.of("drag", Drag.class);

	public static final VoxelShape FRAME = VoxelShapes.union(
			createCuboidShape(0, 0, 0, 1, 1, 16),
			createCuboidShape(15, 0, 0, 16, 1, 16),
			createCuboidShape(15, 15, 0, 16, 16, 16),
			createCuboidShape(0, 15, 0, 1, 16, 16),
			createCuboidShape(1, 0, 0, 15, 1, 1),
			createCuboidShape(1, 0, 15, 15, 1, 16),
			createCuboidShape(1, 15, 15, 15, 16, 16),
			createCuboidShape(1, 15, 0, 15, 16, 1),
			createCuboidShape(0, 1, 0, 1, 15, 1),
			createCuboidShape(15, 1, 0, 16, 15, 1),
			createCuboidShape(15, 1, 15, 16, 15, 16),
			createCuboidShape(0, 1, 15, 1, 15, 16)
	);

	public static final VoxelShape NORTH = createCuboidShape(0, 0, 0, 16, 16, 1);
	public static final VoxelShape SOUTH = createCuboidShape(0, 0, 15, 16, 16, 16);
	public static final VoxelShape EAST = createCuboidShape(15, 0, 0, 16, 16, 16);
	public static final VoxelShape WEST = createCuboidShape(0, 0, 0, 1, 16, 16);
	public static final VoxelShape UP = createCuboidShape(0, 15, 0, 16, 16, 16);
	public static final VoxelShape DOWN = createCuboidShape(0, 0, 0, 16, 1, 16);

	private static final Map<BitField, VoxelShape> SHAPES = new HashMap<>();

	private static VoxelShape of(BitField states) {
		return SHAPES.computeIfAbsent(states, s -> {
			VoxelShape shape = FRAME;

			if (s.get(0))
				shape = VoxelShapes.union(shape, NORTH);

			if (s.get(1))
				shape = VoxelShapes.union(shape, SOUTH);

			if (s.get(2))
				shape = VoxelShapes.union(shape, EAST);

			if (s.get(3))
				shape = VoxelShapes.union(shape, WEST);

			if (s.get(4))
				shape = VoxelShapes.union(shape, UP);

			if (s.get(5))
				shape = VoxelShapes.union(shape, DOWN);


			return shape;
		});
	}

	private static VoxelShape of(BlockState state) {
		BitField states = new BitField(0b111111);

		states.set(0, state.get(Properties.NORTH));
		states.set(1, state.get(Properties.SOUTH));
		states.set(2, state.get(Properties.EAST));
		states.set(3, state.get(Properties.WEST));
		states.set(4, state.get(Properties.UP));
		states.set(5, state.get(Properties.DOWN));

		return of(states);
	}

	public DuctBlock(Block block) {
		super(FabricBlockSettings.copyOf(block).nonOpaque());
		this.setDefaultState(this.getDefaultState()
				.with(Properties.WATERLOGGED, false)
				.with(DRAG, Drag.NONE)
				.with(Properties.NORTH, true)
				.with(Properties.SOUTH, true)
				.with(Properties.EAST, true)
				.with(Properties.WEST, true)
				.with(Properties.UP, true)
				.with(Properties.DOWN, true)
		);
	}

	@Override
	protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
		super.appendProperties(builder);
		builder.add(
				Properties.WATERLOGGED,
				DRAG,
				Properties.NORTH,
				Properties.SOUTH,
				Properties.EAST,
				Properties.WEST,
				Properties.UP,
				Properties.DOWN
		);
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
	public BlockState getPlacementState(ItemPlacementContext ctx) {
		return checkDrag(makeConnections(this.getDefaultState(), ctx.getWorld(), ctx.getBlockPos()).with(WATERLOGGED,ctx.getWorld().getFluidState(ctx.getBlockPos()).getFluid() == Fluids.WATER), ctx.getWorld(), ctx.getBlockPos());
	}

	protected BlockState makeConnections(BlockState state, World world, BlockPos pos) {
		BlockState neighbor;
		state = state.with(Properties.NORTH, !((neighbor = world.getBlockState(pos.north())).getBlock() instanceof Connectable &&
				((Connectable)neighbor.getBlock()).canConnect(neighbor, this, Direction.SOUTH)));

		state = state.with(Properties.SOUTH, !((neighbor = world.getBlockState(pos.south())).getBlock() instanceof Connectable &&
				((Connectable)neighbor.getBlock()).canConnect(neighbor, this, Direction.NORTH)));

		state = state.with(Properties.EAST, !((neighbor = world.getBlockState(pos.east())).getBlock() instanceof Connectable &&
				((Connectable)neighbor.getBlock()).canConnect(neighbor, this, Direction.WEST)));

		state = state.with(Properties.WEST, !(((neighbor = world.getBlockState(pos.west())).getBlock() instanceof Connectable &&
				((Connectable)neighbor.getBlock()).canConnect(neighbor, this, Direction.EAST))));

		state = state.with(Properties.UP, !(((neighbor = world.getBlockState(pos.up())).getBlock() instanceof Connectable &&
				((Connectable)neighbor.getBlock()).canConnect(neighbor, this, Direction.DOWN))));

		state = state.with(Properties.DOWN, !(((neighbor = world.getBlockState(pos.down())).getBlock() instanceof Connectable &&
				((Connectable)neighbor.getBlock()).canConnect(neighbor, this, Direction.UP))));

		return state;
	}

	private BlockState checkDrag(BlockState state, World world, BlockPos pos) {
		BlockState below = world.getBlockState(pos.down());
		if (below.getBlock() instanceof DuctBlock) {
			state = state.with(DRAG, below.get(DRAG));
		} else if (below.getBlock() instanceof SoulSandBlock) {
			state = state.with(DRAG, Drag.UP);
		} else if (below.getBlock() instanceof MagmaBlock) {
			state = state.with(DRAG, Drag.DOWN);
		} else {
			state = state.with(DRAG, Drag.NONE);
		}

		return state;
	}

	@Override
	public void neighborUpdate(BlockState state, World world, BlockPos pos, Block block, BlockPos fromPos, boolean notify) {
		world.setBlockState(pos, checkDrag(makeConnections(state, world, pos), world, pos));
	}

	@Environment(EnvType.CLIENT)
	@Override
	public void randomDisplayTick(BlockState state, World world, BlockPos pos, Random random) {
		if (state.get(WATERLOGGED) && state.get(DRAG) != Drag.NONE) {
			double d = pos.getX();
			double e = pos.getY();
			double f = pos.getZ();
			if (state.get(DRAG) == Drag.DOWN) {
				world.addImportantParticle(ParticleTypes.CURRENT_DOWN, d + 0.5D, e + 0.8D, f, 0.0D, 0.0D, 0.0D);
				if (random.nextInt(200) == 0) {
					world.playSound(d, e, f, SoundEvents.BLOCK_BUBBLE_COLUMN_WHIRLPOOL_AMBIENT, SoundCategory.BLOCKS, 0.2F + random.nextFloat() * 0.2F, 0.9F + random.nextFloat() * 0.15F, false);
				}
			} else {
				world.addImportantParticle(ParticleTypes.BUBBLE_COLUMN_UP, d + 0.5D, e, f + 0.5D, 0.0D, 0.04D, 0.0D);
				world.addImportantParticle(ParticleTypes.BUBBLE_COLUMN_UP, d + (double)random.nextFloat(), e + (double)random.nextFloat(), f + (double)random.nextFloat(), 0.0D, 0.04D, 0.0D);
				if (random.nextInt(200) == 0) {
					world.playSound(d, e, f, SoundEvents.BLOCK_BUBBLE_COLUMN_UPWARDS_AMBIENT, SoundCategory.BLOCKS, 0.2F + random.nextFloat() * 0.2F, 0.9F + random.nextFloat() * 0.15F, false);
				}
			}
		}
	}

	@Override
	public boolean canConnect(BlockState state, Connectable other, Direction direction) {
		return true;
	}

	@Override
	public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
		return of(state);
	}

	public enum Drag implements StringIdentifiable {
		UP("up"),
		DOWN("down"),
		NONE("none");

		private final String value;
		Drag(String value) {
			this.value = value;
		}

		@Override
		public String asString() {
			return this.value;
		}
	}
}
