package hephaestus.dev.automotion.block.enums;

import net.minecraft.util.StringIdentifiable;

public enum ConveyorBeltShape implements StringIdentifiable {
	NORTH_SOUTH("north_south"),
	EAST_WEST("east_west"),
	ASCENDING_EAST("ascending_east"),
	ASCENDING_WEST("ascending_west"),
	ASCENDING_NORTH("ascending_north"),
	ASCENDING_SOUTH("ascending_south");


	private final String id;
	ConveyorBeltShape(String id) {
		this.id = id;
	}

	@Override
	public String asString() {
		return this.id;
	}

	public boolean isAscending() {
		return this == ASCENDING_NORTH || this == ASCENDING_EAST || this == ASCENDING_SOUTH || this == ASCENDING_WEST;
	}
}
