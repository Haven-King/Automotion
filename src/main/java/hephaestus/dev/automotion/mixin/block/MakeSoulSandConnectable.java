package hephaestus.dev.automotion.mixin.block;

import hephaestus.dev.automotion.common.block.Connectable;
import hephaestus.dev.automotion.common.block.transportation.DuctBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.SoulSandBlock;
import net.minecraft.util.math.Direction;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(SoulSandBlock.class)
public class MakeSoulSandConnectable implements Connectable {
	@Override
	public boolean canConnect(BlockState state, Connectable other, Direction direction) {
		return direction == Direction.UP && other instanceof DuctBlock;
	}
}
