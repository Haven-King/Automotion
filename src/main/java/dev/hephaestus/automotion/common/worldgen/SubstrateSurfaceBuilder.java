package dev.hephaestus.automotion.common.worldgen;

import com.mojang.serialization.Codec;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.gen.surfacebuilder.SurfaceBuilder;

import java.util.Random;

public class SubstrateSurfaceBuilder extends SurfaceBuilder<SubstrateSurfaceConfig> {
    public SubstrateSurfaceBuilder(Codec<SubstrateSurfaceConfig> codec) {
        super(codec);
    }

    @Override
    public void generate(Random random, Chunk chunk, Biome biome, int x, int z, int height, double noise, BlockState defaultBlock, BlockState defaultFluid, int seaLevel, long seed, SubstrateSurfaceConfig config) {
        this.generate(random, chunk, biome, x, z, height, noise, defaultBlock, defaultFluid, config.getTopMaterial(), config.getUnderMaterial(), config.getUnderwaterMaterial(), config.getSubstrateMaterial(), seaLevel);
    }

    protected void generate(Random random, Chunk chunk, Biome biome, int x, int z, int height, double noise, BlockState defaultBlock, BlockState defaultFluid, BlockState topBlock, BlockState underBlock, BlockState underwaterBlock, BlockState substrateBlock, int seaLevel) {
        BlockPos.Mutable mutable = new BlockPos.Mutable();
        int surfaceDepth = (int)(noise / 3.0D + 3.0D + random.nextDouble() * 0.25D);
        int substrateDepth = (int) (noise / 3D + 3D + random.nextDouble());

        int blocksLeft;

        BlockState blockState5;
        if (surfaceDepth == 0) {
            boolean bl = false;

            for(blocksLeft = height; blocksLeft >= 50; --blocksLeft) {
                mutable.set(x, blocksLeft, z);
                BlockState blockState = chunk.getBlockState(mutable);
                if (blockState.isAir()) {
                    bl = false;
                } else if (blockState.isOf(defaultBlock.getBlock())) {
                    if (!bl) {
                        if (blocksLeft >= seaLevel) {
                            blockState5 = Blocks.AIR.getDefaultState();
                        } else if (blocksLeft == seaLevel - 1) {
                            blockState5 = biome.getTemperature(mutable) < 0.15F ? Blocks.ICE.getDefaultState() : defaultFluid;
                        } else if (blocksLeft >= seaLevel - (7 + surfaceDepth)) {
                            blockState5 = substrateBlock;
                        } else {
                            blockState5 = underwaterBlock;
                        }

                        chunk.setBlockState(mutable, blockState5, false);
                    }

                    bl = true;
                }
            }
        } else {
            BlockState blockState6 = underBlock;
            blocksLeft = -1;

            for(int l = height; l >= 50; --l) {
                mutable.set(x, l, z);
                blockState5 = chunk.getBlockState(mutable);
                if (blockState5.isAir()) {
                    blocksLeft = -1;
                } else if (blockState5.isOf(defaultBlock.getBlock())) {
                    if (blocksLeft == -1) {
                        blocksLeft = surfaceDepth + substrateDepth;
                        BlockState blockState12;
                        if (l >= seaLevel + 2) {
                            blockState12 = topBlock;
                        } else if (l >= seaLevel - 1) {
                            blockState6 = underBlock;
                            blockState12 = topBlock;
                        } else if (l >= seaLevel - 4) {
                            blockState6 = underBlock;
                            blockState12 = underBlock;
                        } else if (l >= seaLevel - (7 + surfaceDepth)) {
                            blockState12 = blockState6;
                        } else {
                            blockState6 = defaultBlock;
                            blockState12 = underwaterBlock;
                        }

                        chunk.setBlockState(mutable, blockState12, false);
                    } else if (blocksLeft > 0) {
                        --blocksLeft;

                        if (blocksLeft > substrateDepth && !blockState6.equals(defaultBlock)) {
                            chunk.setBlockState(mutable, blockState6, false);
                        } else {
                            chunk.setBlockState(mutable, substrateBlock, false);
                        }

                        if (blocksLeft == 0 && blockState6.isOf(Blocks.SAND) && surfaceDepth > 1) {
                            blocksLeft = random.nextInt(4) + Math.max(0, l - seaLevel);
                            blockState6 = blockState6.isOf(Blocks.RED_SAND) ? Blocks.RED_SANDSTONE.getDefaultState() : Blocks.SANDSTONE.getDefaultState();
                        }
                    }
                }
            }
        }
    }
}
