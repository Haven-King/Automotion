package dev.hephaestus.automotion.common.worldgen;

import com.mojang.serialization.Codec;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.Heightmap;
import net.minecraft.world.StructureWorldAccess;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.util.FeatureContext;

public class SubstrateFeature extends Feature<SubstrateFeatureConfig> {

    public SubstrateFeature(Codec<SubstrateFeatureConfig> configCodec) {
        super(configCodec);
    }

    @Override
    public boolean generate(FeatureContext<SubstrateFeatureConfig> context) {
        BlockState state = context.getConfig().state;

        StructureWorldAccess world = context.getWorld();
        ChunkGenerator generator = context.getGenerator();

        int x = context.getOrigin().getX();
        int z = context.getOrigin().getZ();

        BlockPos.Mutable mut = new BlockPos.Mutable();

        for (int dX = 0; dX < 16; ++dX) {
            for (int dZ = 0; dZ < 16; ++dZ) {
                mut.set(x + dX, generator.getHeight(x + dX, z + dZ, Heightmap.Type.OCEAN_FLOOR_WG, world) - 5, z + dZ);

                for (int dY = 0; dY < 10 + context.getRandom().nextInt(2); ++dY) {
                    if (world.getBlockState(mut.move(Direction.DOWN)).isOf(Blocks.STONE)) {
                        this.setBlockState(world, mut, state);
                    }
                }
            }
        }

        return true;
    }
}
