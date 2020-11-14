package hephaestus.dev.automotion.client;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.RenderPhase;
import net.minecraft.client.render.VertexFormat;
import net.minecraft.client.render.VertexFormats;

public class AutomotionRenderLayers extends RenderLayer {
	private static final RenderPhase.Target TRANSLUCENT_TARGET = new RenderPhase.Target("automotion:translucent_target", () -> {
		if (MinecraftClient.isFabulousGraphicsOrBetter()) {
			MinecraftClient.getInstance().worldRenderer.getTranslucentFramebuffer().beginWrite(false);
		}

	}, () -> {
		if (MinecraftClient.isFabulousGraphicsOrBetter()) {
			MinecraftClient.getInstance().getFramebuffer().beginWrite(false);
		}

	});

	public static RenderLayer TRANSLUCENT = of("automotion:translucent", VertexFormats.POSITION_COLOR_LIGHT, 7, 2097152, true, true, RenderLayer.MultiPhaseParameters.builder().shadeModel(SMOOTH_SHADE_MODEL).lightmap(ENABLE_LIGHTMAP).transparency(TRANSLUCENT_TRANSPARENCY).build(true));

	public AutomotionRenderLayers(String name, VertexFormat vertexFormat, int drawMode, int expectedBufferSize, boolean hasCrumbling, boolean translucent, Runnable startAction, Runnable endAction) {
		super(name, vertexFormat, drawMode, expectedBufferSize, hasCrumbling, translucent, startAction, endAction);
	}
}
