package hephaestus.dev.automotion.common.block.transportation;

import hephaestus.dev.automotion.common.block.DuctAttachmentBlock;
import hephaestus.dev.automotion.common.block.entity.FanBlockEntity;
import net.minecraft.block.Block;
import net.minecraft.block.BlockEntityProvider;
import net.minecraft.block.BlockState;
import net.minecraft.block.ShapeContext;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.Properties;
import net.minecraft.util.function.BooleanBiFunction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;

public class FanBlock extends DuctAttachmentBlock implements BlockEntityProvider {
    public static final VoxelShape UP = VoxelShapes.union(
            VoxelShapes.combine(DuctBlock.UP, DuctBlock.FRAME, BooleanBiFunction.AND),
            Block.createCuboidShape(2, 14, 2, 14, 16, 14),
            Block.createCuboidShape(3, 6, 3, 13, 14, 13)
    );

    public static final VoxelShape DOWN = VoxelShapes.union(
            VoxelShapes.combine(DuctBlock.DOWN, DuctBlock.FRAME, BooleanBiFunction.AND),
            Block.createCuboidShape(2, 0, 2, 14, 2, 14),
            Block.createCuboidShape(3, 2, 3, 13, 10, 13)
    );

    public static final VoxelShape NORTH = VoxelShapes.union(
            VoxelShapes.combine(DuctBlock.NORTH, DuctBlock.FRAME, BooleanBiFunction.AND),
            Block.createCuboidShape(2, 2, 0, 14, 14, 2),
            Block.createCuboidShape(3, 3, 2, 13, 13, 10)
    );

    public static final VoxelShape SOUTH = VoxelShapes.union(
            VoxelShapes.combine(DuctBlock.SOUTH, DuctBlock.FRAME, BooleanBiFunction.AND),
            Block.createCuboidShape(2, 2, 14, 14, 14, 16),
            Block.createCuboidShape(3, 3, 6, 13, 13, 14)

    );

    public static final VoxelShape EAST = VoxelShapes.union(
            VoxelShapes.combine(DuctBlock.EAST, DuctBlock.FRAME, BooleanBiFunction.AND),
            Block.createCuboidShape(14, 2, 2, 16, 14, 14),
            Block.createCuboidShape(6, 3, 3, 14, 13, 13)
    );

    public static final VoxelShape WEST = VoxelShapes.union(
            VoxelShapes.combine(DuctBlock.WEST, DuctBlock.FRAME, BooleanBiFunction.AND),
            Block.createCuboidShape(0, 2, 2, 2, 14, 14),
            Block.createCuboidShape(2, 3, 3, 10, 13, 13)
    );

    private final int strength;

    public FanBlock(Settings settings, int strength) {
        super(settings);
        this.strength = strength;
        setDefaultState(getDefaultState().with(Properties.ENABLED, true));
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        super.appendProperties(builder);
        builder.add(Properties.ENABLED);
    }

    @Override
    public BlockEntity createBlockEntity(BlockView world) {
        return new FanBlockEntity(this.strength);
    }

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        switch (state.get(Properties.FACING)) {
            case DOWN:	return DOWN;
            case UP:	return UP;
            case NORTH:	return NORTH;
            case SOUTH: return SOUTH;
            case WEST:	return WEST;
            case EAST:	return EAST;
            default: throw new IllegalStateException("Unexpected value: " + state.get(Properties.FACING));
        }
    }
}
