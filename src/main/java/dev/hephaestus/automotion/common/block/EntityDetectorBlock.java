package dev.hephaestus.automotion.common.block;

import dev.hephaestus.automotion.common.block.entity.EntityDetectorBlockEntity;
import net.minecraft.block.Block;
import net.minecraft.block.BlockEntityProvider;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class EntityDetectorBlock extends Block implements BlockEntityProvider {
    public EntityDetectorBlock(Settings settings) {
        super(settings);
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(Properties.FACING, Properties.POWERED);
    }

    @Nullable
    @Override
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        return this.getDefaultState().with(Properties.FACING, ctx.getPlayerLookDirection().getOpposite()).with(Properties.POWERED, false);
    }

    @Nullable
    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new EntityDetectorBlockEntity(pos, state);
    }

    public int getWeakRedstonePower(BlockState state, BlockView world, BlockPos pos, Direction direction) {
        return state.get(Properties.POWERED) ? 15 : 0;
    }

    public int getStrongRedstonePower(BlockState state, BlockView world, BlockPos pos, Direction direction) {
        return state.get(Properties.POWERED) && state.get(Properties.FACING).getOpposite() == direction ? 15 : 0;
    }

    public boolean emitsRedstonePower(BlockState state) {
        return true;
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(World world, BlockState state, BlockEntityType<T> type) {
        return (world1, pos, state1, blockEntity) -> {
            Direction direction = state.get(Properties.FACING);
            world1.setBlockState(pos, state.with(Properties.POWERED, !world.getOtherEntities(null, new Box(pos.offset(direction, direction.getAxis() == Direction.Axis.Y ? 2 : 1))).isEmpty()));
        };
    }
}
