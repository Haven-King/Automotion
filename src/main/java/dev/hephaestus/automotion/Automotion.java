package dev.hephaestus.automotion;

import dev.hephaestus.automotion.common.*;
import dev.hephaestus.automotion.common.networking.AlternatePlaceHandler;
import net.fabricmc.api.ModInitializer;
import net.minecraft.util.Identifier;

public class Automotion implements ModInitializer {
    @Override
    public void onInitialize() {
        AutomotionBlocks.init();
        AutomotionItems.init();
        AutomotionEntities.init();
        AutomotionSounds.init();
        AutomotionWorldgen.init();
        AlternatePlaceHandler.init();
    }

    public static Identifier id(String path, String... paths) {
        return new Identifier("automotion", path + (paths.length > 0 ? String.join("/", paths) : ""));
    }
}
