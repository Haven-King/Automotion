package hephaestus.dev.automotion.common.block;

import hephaestus.dev.automotion.common.item.Conveyable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.*;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.StringIdentifiable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;

import java.util.Random;

import static net.minecraft.state.property.Properties.*;

public class ChuteBlock extends Block implements Waterloggable, FluidDrainable, Connectable {
    public static EnumProperty<Drag> DRAG = EnumProperty.of("drag", Drag.class);

    public ChuteBlock(Settings settings) {
        super(settings);
        this.setDefaultState(this.getDefaultState()
                .with(WATERLOGGED, false)
                .with(DRAG, Drag.NONE)
                .with(Properties.NORTH, false)
                .with(Properties.EAST, false)
                .with(Properties.SOUTH, false)
                .with(Properties.WEST, false)
        );
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        super.appendProperties(builder);
        builder.add(WATERLOGGED, DRAG, NORTH, SOUTH, EAST, WEST);
    }

    public static final VoxelShape NORTH_CLOSED = Block.createCuboidShape(0,0,0, 16, 16, 1);
    public static final VoxelShape WEST_CLOSED  = Block.createCuboidShape(0,0,0, 1, 16, 16);
    public static final VoxelShape SOUTH_CLOSED = Block.createCuboidShape(0,0,15, 16, 16, 16);
    public static final VoxelShape EAST_CLOSED  = Block.createCuboidShape(15,0,0, 16, 16, 16);

    public static final VoxelShape NORTH_OPEN   = VoxelShapes.union(
            Block.createCuboidShape(0, 0, 0, 16, 3, 1),
            Block.createCuboidShape(0, 13, 0, 16, 16, 1),
            Block.createCuboidShape(0, 0, 0, 3, 16, 1),
            Block.createCuboidShape(13, 0, 0, 16, 16, 1)
    );

    public static final VoxelShape EAST_OPEN   = VoxelShapes.union(
            Block.createCuboidShape(15, 0, 0, 16, 3, 16),
            Block.createCuboidShape(15, 13, 0, 16, 16, 16),
            Block.createCuboidShape(15, 0, 0, 16, 16, 3),
            Block.createCuboidShape(15, 0, 13, 16, 16, 16)
    );

    public static final VoxelShape SOUTH_OPEN   = VoxelShapes.union(
            Block.createCuboidShape(0, 0, 15, 16, 3, 16),
            Block.createCuboidShape(0, 13, 15, 16, 16, 16),
            Block.createCuboidShape(0, 0, 15, 3, 16, 16),
            Block.createCuboidShape(13, 0, 15, 16, 16, 16)
    );

