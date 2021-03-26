package dev.hephaestus.automotion.mixin.worldgen;

import dev.hephaestus.automotion.common.AutomotionWorldgen;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.DefaultBiomeCreator;
import net.minecraft.world.biome.GenerationSettings;
import net.minecraft.world.biome.SpawnSettings;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(DefaultBiomeCreator.class)
public abstract class MixinDefaultBiomeCreator {
    @Shadow
    private static Biome createOcean(SpawnSettings.Builder spawnSettings, int waterColor, int waterFogColor, boolean deep, GenerationSettings.Builder builder) {
        throw new UnsupportedOperationException();
    }

    @Redirect(method = "createWarmOcean", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/biome/DefaultBiomeCreator;createOcean(Lnet/minecraft/world/biome/SpawnSettings$Builder;IIZLnet/minecraft/world/biome/GenerationSettings$Builder;)Lnet/minecraft/world/biome/Biome;", opcode = Opcodes.GETFIELD))
    private static Biome changeWarmSurfaceBuilder(SpawnSettings.Builder spawnSettings, int waterColor, int waterFogColor, boolean deep, GenerationSettings.Builder builder) {
        return createOcean(spawnSettings, waterColor, waterFogColor, deep, builder.surfaceBuilder(AutomotionWorldgen.LIMESTONE));
    }

    @Redirect(method = "createLukewarmOcean", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/biome/DefaultBiomeCreator;createOcean(Lnet/minecraft/world/biome/SpawnSettings$Builder;IIZLnet/minecraft/world/biome/GenerationSettings$Builder;)Lnet/minecraft/world/biome/Biome;", opcode = Opcodes.GETFIELD))
    private static Biome changeLukwarmSurfaceBuilder(SpawnSettings.Builder spawnSettings, int waterColor, int waterFogColor, boolean deep, GenerationSettings.Builder builder) {
        return createOcean(spawnSettings, waterColor, waterFogColor, deep, builder.surfaceBuilder(AutomotionWorldgen.LIMESTONE));
    }
}
