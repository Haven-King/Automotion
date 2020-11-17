package hephaestus.dev.automotion.common.block;

import hephaestus.dev.automotion.common.block.entity.DiamondHopperBlockEntity;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.Hopper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.stat.Stats;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.function.BooleanBiFunction;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class DiamondHopperBlock extends BlockWithEntity {
	public static final Direction[] DIRECTIONS = new Direction[] {
			Direction.NORTH,
			Direction.SOUTH,
			Direction.EAST,
			Direction.WEST,
			Direction.DOWN
	};

	public static final BooleanProperty[] DIRECTIONS_PROPERTIES = new BooleanProperty[] {
			Properties.NORTH,
			Properties.SOUTH,
			Properties.EAST,
			Properties.WEST,
			Properties.DOWN
	};

	private static final VoxelShape DEFAULT_SHAPE = VoxelShapes.combineAndSimplify(
			VoxelShapes.union(
					Block.createCuboidShape(0.0D, 10.0D, 0.0D, 16.0D, 16.0D, 16.0D),
					Block.createCuboidShape(4.0D, 4.0D, 4.0D, 12.0D, 10.0D, 12.0D)
			),
			Hopper.INSIDE_SHAPE,
			BooleanBiFunction.ONLY_FIRST
	);

	public DiamondHopperBlock(Settings settings) {
		super(settings);
		this.setDefaultState(this.getDefaultState()
				.with(Properties.NORTH, false)
				.with(Properties.SOUTH, false)
				.with(Properties.EAST, false)
				.with(Properties.WEST, false)
				.with(Properties.DOWN, false)
				.with(Properties.WATERLOGGED, false)
		);
	}

	@Override
	protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
		builder.add(
				Properties.NORTH,
				Properties.SOUTH,
				Properties.EAST,
				Properties.WEST,
				Properties.DOWN,
				Properties.WATERLOGGED
		);
	}

	@Override
	public void onEntityCollision(BlockState state, World world, BlockPos pos, Entity entity) {
		BlockEntity blockEntity = world.getBlockEntity(pos);
		if (blockEntity instanceof DiamondHopperBlockEntity) {
			((DiamondHopperBlockEntity)blockEntity).onEntityCollided(entity);
		}
	}

	@Override
	public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
		return DEFAULT_SHAPE;
	}

	public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
		if (world.isClient) {
			return ActionResult.SUCCESS;
		} else {
			BlockEntity blockEntity = world.getBlockEntity(pos);
			if (blockEntity instanceof DiamondHopperBlockEntity) {
				player.openHandledScreen((DiamondHopperBlockEntity)blockEntity);
				player.incrementStat(Stats.INSPECT_HOPPER);
			}

			return ActionResult.CONSUME;
		}
	}

	@Override
	public BlockRenderType getRenderType(BlockState state) {
		return BlockRenderType.MODEL;
	}

	@Override
	public @Nullable BlockState getPlacementState(ItemPlacementContext ctx) {
		return this.getDefaultState();
	}

	@Override
	public @Nullable BlockEntity createBlockEntity(BlockView world) {
		return new DiamondHopperBlockEntity();
	}

	public BlockState getState(World world, BlockPos pos, BlockState state) {
		BlockEntity blockEntity = world.getBlockEntity(pos);

		if (blockEntity instanceof DiamondHopperBlockEntity) {
			List<ItemStack> stacks = ((DiamondHopperBlockEntity) blockEntity).getInventory();


			for (int y = 0; y < 5; ++y) {
				for (int x = 0; x < 5; ++x) {
					if (!stacks.get(x + y * 5).isEmpty()) {
						state = state.with(DIRECTIONS_PROPERTIES[y], true);
						break;
					} else if (x == 4) {
						state = state.with(DIRECTIONS_PROPERTIES[y], false);
					}
				}
			}
		}

		return state;
	}
}
