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
import net.minecraft.world.WorldView;

public class FanBlock extends DuctAttachmentBlock implements BlockEntityProvider {
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
    public boolean canConnect(BlockState state, Connectable other, Direction direction) {
        return direction == state.get(Properties.FACING) && super.canConnect(state, other, direction);
    }
}
