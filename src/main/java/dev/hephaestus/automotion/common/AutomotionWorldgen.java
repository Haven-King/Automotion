package dev.hephaestus.automotion.common;

import dev.hephaestus.automotion.Automotion;
import dev.hephaestus.automotion.common.worldgen.SubstrateFeature;
import dev.hephaestus.automotion.common.worldgen.SubstrateFeatureConfig;
import net.fabricmc.fabric.api.biome.v1.BiomeModifications;
import net.fabricmc.fabric.api.biome.v1.BiomeSelectors;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.BiomeKeys;
import net.minecraft.world.gen.GenerationStep;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.FeatureConfig;

import java.util.Collection;
import java.util.HashSet;

public class AutomotionWorldgen {
    private static final Collection<Biome> WARM_OCEANS = new HashSet<>();
    public static final Feature<SubstrateFeatureConfig> SUBSTRATE = register("substrate", new SubstrateFeature(SubstrateFeatureConfig.CODEC));

    public static void init() {
        BiomeModifications.addFeature(BiomeSelectors.includeByKey(BiomeKeys.WARM_OCEAN, BiomeKeys.LUKEWARM_OCEAN), GenerationStep.Feature.LOCAL_MODIFICATIONS, RegistryKey.of(Registry.CONFIGURED_FEATURE_WORLDGEN, Automotion.id("limestone_deposits")));
    }

    private static <C extends FeatureConfig, F extends Feature<C>> F register(String id, F feature) {
        return Registry.register(Registry.FEATURE, Automotion.id(id), feature);
    }

    public static void registerWarmOceanBiome(Biome biome) {
        WARM_OCEANS.add(biome);
    }

    public static boolean isWarmOcean(Biome biome) {
        return WARM_OCEANS.contains(biome);
    }
}
