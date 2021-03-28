package dev.hephaestus.automotion.mixin.worldgen;

import dev.hephaestus.automotion.common.AutomotionBlocks;
import dev.hephaestus.automotion.common.AutomotionWorldgen;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.gen.surfacebuilder.ConfiguredSurfaceBuilders;
import net.minecraft.world.gen.surfacebuilder.DefaultSurfaceBuilder;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Random;

@Mixin(DefaultSurfaceBuilder.class)
public class MixinDefaultSurfaceBuilder {
    @Inject(method = "generate(Ljava/util/Random;Lnet/minecraft/world/chunk/Chunk;Lnet/minecraft/world/biome/Biome;IIIDLnet/minecraft/block/BlockState;Lnet/minecraft/block/BlockState;Lnet/minecraft/block/BlockState;Lnet/minecraft/block/BlockState;Lnet/minecraft/block/BlockState;I)V", at = @At("TAIL"))
    private void doThing(Random random, Chunk chunk, Biome biome, int x, int z, int height, double noise, BlockState defaultBlock, BlockState fluidBlock, BlockState topBlock, BlockState underBlock, BlockState underwaterBlock, int seaLevel, CallbackInfo ci) {
        if (AutomotionWorldgen.isWarmOcean(biome)) {
            BlockPos.Mutable mut = new BlockPos.Mutable(x, height, z);

            while (!chunk.getBlockState(mut).isOf(Blocks.STONE) && !chunk.isOutOfHeightLimit(mut)) mut.move(Direction.DOWN);

            for (int i = 0; i < 5 && !chunk.isOutOfHeightLimit(mut); ++i, mut.move(Direction.DOWN)) {
                if (chunk.getBlockState(mut).isOf(Blocks.STONE)) {
                    chunk.setBlockState(mut, AutomotionBlocks.LIMESTONE.getDefaultState(), false);
                }
            }
        }
    }

}
