package dev.hephaestus.automotion.common.util;

import net.minecraft.entity.MovementType;
import net.minecraft.util.math.Vec3d;

public class MovementVector extends Vec3d {
    private final MovementType movementType;

    public MovementVector(MovementType movementType, double x, double y, double z) {
        super(x, y, z);
        this.movementType = movementType;
    }

    public MovementVector(MovementType movementType, Vec3d vec3d) {
        this(movementType, vec3d.x, vec3d.y, vec3d.z);
    }
}
