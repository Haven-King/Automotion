package hephaestus.dev.automotion.block;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.AbstractFurnaceBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.Random;

public class HeatedConveyorBelt extends ConveyorBelt {
	private static boolean isPowered(World world, BlockPos pos) {
		BlockState blockState = world.getBlockState(pos.down());
		Block block = blockState.getBlock();
		if (block == Blocks.FIRE || block == Blocks.MAGMA_BLOCK) {
			return true;
		} else if (block == Blocks.FURNACE || block == Blocks.BLAST_FURNACE) {
			return blockState.get(AbstractFurnaceBlock.LIT);
		}

		FluidState fluidState = world.getFluidState(pos.down());
		return fluidState.getFluid() == Fluids.LAVA && fluidState.isStill();
	}

	public HeatedConveyorBelt(Settings settings, double speed) {
		super(settings, speed);
		this.setDefaultState(this.getDefaultState().with(Properties.POWERED, false).with(Properties.BOTTOM, true));
	}

	@Override
	protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
		super.appendProperties(builder);
		builder.add(Properties.POWERED);
	}

	@Override
	public BlockState getPlacementState(ItemPlacementContext ctx) {
		return super.getPlacementState(ctx).with(Properties.POWERED, isPowered(ctx.getWorld(), ctx.getBlockPos())).with(Properties.BOTTOM, true);
	}

	@Override
	public void neighborUpdate(BlockState state, World world, BlockPos pos, Block block, BlockPos fromPos, boolean notify) {
		super.neighborUpdate(state.with(Properties.POWERED, isPowered(world, pos)).with(Properties.BOTTOM, true), world, pos, block, fromPos, notify);
	}

	@Override
	public void onEntityCollision(BlockState state, World world, BlockPos pos, Entity entity) {
		if (!(entity instanceof ItemEntity) && state.get(Properties.POWERED)) {
			entity.setOnFireFor(1);
		}

		super.onEntityCollision(state, world, pos, entity);
	}

	@Override
	@Environment(EnvType.CLIENT)
	public void randomDisplayTick(BlockState state, World world, BlockPos pos, Random random) {
		if (state.get(Properties.POWERED)) {
			int i;
			double x, z, aa;
			for (i = 0; i < 3; ++i) {
				x = (double) pos.getX() + random.nextDouble();
				z = (double) pos.getY() + random.nextDouble() * 0.5D + 0.5D;
				aa = (double) pos.getZ() + random.nextDouble();
				world.addParticle(ParticleTypes.LARGE_SMOKE, x, z, aa, 0.0D, 0.0D, 0.0D);
			}
		}
	}
}
