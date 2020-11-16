package hephaestus.dev.automotion.common.item;

import hephaestus.dev.automotion.common.Automotion;
import hephaestus.dev.automotion.common.block.DuctBlock;
import hephaestus.dev.automotion.common.block.conveyors.ConveyorBelt;
import net.minecraft.block.BlockState;
import net.minecraft.item.Item;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.state.property.Properties;
import net.minecraft.state.property.Property;
import net.minecraft.text.MutableText;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.ActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraft.world.chunk.WorldChunk;
import org.jetbrains.annotations.NotNull;

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
				if (!world.isClient) {
					int n = ConveyorBelt.Angle.values().length;

					world.setBlockState(pos, blockState.with(ConveyorBelt.ANGLE,
							ConveyorBelt.Angle.values()[(((blockState.get(ConveyorBelt.ANGLE).ordinal() + (Automotion.isAlternate(context.getPlayer()) ? -1 : 1)) % n) + n) % n]
					), 2);
				}

				return use(ActionResult.SUCCESS, world, context.getPlayer().getPos());
			}
		}

		if (blockState.getBlock() instanceof ConveyorBelt) {
			if (!world.isClient) {
				world.setBlockState(pos, blockState.with(ConveyorBelt.FACING,
						Automotion.isAlternate(context.getPlayer())
								? blockState.get(ConveyorBelt.FACING).rotateYCounterclockwise()
								: blockState.get(ConveyorBelt.FACING).rotateYClockwise()
				), 2);
			}

			return use(ActionResult.SUCCESS, world, context.getPlayer().getPos());
		}

		return use(super.useOnBlock(context), world, context.getPlayer().getPos());
	}

	private static ActionResult use(@NotNull ActionResult result, World world, Vec3d pos) {
		if (!world.isClient && result.isAccepted()) {
			world.playSound(null, pos.x, pos.y, pos.z, SoundEvents.BLOCK_ANVIL_HIT, SoundCategory.PLAYERS, 1F, 1F);
		}

		return result;
	}
}
