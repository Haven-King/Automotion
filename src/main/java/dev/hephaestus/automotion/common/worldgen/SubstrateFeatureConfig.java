package dev.hephaestus.automotion.common.worldgen;

import com.mojang.serialization.Codec;
import net.minecraft.block.BlockState;
import net.minecraft.world.gen.feature.FeatureConfig;

public class SubstrateFeatureConfig implements FeatureConfig {
    public static final Codec<SubstrateFeatureConfig> CODEC;
    public final BlockState state;

    public SubstrateFeatureConfig(BlockState state) {
        this.state = state;
    }

    static {
        CODEC = BlockState.CODEC.fieldOf("state").xmap(SubstrateFeatureConfig::new, (config) -> {
            return config.state;
        }).codec();
    }

}
