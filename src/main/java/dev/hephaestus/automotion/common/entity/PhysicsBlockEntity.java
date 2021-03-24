package dev.hephaestus.automotion.common.entity;

import com.jme3.bullet.collision.shapes.BoxCollisionShape;
import com.jme3.bullet.collision.shapes.CollisionShape;
import com.jme3.bullet.collision.shapes.CompoundCollisionShape;
import com.jme3.bullet.collision.shapes.infos.ChildCollisionShape;
import net.fabricmc.fabric.api.util.NbtType;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtHelper;
import net.minecraft.network.Packet;
import net.minecraft.network.packet.s2c.play.EntitySpawnS2CPacket;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class PhysicsBlockEntity extends Entity /*implements EntityPhysicsElement*/ {
    private final CompoundCollisionShape collisionShape = new CompoundCollisionShape();
//    private final ElementRigidBody rigidBody;
    private BlockState blockState;

    public PhysicsBlockEntity(EntityType<?> type, World world) {
        super(type, world);
//        this.rigidBody = new ElementRigidBody(this, MinecraftSpace.get(world), this.collisionShape);
//        this.rigidBody = new ElementRigidBody(this);
//        this.rigidBody.setAngularDamping(0.25F);

        this.with(Blocks.OAK_STAIRS.getDefaultState());
    }

    public PhysicsBlockEntity with(BlockState blockState) {
        this.blockState = blockState;

        for (ChildCollisionShape child : this.collisionShape.listChildren()) {
            this.collisionShape.removeChildShape(child.getShape());
        }

        for (Box box : blockState.getCollisionShape(this.world, BlockPos.ORIGIN).getBoundingBoxes()) {
            CollisionShape child = new BoxCollisionShape(
                    (float) box.getXLength() / 2F,
                    (float) box.getYLength() / 2F,
                    (float) box.getZLength() / 2F
            );

            this.collisionShape.addChildShape(child,
                    (float) box.minX - 0.5F,
                    (float) box.minY - 0.5F,
                    (float) box.minZ - 0.5F
            );
        }

        return this;
    }

    @Override
    public boolean collides() {
        return !this.isRemoved();
    }

    //    @Override
//    public ElementRigidBody getRigidBody() {
//        return this.rigidBody;
//    }
//
//    @Override
//    public void step(MinecraftSpace space) {
//
//    }
//
//    @Override
//    public void reset() {
//
//    }



    @Override
    protected void initDataTracker() {

    }

    @Override
    protected void writeCustomDataToNbt(CompoundTag tag) {
        if (this.blockState != null) {
            tag.put("BlockState", NbtHelper.fromBlockState(this.blockState));
        }
    }

    @Override
    protected void readCustomDataFromNbt(CompoundTag tag) {
        if (tag.contains("BlockState", NbtType.COMPOUND)) {
            this.blockState = NbtHelper.toBlockState(tag.getCompound("BlockState"));
        }
    }

    @Override
    public Packet<?> createSpawnPacket() {
        return new EntitySpawnS2CPacket(this.getId(), this.getUuid(), this.getX(), this.getY(), this.getZ(), this.pitch, this.yaw, this.getType(), 0, this.getVelocity());
    }

    public @Nullable BlockState getBlockState() {
        return this.blockState;
    }
}
