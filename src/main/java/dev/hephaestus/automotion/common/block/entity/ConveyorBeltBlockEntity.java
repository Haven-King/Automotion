package dev.hephaestus.automotion.common.block.entity;

import dev.hephaestus.automotion.common.AutomotionBlocks;
import dev.hephaestus.automotion.common.block.ConveyorBeltBlock;
import net.fabricmc.fabric.api.block.entity.BlockEntityClientSerializable;
import net.fabricmc.fabric.api.rendering.data.v1.RenderAttachmentBlockEntity;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import org.jetbrains.annotations.Nullable;

public class ConveyorBeltBlockEntity extends BlockEntity implements BlockEntityClientSerializable, RenderAttachmentBlockEntity {
    private double baseSpeed;
    private BlockPos pivot = null;

    public ConveyorBeltBlockEntity(BlockPos pos, BlockState state) {
        super(AutomotionBlocks.CONVEYOR_BELT_BLOCK_ENTITY, pos, state);
    }

    public boolean needsInitialization() {
        return this.pivot == null;
    }

    public void calculatePivot() {
        if (this.world != null && !this.world.isClient) {
            BlockState state = this.getCachedState();
            ConveyorBeltBlock.Shape shape = state.get(ConveyorBeltBlock.SHAPE);

            if (shape != ConveyorBeltBlock.Shape.STRAIGHT) {
                Direction direction = state.get(Properties.HORIZONTAL_FACING);

                int dX = 0, dZ = 0;
                int bdX = 0, bdZ = 0;

                switch (direction) {
                    case NORTH:
                        dX = shape == ConveyorBeltBlock.Shape.CLOCKWISE ? 1 /* East */ : -1 /* West */;
                        dZ = -1;
                        bdX = shape == ConveyorBeltBlock.Shape.CLOCKWISE ? 1 /* East */ : 0 /* West */;
                        break;
                    case SOUTH:
                        dX = shape == ConveyorBeltBlock.Shape.CLOCKWISE ? -1 /* West */ : 1 /* East */;
                        dZ = 1;
                        bdX = shape == ConveyorBeltBlock.Shape.CLOCKWISE ? 0 /* West */ : 1 /* East */;
                        bdZ = 1;
                        break;
                    case WEST:
                        dX = -1;
                        dZ = shape == ConveyorBeltBlock.Shape.CLOCKWISE ? -1 /* North */ : 1 /* South */;
                        bdZ = shape == ConveyorBeltBlock.Shape.CLOCKWISE ? 0 /* North */ : 1 /* South */;
                        break;
                    case EAST:
                        dX = 1;
                        dZ = shape == ConveyorBeltBlock.Shape.CLOCKWISE ? 1 /* South */ : -1 /* North */;
                        bdX = 1;
                        bdZ = shape == ConveyorBeltBlock.Shape.CLOCKWISE ? 1 /* South */ : 0 /* North */;
                        break;
                }

                int pX = this.pos.getX(), pZ = this.pos.getZ();

                BlockPos.Mutable mut = this.pos.mutableCopy();

                while (matches(state, world.getBlockState(mut.move(dX, 0, 0)))) {
                    pX += dX;
                }

                mut.set(this.pos);

                while (matches(state, world.getBlockState(mut.move(0, 0, dZ)))) {
                    pZ += dZ;
                }

                this.pivot = new BlockPos(pX + bdX, 0, pZ + bdZ);
                this.sync();
            }
        }
    }

    private static boolean matches(BlockState state1, BlockState state2) {
        return state1.getBlock() instanceof ConveyorBeltBlock && state2.getBlock() instanceof ConveyorBeltBlock
                && state1.get(Properties.HORIZONTAL_FACING) == state2.get(Properties.HORIZONTAL_FACING)
                && state1.get(ConveyorBeltBlock.SHAPE) == state2.get(ConveyorBeltBlock.SHAPE);
    }

