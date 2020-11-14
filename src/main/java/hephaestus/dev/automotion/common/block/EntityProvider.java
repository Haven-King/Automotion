package hephaestus.dev.automotion.common.block;

import net.minecraft.block.EntityShapeContext;
import net.minecraft.entity.Entity;

public interface EntityProvider {
	static EntityProvider of(EntityShapeContext context) {
		return (EntityProvider) context;
	}

	Entity getEntity();
}
