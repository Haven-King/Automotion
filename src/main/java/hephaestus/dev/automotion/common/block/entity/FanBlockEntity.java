package hephaestus.dev.automotion.common.block.entity;

import hephaestus.dev.automotion.common.AutomotionBlocks;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.Entity;
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

            BlockHitResult result = world.raycastBlock(startPos, endPos, this.pos.offset(facing), VoxelShapes.cuboid(pushBox), this.getCachedState());

            Box pushBox = this.pushBox;
            if (result != null && result.getType() == HitResult.Type.BLOCK) {
                pushBox = new Box(
                        startPos.getX(),
                        startPos.getY(),
                        startPos.getZ(),
                        facing.getAxis() == Direction.Axis.X
                                ? result.getPos().x :
                                endPos.x,
                        facing.getAxis() == Direction.Axis.Y
                                ? result.getPos().y :
                                endPos.y,
                        facing.getAxis() == Direction.Axis.Z
                                ? result.getPos().z :
                                endPos.z
                        );
            }

            Collection<Entity> entities = this.world.getEntitiesByType(null, pushBox, e -> true);

            Vec3i vec = facing.getVector();
            for (Entity entity: entities) {
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
