package dev.hephaestus.automotion;

import dev.hephaestus.automotion.common.AutomotionBlocks;
import dev.hephaestus.automotion.common.AutomotionEntities;
import dev.hephaestus.automotion.common.AutomotionItems;
import net.fabricmc.api.ModInitializer;
import net.minecraft.util.Identifier;

public class Automotion implements ModInitializer {
    @Override
    public void onInitialize() {
        AutomotionBlocks.init();
        AutomotionItems.init();
        AutomotionEntities.init();
    }

    public static Identifier id(String path, String... paths) {
        return new Identifier("automotion", path + (paths.length > 0 ? String.join("/", paths) : ""));
    }
}
