package hephaestus.dev.automotion.common.block.transportation;

import hephaestus.dev.automotion.common.block.DuctAttachmentBlock;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Material;
import net.minecraft.block.ShapeContext;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.Properties;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class DuctDoorBlock extends DuctAttachmentBlock {
	public DuctDoorBlock(Block block) {
		super(FabricBlockSettings.copyOf(block));
		this.setDefaultState(this.getDefaultState().with(Properties.OPEN, false).with(Properties.POWERED, false));
	}

	@Override
	protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
		super.appendProperties(builder);

		builder.add(Properties.OPEN);
		builder.add(Properties.POWERED);
	}

	@Override
	public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
		boolean open = state.get(Properties.OPEN);
		switch (state.get(Properties.FACING)) {
			case DOWN:	return open ? DuctOpeningBlock.DOWN : DuctBlock.DOWN;
			case UP:	return open ? DuctOpeningBlock.UP : DuctBlock.UP;
			case NORTH:	return open ? DuctOpeningBlock.NORTH : DuctBlock.NORTH;
			case SOUTH:	return open ? DuctOpeningBlock.SOUTH : DuctBlock.SOUTH;
			case WEST:	return open ? DuctOpeningBlock.WEST : DuctBlock.WEST;
			case EAST:	return open ? DuctOpeningBlock.EAST : DuctBlock.EAST;
			default:
				throw new IllegalStateException("Unexpected value: " + state.get(Properties.FACING));
		}
	}

	public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
		state = state.cycle(Properties.OPEN);
		world.setBlockState(pos, state, 2);
		if (state.get(Properties.WATERLOGGED)) {
			world.getFluidTickScheduler().schedule(pos, Fluids.WATER, Fluids.WATER.getTickRate(world));
		}

		this.playToggleSound(player, world, pos, state.get(Properties.OPEN));
		return ActionResult.success(world.isClient);
	}

	public void neighborUpdate(BlockState state, World world, BlockPos pos, Block block, BlockPos fromPos, boolean notify) {
		if (!world.isClient) {
			boolean powered = world.isReceivingRedstonePower(pos);
			if (powered != state.get(Properties.POWERED)) {
				if (state.get(Properties.OPEN) != powered) {
					state = state.with(Properties.OPEN, powered);
					this.playToggleSound(null, world, pos, powered);
				}

				world.setBlockState(pos, state.with(Properties.POWERED, powered), 2);
				if (state.get(Properties.WATERLOGGED)) {
					world.getFluidTickScheduler().schedule(pos, Fluids.WATER, Fluids.WATER.getTickRate(world));
				}
			}

		}
	}

	protected void playToggleSound(@Nullable PlayerEntity player, World world, BlockPos pos, boolean open) {
		int e = open ? this.material == Material.METAL ? 1037 : 1007 : this.material == Material.METAL ? 1036 : 1013;

		world.syncWorldEvent(player, e, pos, 0);
	}

	public BlockState getPlacementState(ItemPlacementContext ctx) {
		BlockState blockState = super.getPlacementState(ctx);

		if (blockState != null && ctx.getWorld().isReceivingRedstonePower(ctx.getBlockPos())) {
			blockState = blockState.with(Properties.OPEN, true).with(Properties.POWERED, true);
		}

		return blockState;
	}
}