    public void calculatePivotOld() {
        if (this.world != null && !this.world.isClient) {
            BlockState state = this.getCachedState();
            ConveyorBeltBlock.Shape shape = state.get(ConveyorBeltBlock.SHAPE);

            if (shape != ConveyorBeltBlock.Shape.STRAIGHT) {
                Direction direction = state.get(Properties.HORIZONTAL_FACING);

                int dX = 0, dZ = 0;
                int bdX = 0, bdZ = 0;

                switch (direction) {
                    case NORTH:
                        dX = shape == ConveyorBeltBlock.Shape.CLOCKWISE ? 1 /* East */ : -1 /* West */;
                        dZ = -1;
                        bdX = shape == ConveyorBeltBlock.Shape.CLOCKWISE ? 1 /* East */ : 0 /* West */;
                        break;
                    case SOUTH:
                        dX = shape == ConveyorBeltBlock.Shape.CLOCKWISE ? -1 /* West */ : 1 /* East */;
                        dZ = 1;
                        bdX = shape == ConveyorBeltBlock.Shape.CLOCKWISE ? 0 /* West */ : 1 /* East */;
                        bdZ = 1;
                        break;
                    case WEST:
                        dX = -1;
                        dZ = shape == ConveyorBeltBlock.Shape.CLOCKWISE ? -1 /* North */ : 1 /* South */;
                        bdZ = shape == ConveyorBeltBlock.Shape.CLOCKWISE ? 0 /* North */ : 1 /* South */;
                        break;
                    case EAST:
                        dX = 1;
                        dZ = shape == ConveyorBeltBlock.Shape.CLOCKWISE ? 1 /* South */ : -1 /* North */;
                        bdX = 1;
                        bdZ = shape == ConveyorBeltBlock.Shape.CLOCKWISE ? 1 /* South */ : 0 /* North */;
                        break;
                }

                BlockState neighbor;
                BlockPos.Mutable mut = this.pos.mutableCopy();

                if ((neighbor = this.world.getBlockState(this.pos.add(dX, 0, dZ))).getBlock() instanceof ConveyorBeltBlock
                        && neighbor.get(Properties.HORIZONTAL_FACING) == direction
                        && neighbor.get(ConveyorBeltBlock.SHAPE) == shape) {

                    while ((neighbor = this.world.getBlockState(mut)).getBlock() instanceof ConveyorBeltBlock
                            && neighbor.get(Properties.HORIZONTAL_FACING) == direction
                            && neighbor.get(ConveyorBeltBlock.SHAPE) == shape) {
                        mut.move(dX, 0, dZ);
                    }
                }

                this.pivot = mut.move(bdX, 0, bdZ).toImmutable();

                this.sync();
            }
        }
    }

    public BlockPos getPivot() {
        return this.pivot;
    }

    public double getSpeed() {
        return this.baseSpeed;
    }

    public BlockEntity withBaseSpeed(double speed) {
        this.baseSpeed = speed;

        return this;
    }

    @Override
    public void readNbt(CompoundTag tag) {
        this.baseSpeed = tag.getDouble("BaseSpeed");

        if (tag.contains("PivotX")) {
            this.pivot = new BlockPos(
                    tag.getInt("PivotX"),
                    0,
                    tag.getInt("PivotZ")
            );
        }
    }

    @Override
    public CompoundTag writeNbt(CompoundTag tag) {
        tag.putDouble("BaseSpeed", this.baseSpeed);

        if (this.pivot != null) {
            tag.putInt("PivotX", this.pivot.getX());
            tag.putInt("PivotZ", this.pivot.getZ());
        }

        return super.writeNbt(tag);
    }

    @Override
    public void fromClientTag(CompoundTag tag) {
        this.baseSpeed = tag.getDouble("BaseSpeed");

        if (tag.contains("PivotX")) {
            this.pivot = new BlockPos(
                    tag.getInt("PivotX"),
                    0,
                    tag.getInt("PivotZ")
            );
        }
    }

    @Override
    public CompoundTag toClientTag(CompoundTag tag) {
        tag.putDouble("BaseSpeed", this.baseSpeed);

        if (this.pivot != null) {
            tag.putInt("PivotX", this.pivot.getX());
            tag.putInt("PivotZ", this.pivot.getZ());
        }

        return tag;
    }

    @Override
    public @Nullable Object getRenderAttachmentData() {
        return this.pivot;
    }
}
