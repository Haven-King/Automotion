package hephaestus.dev.automotion.common.block;

import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public interface HeatTickable {
	default boolean heatTick(World world, BlockPos pos, int newTemperature) {
		return true;
	}
}
