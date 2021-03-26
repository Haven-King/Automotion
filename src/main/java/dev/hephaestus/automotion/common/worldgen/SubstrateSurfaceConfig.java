package dev.hephaestus.automotion.common.worldgen;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.block.BlockState;
import net.minecraft.world.gen.surfacebuilder.SurfaceConfig;

public class SubstrateSurfaceConfig implements SurfaceConfig {
    public static final Codec<SubstrateSurfaceConfig> CODEC = RecordCodecBuilder.create((instance) -> {
        return instance.group(BlockState.CODEC.fieldOf("top_material").forGetter((config) -> {
            return config.topMaterial;
        }), BlockState.CODEC.fieldOf("under_material").forGetter((config) -> {
            return config.underMaterial;
        }), BlockState.CODEC.fieldOf("underwater_material").forGetter((config) -> {
            return config.underwaterMaterial;
        }), BlockState.CODEC.fieldOf("substrate_material").forGetter((config) -> {
            return config.substrateMaterial;
        })).apply(instance, (SubstrateSurfaceConfig::new));
    });
    private final BlockState topMaterial;
    private final BlockState underMaterial;
    private final BlockState underwaterMaterial;
    private final BlockState substrateMaterial;

    public SubstrateSurfaceConfig(BlockState topMaterial, BlockState underMaterial, BlockState underwaterMaterial, BlockState substrateMaterial) {
        this.topMaterial = topMaterial;
        this.underMaterial = underMaterial;
        this.underwaterMaterial = underwaterMaterial;
        this.substrateMaterial = substrateMaterial;
    }

    public BlockState getTopMaterial() {
        return this.topMaterial;
    }

    public BlockState getUnderMaterial() {
        return this.underMaterial;
    }

    public BlockState getUnderwaterMaterial() {
        return this.underwaterMaterial;
    }

    public BlockState getSubstrateMaterial() {
        return this.substrateMaterial;
    }
}
