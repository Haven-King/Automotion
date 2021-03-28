package dev.hephaestus.automotion.mixin.worldgen;

import dev.hephaestus.automotion.common.AutomotionWorldgen;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.DefaultBiomeCreator;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(DefaultBiomeCreator.class)
public class MixinDefaultBiomeCreator {
    @Inject(method = "createWarmOcean", at = @At("RETURN"))
    private static void captureWarmOcean(CallbackInfoReturnable<Biome> cir) {
        AutomotionWorldgen.registerWarmOceanBiome(cir.getReturnValue());
    }

    @Inject(method = "createLukewarmOcean", at = @At("RETURN"))
    private static void captureLukwarmOcean(boolean deep, CallbackInfoReturnable<Biome> cir) {
        if (!deep) {
            AutomotionWorldgen.registerWarmOceanBiome(cir.getReturnValue());
        }
    }
}
