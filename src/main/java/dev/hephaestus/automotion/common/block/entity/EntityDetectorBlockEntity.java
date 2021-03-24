package dev.hephaestus.automotion.common.block.entity;

import dev.hephaestus.automotion.common.AutomotionBlocks;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.util.math.BlockPos;

public class EntityDetectorBlockEntity extends BlockEntity {
    public EntityDetectorBlockEntity(BlockPos pos, BlockState state) {
        super(AutomotionBlocks.BASIC_ENTITY_DETECTOR_BLOCK_ENTITY, pos, state);
    }
}
