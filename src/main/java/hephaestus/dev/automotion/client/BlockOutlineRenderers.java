package hephaestus.dev.automotion.client;

import net.minecraft.block.BlockState;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.util.math.MatrixStack;

import java.util.HashMap;
import java.util.Map;

public class BlockOutlineRenderers {
	private static final Map<BlockState, BlockOutlineRenderer> RENDERERS = new HashMap<>();

	public static boolean isHandlerRegistered(BlockState blockState) {
		return RENDERERS.containsKey(blockState);
	}

	public static void put(BlockState blockState, BlockOutlineRenderer renderer) {
		RENDERERS.put(blockState, renderer);
	}

	public static void render(MatrixStack matrixStack, VertexConsumer vertexConsumer, BlockState blockState, float r, float g, float b, float a) {
		RENDERERS.get(blockState).render(matrixStack, vertexConsumer, blockState, r, g, b, a);
	}

}
