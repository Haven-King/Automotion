package hephaestus.dev.automotion.common.block.transportation;

import hephaestus.dev.automotion.common.block.entity.GoldenHopperBlockEntity;
import net.minecraft.block.HopperBlock;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.world.BlockView;

public class GoldenHopper extends HopperBlock {
	public GoldenHopper(Settings settings) {
		super(settings);
	}

	@Override
	public BlockEntity createBlockEntity(BlockView world) {
		return new GoldenHopperBlockEntity();
	}
}
