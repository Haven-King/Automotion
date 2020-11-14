package hephaestus.dev.automotion.common.item;

import hephaestus.dev.automotion.common.Automotion;
import hephaestus.dev.automotion.common.block.conveyors.ConveyorBelt;
import net.minecraft.block.BlockState;
import net.minecraft.item.Item;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.util.ActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class WrenchItem extends Item {
	public WrenchItem(Settings settings) {
		super(settings);
	}

	@Override
	public ActionResult useOnBlock(ItemUsageContext context) {
		World world = context.getWorld();
		BlockPos pos = context.getBlockPos();
		BlockState blockState = world.getBlockState(pos);

		if (context.getPlayer() != null && context.getPlayer().isSneaking()) {
			if (blockState.getBlock() instanceof ConveyorBelt) {
				int n = ConveyorBelt.Angle.values().length;
				world.setBlockState(pos, blockState.with(ConveyorBelt.ANGLE,
					ConveyorBelt.Angle.values()[(((blockState.get(ConveyorBelt.ANGLE).ordinal() + (Automotion.isAlternate(context.getPlayer()) ? -1 : 1)) % n) + n ) % n]
				), 2);

				return ActionResult.SUCCESS;
			}
		}

		if (blockState.getBlock() instanceof ConveyorBelt) {
			world.setBlockState(pos, blockState.with(ConveyorBelt.FACING,
					Automotion.isAlternate(context.getPlayer())
							? blockState.get(ConveyorBelt.FACING).rotateYCounterclockwise()
							: blockState.get(ConveyorBelt.FACING).rotateYClockwise()
			), 2);
		}

		return super.useOnBlock(context);
	}
}
