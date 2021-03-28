package dev.hephaestus.automotion.common.block.entity;

import dev.hephaestus.automotion.common.AutomotionBlocks;
import net.minecraft.block.BlockState;
import net.minecraft.block.CampfireBlock;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.Random;

public class CokeOvenBlockEntity extends BlockEntity {
    public CokeOvenBlockEntity(BlockPos pos, BlockState state) {
        super(AutomotionBlocks.COKE_OVEN_BLOCK_ENTITY, pos, state);
    }

    public static <T extends BlockEntity> void clientTick(World world, BlockPos pos, BlockState state, T blockEntity) {
        Random random = world.random;
        int j;
        if (random.nextFloat() < 0.11F) {
            for(j = 0; j < random.nextInt(2) + 2; ++j) {
                CampfireBlock.spawnSmokeParticle(world, pos, true, true);
            }
        }
    }
}
