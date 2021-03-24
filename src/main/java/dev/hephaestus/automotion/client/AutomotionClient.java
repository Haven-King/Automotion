package dev.hephaestus.automotion.client;

import dev.hephaestus.automotion.client.blockentity.ConveyorBeltBlockEntityRenderer;
import dev.hephaestus.automotion.client.entity.PhysicsBlockEntityRenderer;
import dev.hephaestus.automotion.common.AutomotionBlocks;
import dev.hephaestus.automotion.common.AutomotionEntities;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.rendereregistry.v1.BlockEntityRendererRegistry;
import net.fabricmc.fabric.api.client.rendereregistry.v1.EntityRendererRegistry;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.util.registry.Registry;

@Environment(EnvType.CLIENT)
public class AutomotionClient implements ClientModInitializer {
//    public static final KeyBinding ALT = KeyBindingHelper.registerKeyBinding(new KeyBinding())

    @Override
    public void onInitializeClient() {
        EntityRendererRegistry.INSTANCE.register(AutomotionEntities.PHYSICS_BLOCK, PhysicsBlockEntityRenderer::new);

        if (FabricLoader.getInstance().isDevelopmentEnvironment()) {
            BlockEntityRendererRegistry.INSTANCE.register(AutomotionBlocks.CONVEYOR_BELT_BLOCK_ENTITY, ctx -> new ConveyorBeltBlockEntityRenderer());
        }
    }
}
