package hephaestus.dev.automotion.common.block.entity;

import hephaestus.dev.automotion.common.AutomotionBlocks;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.Entity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.property.Properties;
import net.minecraft.util.Tickable;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.*;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.RaycastContext;

import java.util.Collection;

public class FanBlockEntity extends BlockEntity implements Tickable {
    private final int strength;
    private Direction facing = null;
    private Vec3d startPos = null;
    private Vec3d endPos = null;
    private Box pushBox = null;

    public FanBlockEntity() {
        this(1);
    }

    public FanBlockEntity(int strength) {
        super(AutomotionBlocks.FAN_TYPE);
        this.strength = strength;
    }

    private void init() {
        if (this.getCachedState() != null && facing == null) {
            this.facing = this.getCachedState().get(Properties.FACING);
            this.startPos = Vec3d.ofCenter(this.pos).add(Vec3d.of(facing.getVector()).multiply(0.5));
            this.endPos = startPos.add(Vec3d.of(facing.getVector()).multiply(strength)).subtract(Vec3d.of(facing.getVector()).multiply(0.5));
            this.pushBox = new Box(this.pos.offset(facing), this.pos.offset(facing, strength).add(1, 1, 1));
        }
    }

    @Override
    public void tick() {
        if (this.getCachedState().get(Properties.ENABLED) && this.world != null) {
            this.init();

            Collection<Entity> entities = this.world.getOtherEntities(null, pushBox);

            Vec3i vec = facing.getVector();
            Direction.Axis axis = facing.getAxis();
            for (Entity entity: entities) {
                BlockHitResult result = world.raycast(new RaycastContext(startPos, endPos, RaycastContext.ShapeType.COLLIDER, RaycastContext.FluidHandling.NONE, entity));
                if (result == null || isBetween(entity.getPos().getComponentAlongAxis(axis), result.getPos().getComponentAlongAxis(axis), startPos.getComponentAlongAxis(axis))) {
                    double distance = this.getPos().getManhattanDistance(entity.getBlockPos());
                    double scale = strength / distance;
                    entity.addVelocity(
                            vec.getX() * 0.1 * scale,
                            vec.getY() * 0.1 * scale,
                            vec.getZ() * 0.1 * scale
                    );
                }
            }
        }
    }

    private static boolean isBetween(double i, double l, double r) {
        return (l <= i && i <= r) || (r <= i && i <= l);
    }
}
