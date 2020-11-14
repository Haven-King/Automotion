package hephaestus.dev.automotion.common.block;

import hephaestus.dev.automotion.common.block.entity.FanBlockEntity;
import net.minecraft.block.Block;
import net.minecraft.block.BlockEntityProvider;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;

public class FanBlock extends Block implements BlockEntityProvider, Connectable {
    private final int strength;
    public FanBlock(Settings settings, int strength) {
        super(settings);
        this.strength = strength;
        setDefaultState(getDefaultState().with(Properties.ENABLED, true).with(Properties.FACING, Direction.NORTH));
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        super.appendProperties(builder);
        builder.add(Properties.ENABLED, Properties.FACING);
    }

    @Override
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        return this.getDefaultState().with(Properties.FACING, ctx.getSide().getOpposite());
    }

    @Override
    public void neighborUpdate(BlockState state, World world, BlockPos pos, Block block, BlockPos fromPos, boolean notify) {
        Direction facing = state.get(Properties.FACING);
        BlockState neighbor = world.getBlockState(pos.offset(facing));
        if (!(neighbor.getBlock() instanceof Connectable && ((Connectable)neighbor.getBlock()).canConnect(neighbor, facing.getOpposite()))) {
            world.breakBlock(pos, true, null);
            world.updateNeighborsAlways(pos, this);
        }
    }

    @Override
    public BlockEntity createBlockEntity(BlockView world) {
        return new FanBlockEntity(this.strength);
    }

    @Override
    public boolean canConnect(BlockState state, Direction direction) {
        return direction == state.get(Properties.FACING);
    }
}
