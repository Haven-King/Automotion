package hephaestus.dev.automotion.client;

import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.RenderPhase;
import net.minecraft.client.render.VertexFormat;
import net.minecraft.client.render.VertexFormats;

public class AutomotionRenderLayers extends RenderLayer {
	public static RenderLayer TRANSLUCENT = of("automotion:translucent", VertexFormats.POSITION_COLOR_LIGHT, 7, 2097152, true, true, RenderLayer.MultiPhaseParameters.builder().shadeModel(SMOOTH_SHADE_MODEL).lightmap(ENABLE_LIGHTMAP).transparency(TRANSLUCENT_TRANSPARENCY).build(true));
	public static RenderLayer TRANSLUCENT_UNLIT = of("automotion:translucent", VertexFormats.POSITION_COLOR, 7, 262144, false, true, RenderLayer.MultiPhaseParameters.builder().shadeModel(SMOOTH_SHADE_MODEL).transparency(RenderPhase.TRANSLUCENT_TRANSPARENCY).target(TRANSLUCENT_TARGET).build(true));

	private AutomotionRenderLayers(String name, VertexFormat vertexFormat, int drawMode, int expectedBufferSize, boolean hasCrumbling, boolean translucent, Runnable startAction, Runnable endAction) {
		super(name, vertexFormat, drawMode, expectedBufferSize, hasCrumbling, translucent, startAction, endAction);
	}
}
