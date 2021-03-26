package dev.hephaestus.automotion.common;

import dev.hephaestus.automotion.Automotion;
import dev.hephaestus.automotion.common.worldgen.SubstrateSurfaceBuilder;
import dev.hephaestus.automotion.common.worldgen.SubstrateSurfaceConfig;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.FeatureConfig;
import net.minecraft.world.gen.surfacebuilder.ConfiguredSurfaceBuilder;
import net.minecraft.world.gen.surfacebuilder.SurfaceBuilder;

public class AutomotionWorldgen {
    private static final BlockState SAND = Blocks.SAND.getDefaultState();

    public static final SurfaceBuilder<SubstrateSurfaceConfig> SUBSTRATE = Registry.register(Registry.SURFACE_BUILDER, Automotion.id("substrate"), new SubstrateSurfaceBuilder(SubstrateSurfaceConfig.CODEC));
    public static final ConfiguredSurfaceBuilder<SubstrateSurfaceConfig> LIMESTONE = SUBSTRATE.withConfig(new SubstrateSurfaceConfig(
            SAND, SAND, SAND, AutomotionBlocks.LIMESTONE.getDefaultState()
    ));

    public static void init() {
    }

    private static <C extends FeatureConfig, F extends Feature<C>> F register(String id, F feature) {
        return Registry.register(Registry.FEATURE, Automotion.id(id), feature);
    }
}
