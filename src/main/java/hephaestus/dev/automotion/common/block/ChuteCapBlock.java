package hephaestus.dev.automotion.common.block;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.ShapeContext;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;

import static net.minecraft.state.property.Properties.*;
import static net.minecraft.state.property.Properties.WEST;

public class ChuteCapBlock extends ChuteBlock {
    public static VoxelShape CAP = Block.createCuboidShape(0, 13, 0, 16, 16, 16);

    public ChuteCapBlock(Settings settings) {
        super(settings);
    }

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return VoxelShapes.union(super.getOutlineShape(state, world, pos, context), CAP);
    }

    @Override
    protected BlockState makeConnections(BlockState state, World world, BlockPos pos) {
        BlockState neighbor;
        state = state.with(NORTH, (neighbor = world.getBlockState(pos.north())).getBlock() instanceof Connectable);

        state = state.with(SOUTH, (neighbor = world.getBlockState(pos.south())).getBlock() instanceof Connectable);

        state = state.with(EAST, (neighbor = world.getBlockState(pos.east())).getBlock() instanceof Connectable);

        state = state.with(WEST, (neighbor = world.getBlockState(pos.west())).getBlock() instanceof Connectable);

        return state;
    }
}