    public static final VoxelShape WEST_OPEN    = VoxelShapes.union(
            Block.createCuboidShape(0, 0, 0, 1, 3, 16),
            Block.createCuboidShape(0, 13, 0, 1, 16, 16),
            Block.createCuboidShape(0, 0, 0, 1, 16, 3),
            Block.createCuboidShape(0, 0, 13, 1, 16, 16)
    );

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return VoxelShapes.union(
            state.get(NORTH)    ? NORTH_OPEN : NORTH_CLOSED,
            state.get(EAST)     ? EAST_OPEN : EAST_CLOSED,
            state.get(SOUTH)    ? SOUTH_OPEN : SOUTH_CLOSED,
            state.get(WEST)     ? WEST_OPEN : WEST_CLOSED
        );
    }

    @Override
    public FluidState getFluidState(BlockState state) {
        return state.get(WATERLOGGED) ? Fluids.WATER.getStill(false) : super.getFluidState(state);
    }

    @Override
    public Fluid tryDrainFluid(WorldAccess world, BlockPos pos, BlockState state) {
        world.setBlockState(pos, Blocks.AIR.getDefaultState(), 11);
        return Fluids.WATER;
    }

    @Override
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        return makeConnections(this.getDefaultState(), ctx.getWorld(), ctx.getBlockPos()).with(WATERLOGGED,ctx.getWorld().getFluidState(ctx.getBlockPos()).getFluid() == Fluids.WATER);
    }

    protected BlockState makeConnections(BlockState state, World world, BlockPos pos) {
        BlockState neighbor;
        state = state.with(NORTH, (neighbor = world.getBlockState(pos.north())).getBlock() instanceof Connectable &&
                ((Connectable)neighbor.getBlock()).canConnect(neighbor, Direction.SOUTH));

        state = state.with(SOUTH, (neighbor = world.getBlockState(pos.south())).getBlock() instanceof Connectable &&
                ((Connectable)neighbor.getBlock()).canConnect(neighbor, Direction.NORTH));

        state = state.with(EAST, (neighbor = world.getBlockState(pos.east())).getBlock() instanceof Connectable &&
                ((Connectable)neighbor.getBlock()).canConnect(neighbor, Direction.WEST));

        state = state.with(WEST, ((neighbor = world.getBlockState(pos.west())).getBlock() instanceof Connectable &&
                ((Connectable)neighbor.getBlock()).canConnect(neighbor, Direction.EAST)));

        return state;
    }

    private BlockState checkDrag(BlockState state, World world, BlockPos pos) {
        BlockState below = world.getBlockState(pos.down());
        if (below.getBlock() instanceof ChuteBlock) {
            state = state.with(DRAG, below.get(DRAG));
        } else if (below.getBlock() instanceof SoulSandBlock) {
            state = state.with(DRAG, Drag.UP);
        } else if (below.getBlock() instanceof MagmaBlock) {
            state = state.with(DRAG, Drag.DOWN);
        } else {
            state = state.with(DRAG, Drag.NONE);
        }

        return state;
    }

    private static final double MOD = 0.05;
    @Override
    public void onEntityCollision(BlockState state, World world, BlockPos pos, Entity entity) {
        if (state.get(WATERLOGGED) && state.get(DRAG) != Drag.NONE) {
            if (entity instanceof ItemEntity) {
                Vec3d center = new Vec3d(pos.getX() + 0.5, entity.getY(), pos.getZ() + 0.5);
                double distance = entity.getPos().distanceTo(center);
                if (distance > 0.25D) {
                    Vec3d dif = center.subtract(entity.getPos()).normalize().multiply(MOD);
                    entity.addVelocity(dif.x, dif.y, dif.z);
                }
            }

            entity.onBubbleColumnCollision(state.get(DRAG) == Drag.DOWN);

            if (entity instanceof Conveyable) {
                ((Conveyable) entity).convey();
            }
        }
    }

    @Override
    public void neighborUpdate(BlockState state, World world, BlockPos pos, Block block, BlockPos fromPos, boolean notify) {
        world.setBlockState(pos, checkDrag(makeConnections(state, world, pos), world, pos));
    }

    @Environment(EnvType.CLIENT)
    @Override
    public void randomDisplayTick(BlockState state, World world, BlockPos pos, Random random) {
        if (state.get(WATERLOGGED) && state.get(DRAG) != Drag.NONE) {
            double d = pos.getX();
            double e = pos.getY();
            double f = pos.getZ();
            if (state.get(DRAG) == Drag.DOWN) {
                world.addImportantParticle(ParticleTypes.CURRENT_DOWN, d + 0.5D, e + 0.8D, f, 0.0D, 0.0D, 0.0D);
                if (random.nextInt(200) == 0) {
                    world.playSound(d, e, f, SoundEvents.BLOCK_BUBBLE_COLUMN_WHIRLPOOL_AMBIENT, SoundCategory.BLOCKS, 0.2F + random.nextFloat() * 0.2F, 0.9F + random.nextFloat() * 0.15F, false);
                }
            } else {
                world.addImportantParticle(ParticleTypes.BUBBLE_COLUMN_UP, d + 0.5D, e, f + 0.5D, 0.0D, 0.04D, 0.0D);
                world.addImportantParticle(ParticleTypes.BUBBLE_COLUMN_UP, d + (double)random.nextFloat(), e + (double)random.nextFloat(), f + (double)random.nextFloat(), 0.0D, 0.04D, 0.0D);
                if (random.nextInt(200) == 0) {
                    world.playSound(d, e, f, SoundEvents.BLOCK_BUBBLE_COLUMN_UPWARDS_AMBIENT, SoundCategory.BLOCKS, 0.2F + random.nextFloat() * 0.2F, 0.9F + random.nextFloat() * 0.15F, false);
                }
            }

        }
    }

    @Override
    public boolean canConnect(BlockState state, Direction direction) {
        return direction.getAxis() != Direction.Axis.Y;
    }

    private enum Drag implements StringIdentifiable {
        UP("up"),
        DOWN("down"),
        NONE("none");

        private final String value;
        Drag(String value) {
            this.value = value;
        }

        @Override
        public String asString() {
            return this.value;
        }
    }
}
