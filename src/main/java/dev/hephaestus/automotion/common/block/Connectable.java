package dev.hephaestus.automotion.common.block;

import net.minecraft.block.BlockState;
import net.minecraft.util.math.Direction;

public interface Connectable {
    boolean canConnect(BlockState state, Connectable other, Direction direction);
}
