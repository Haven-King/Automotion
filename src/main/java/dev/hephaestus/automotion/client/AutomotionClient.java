package dev.hephaestus.automotion.client;

import dev.hephaestus.automotion.Automotion;
import dev.hephaestus.automotion.client.model.ConveyorBeltModel;
import dev.hephaestus.automotion.common.AutomotionBlocks;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.model.ModelLoadingRegistry;
import net.fabricmc.fabric.api.client.model.ModelProviderContext;
import net.fabricmc.fabric.api.client.model.ModelResourceProvider;
import net.fabricmc.fabric.api.client.rendering.v1.ColorProviderRegistry;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.render.model.UnbakedModel;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;
import org.lwjgl.glfw.GLFW;

import java.util.HashMap;

@Environment(EnvType.CLIENT)
public class AutomotionClient implements ClientModInitializer, ModelResourceProvider {
    public static final KeyBinding ALT = KeyBindingHelper.registerKeyBinding(new KeyBinding("automotion.key.alt_placement", GLFW.GLFW_KEY_LEFT_ALT, "key.categories.automotion"));
    private static final HashMap<Identifier, UnbakedModel> MODELS = new HashMap<>();

    static {
        MODELS.put(Automotion.id("block/conveyor_belt"), new ConveyorBeltModel());
        MODELS.put(Automotion.id("item/conveyor_belt"), new ConveyorBeltModel());

    }

    @Override
    public void onInitializeClient() {
        ModelLoadingRegistry.INSTANCE.registerResourceProvider(rm -> this);

        ColorProviderRegistry.BLOCK.register((state, world, pos, tintIndex) -> {
            return tintIndex == 0 ? 0xFF88CC : 0xFFFFFF;
        }, AutomotionBlocks.CONVEYOR_BELT);
    }

    @Override
    public @Nullable UnbakedModel loadModelResource(Identifier identifier, ModelProviderContext modelProviderContext) {
        return MODELS.get(identifier);
    }
}
