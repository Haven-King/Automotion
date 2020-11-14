package hephaestus.dev.automotion.common.block;

import net.minecraft.block.BlockState;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class GlassChuteBlock extends ChuteBlock {
    public GlassChuteBlock(Settings settings) {
        super(settings);
    }

    @Override
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        return super.getPlacementState(ctx)
            .with(Properties.NORTH, false)
            .with(Properties.EAST, false)
            .with(Properties.SOUTH, false)
            .with(Properties.WEST, false)
        ;
    }

    @Override
    protected BlockState makeConnections(BlockState state, World world, BlockPos pos) {
        return state;
    }
}
