package hephaestus.dev.automotion.common.item;

import net.minecraft.util.math.Vec3d;

public interface Conveyable {
	boolean isBeingConveyed();
	void convey(Vec3d direction);
	void doConveyance();
}
