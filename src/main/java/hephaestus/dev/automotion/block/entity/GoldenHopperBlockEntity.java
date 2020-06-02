package hephaestus.dev.automotion.block.entity;

import hephaestus.dev.automotion.AutomotionBlocks;
import net.minecraft.block.entity.HopperBlockEntity;

public class GoldenHopperBlockEntity extends HopperBlockEntity {
	public GoldenHopperBlockEntity() {
		super();
		this.type = AutomotionBlocks.GOLDEN_HOPPER_TYPE;
	}
}
