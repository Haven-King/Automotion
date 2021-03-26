package dev.hephaestus.automotion.common.block;

import dev.hephaestus.automotion.common.block.entity.ConveyorBeltBlockEntity;
import dev.hephaestus.automotion.common.util.PlayerExtensions;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.Properties;
import net.minecraft.tag.FluidTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

public class ConveyorBeltBlock extends Block implements Waterloggable, BlockEntityProvider {
    private static final VoxelShape OUTLINE_X = Block.createCuboidShape(0, 11, 1, 16, 15, 15);
    private static final VoxelShape OUTLINE_Y = Block.createCuboidShape(1, 11, 0, 15, 15, 16);

    private static final Map<Entity, Map<Direction, BlockPos>> COLLISIONS = new HashMap<>();

    private final double speed;

    public ConveyorBeltBlock(double speed, Settings settings) {
        super(settings);
        this.speed = speed;

        this.setDefaultState(this.getStateManager().getDefaultState()
                .with(Properties.HORIZONTAL_FACING, Direction.NORTH)
                .with(Properties.WATERLOGGED, false)
                .with(Properties.ENABLED, true)
        );
    }

    @Nullable
    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new ConveyorBeltBlockEntity(pos, state).withBaseSpeed(this.speed);
    }

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return state.get(Properties.HORIZONTAL_FACING).getAxis() == Direction.Axis.X ? OUTLINE_X : OUTLINE_Y;
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(Properties.HORIZONTAL_FACING, Properties.WATERLOGGED, Properties.ENABLED);
    }

    private BlockState getState(World world, BlockPos pos, BlockState state) {
        state = state.with(Properties.ENABLED, !world.isReceivingRedstonePower(pos));

        return state;
    }

    @Override
    public void neighborUpdate(BlockState state, World world, BlockPos pos, Block block, BlockPos fromPos, boolean notify) {
        state = getState(world, pos, state);
        BlockState neighbor;
        if (block == this && (neighbor = world.getBlockState(pos)).get(Properties.HORIZONTAL_FACING) == state.get(Properties.HORIZONTAL_FACING)) {
            state = state.with(Properties.ENABLED, neighbor.get(Properties.ENABLED));
        }

        world.setBlockState(pos, state, 2);
    }

    @Override
    public BlockState getStateForNeighborUpdate(BlockState state, Direction direction, BlockState neighborState, WorldAccess world, BlockPos pos, BlockPos neighborPos) {
        if (neighborState.isOf(this) && neighborState.get(Properties.HORIZONTAL_FACING) == state.get(Properties.HORIZONTAL_FACING)) {
            return state.with(Properties.ENABLED, neighborState.get(Properties.ENABLED));
        }

        return super.getStateForNeighborUpdate(state, direction, neighborState, world, pos, neighborPos);
    }

    @Nullable
    @Override
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        World world = ctx.getWorld();
        BlockPos pos = ctx.getBlockPos();
        Direction direction = !world.isClient && ctx.getPlayer() != null && ((PlayerExtensions) ctx.getPlayer()).doAltPlacement()
                ? ctx.getPlayerFacing().getOpposite()
                : ctx.getPlayerFacing();

        BlockState state = this.getDefaultState();

        state = state.with(Properties.HORIZONTAL_FACING, direction);
        state = state.with(Properties.WATERLOGGED, world.getFluidState(pos).isIn(FluidTags.WATER));

        return getState(world, pos, state);
    }

    public void collide(BlockPos pos, Entity entity) {
        Map<Direction, BlockPos> map = COLLISIONS.computeIfAbsent(entity, e -> new HashMap<>());

        map.compute(entity.world.getBlockState(pos).get(Properties.HORIZONTAL_FACING), (key, value) -> {
            if (value == null || Vec3d.ofCenter(value).distanceTo(entity.getPos()) > Vec3d.ofCenter(pos).distanceTo(entity.getPos())) {
                return pos.toImmutable();
            } else {
                return value;
            }
        });
    }

    public static boolean move(Entity entity) {
        if (!COLLISIONS.containsKey(entity)) return false;

        double dX = 0, dZ = 0;

        Map<Direction, BlockPos> map = COLLISIONS.remove(entity);

        if (map != null) {
            for (Map.Entry<Direction, BlockPos> entry : map.entrySet()) {
                BlockEntity blockEntity = entity.world.getBlockEntity(entry.getValue());

                if (blockEntity instanceof ConveyorBeltBlockEntity) {
                    Direction dir = entry.getKey();
                    double speed = ((ConveyorBeltBlockEntity) blockEntity).getSpeed();

                    dX += dir.getOffsetX() * speed;
                    dZ += dir.getOffsetZ() * speed;
                }
            }

            entity.addVelocity(dX, 0, dZ);
        }

        return Math.abs(dZ) > 0 || Math.abs(dX) > 0;
    }
}
