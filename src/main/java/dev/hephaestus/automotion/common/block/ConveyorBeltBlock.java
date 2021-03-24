package dev.hephaestus.automotion.common.block;

import dev.hephaestus.automotion.common.block.entity.ConveyorBeltBlockEntity;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.Entity;
import net.minecraft.entity.MovementType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.tag.FluidTags;
import net.minecraft.util.StringIdentifiable;
import net.minecraft.util.math.*;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

public class ConveyorBeltBlock extends Block implements Waterloggable, BlockEntityProvider {
    public static final EnumProperty<Shape> SHAPE = EnumProperty.of("shape", Shape.class);

    private static final VoxelShape OUTLINE = Block.createCuboidShape(0, 13, 0, 16, 16, 16);
    private static final Map<Entity, BlockPos> COLLISIONS = new HashMap<>();

    private final double speed;

    public ConveyorBeltBlock(double speed, Settings settings) {
        super(settings);
        this.speed = speed;

        this.setDefaultState(this.getStateManager().getDefaultState()
                .with(Properties.HORIZONTAL_FACING, Direction.NORTH)
                .with(Properties.WATERLOGGED, false)
                .with(Properties.ENABLED, true)
                .with(SHAPE, Shape.STRAIGHT)
        );
    }

    @Nullable
    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new ConveyorBeltBlockEntity(pos, state).withBaseSpeed(this.speed);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(World world, BlockState state, BlockEntityType<T> type) {
        return ((world1, pos, state1, blockEntity) -> {
            if (blockEntity instanceof ConveyorBeltBlockEntity && ((ConveyorBeltBlockEntity) blockEntity).needsInitialization()) {
                ((ConveyorBeltBlockEntity) blockEntity).calculatePivot();
            }
        });
    }

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return OUTLINE;
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(Properties.HORIZONTAL_FACING, Properties.WATERLOGGED, Properties.ENABLED, SHAPE);
    }

    private BlockState getState(World world, BlockPos pos, BlockState state) {
        state = state.with(Properties.ENABLED, !world.isReceivingRedstonePower(pos));

        Direction facing = state.get(Properties.HORIZONTAL_FACING);
        Direction counterclockwise = facing.rotateYCounterclockwise();
        Direction clockwise = facing.rotateYClockwise();

        BlockState left = world.getBlockState(pos.offset(counterclockwise));
        BlockState right = world.getBlockState(pos.offset(clockwise));

        boolean leftAngle = left.getBlock() instanceof ConveyorBeltBlock && (
                left.get(Properties.HORIZONTAL_FACING) == clockwise
                || (
                        left.get(Properties.HORIZONTAL_FACING) == facing
                        && left.get(SHAPE) == Shape.COUNTERCLOCKWISE
                )
        );

        boolean rightAngle = right.getBlock() instanceof ConveyorBeltBlock && (
                right.get(Properties.HORIZONTAL_FACING) == counterclockwise
                        || (
                        right.get(Properties.HORIZONTAL_FACING) == facing
                                && right.get(SHAPE) == Shape.CLOCKWISE
                )
        );

        if (leftAngle ^ rightAngle) {
            state = state.with(SHAPE, leftAngle ? Shape.COUNTERCLOCKWISE : Shape.CLOCKWISE);
        }

        return state;
    }

    @Nullable
    @Override
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        World world = ctx.getWorld();
        BlockPos pos = ctx.getBlockPos();
        Direction direction = ctx.getPlayerFacing();

        BlockState state = this.getDefaultState();

        state = state.with(Properties.HORIZONTAL_FACING, direction);
        state = state.with(Properties.WATERLOGGED, world.getFluidState(pos).isIn(FluidTags.WATER));

        return getState(world, pos, state);
    }

    @Override
    public void neighborUpdate(BlockState state, World world, BlockPos pos, Block block, BlockPos fromPos, boolean notify) {
        BlockEntity blockEntity = world.getBlockEntity(pos);

        if (blockEntity instanceof ConveyorBeltBlockEntity) {
            ((ConveyorBeltBlockEntity) blockEntity).calculatePivot();
        }

        BlockState newState = this.getState(world, pos, state);

        if (newState != state) {
            world.setBlockState(pos, newState);
        }
    }

    public void collide(BlockPos pos, Entity entity) {
        if (COLLISIONS.containsKey(entity) && COLLISIONS.get(entity) != null) {
            double oldPos = Vec3d.ofCenter(COLLISIONS.get(entity)).distanceTo(entity.getPos());
            double newPos = Vec3d.ofCenter(pos).distanceTo(entity.getPos());

            if (newPos < oldPos) {
                COLLISIONS.put(entity, pos.toImmutable());
            }
        } else {
            COLLISIONS.put(entity, pos.toImmutable());
        }
    }

    public static void move(Entity entity) {
        if (!COLLISIONS.containsKey(entity)) return;

        BlockPos blockPos = COLLISIONS.get(entity);
        World world = entity.world;
        BlockState state = world.getBlockState(blockPos);
        BlockEntity blockEntity = world.getBlockEntity(blockPos);

        if (blockEntity instanceof ConveyorBeltBlockEntity) {
            Direction primary = state.get(Properties.HORIZONTAL_FACING);
            Shape shape = state.get(SHAPE);
            double speed = ((ConveyorBeltBlockEntity) blockEntity).getSpeed();

            Vec3d p = entity.getPos();
            double x = p.x, z = p.z;

            if (shape == Shape.STRAIGHT) {
                Vec3f vec = primary.getUnitVector();
                x += vec.getX() * speed;
                z += vec.getZ() * speed;
            } else {
                BlockPos pivot = ((ConveyorBeltBlockEntity) blockEntity).getPivot();
                Vec3f pos = new Vec3f(
                        (float) x - pivot.getX(),
                        0,
                        (float) z - pivot.getZ()
                );

                speed /= horizontalDistance(pivot, p) * Math.PI * 2;

                float movement = (float) (360 * speed * (shape == Shape.CLOCKWISE ? -1 : 1));

                pos.rotate(Vec3f.POSITIVE_Y.getDegreesQuaternion(movement));

                x = pos.getX() + pivot.getX();
                z = pos.getZ() + pivot.getZ();

                if (!(entity instanceof PlayerEntity)) {
                    entity.yaw -= movement;
                }
            }

            entity.move(MovementType.SELF, new Vec3d(x - p.x, 0, z - p.z));
//            entity.setVelocity(0, entity.getVelocity().getY(), 0);
//            entity.addVelocity(x - p.x, 0, z - p.z);
//            entity.setPosition(x, p.y, z);
        }

        COLLISIONS.remove(entity);
    }

    private static double horizontalDistance(Vec3i pivot, Vec3d pos) {
        double dX = pivot.getX() - pos.getX();
        double dZ = pivot.getZ() - pos.getZ();

        return MathHelper.sqrt(dX * dX + dZ * dZ);
    }

    public enum Shape implements StringIdentifiable {
        STRAIGHT("straight"), CLOCKWISE("cw"), COUNTERCLOCKWISE("ccw");

        private final String string;

        Shape(String string) {
            this.string = string;
        }

        @Override
        public String asString() {
            return this.string;
        }
    }
}
