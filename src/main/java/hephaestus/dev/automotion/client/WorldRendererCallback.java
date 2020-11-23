package hephaestus.dev.automotion.client;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.render.*;
import net.minecraft.client.util.math.MatrixStack;
import org.jetbrains.annotations.Nullable;

@Environment(EnvType.CLIENT)
public interface WorldRendererCallback {
	boolean CANVAS = FabricLoader.getInstance().isModLoaded("canvas");

	Event<WorldRendererCallback> EVENT = EventFactory.createArrayBacked(WorldRendererCallback.class, callbacks -> {
		return (bufferBuilders, renderer, matrixStack, ticks, tickDelta, camera, frustrum) -> {
			for (WorldRendererCallback callback : callbacks) {
				callback.render(bufferBuilders, renderer, matrixStack, ticks, tickDelta, camera, frustrum);
			}
		};
	});

	void render(BufferBuilderStorage bufferBuilders, WorldRenderer renderer, MatrixStack matrixStack, int ticks, float tickDelta, Camera camera, @Nullable Frustum frustum);
}
