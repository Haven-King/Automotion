package hephaestus.dev.automotion.common.block.entity;

import hephaestus.dev.automotion.common.AutomotionBlocks;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.Entity;
import net.minecraft.state.property.Properties;
import net.minecraft.util.Tickable;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3i;

import java.util.Collection;

public class FanBlockEntity extends BlockEntity implements Tickable {
    private final int strength;
    public FanBlockEntity() {
        super(AutomotionBlocks.FAN_TYPE);
        strength = 1;
    }

    public FanBlockEntity(int strength) {
        super(AutomotionBlocks.FAN_TYPE);
        this.strength = strength;
    }

    @Override
    public void tick() {
        if (this.getCachedState().get(Properties.ENABLED) && this.world != null) {
            Direction facing = this.getCachedState().get(Properties.FACING);
            Box pushBox = new Box(this.pos.offset(facing), this.pos.offset(facing, strength).add(1,1,1));
            Collection<Entity> entities = this.world.getEntities(null, pushBox);

            Vec3i vec = facing.getVector();
            for (Entity entity: entities) {
                Vec3d pos = entity.getPos();
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
