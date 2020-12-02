package hephaestus.dev.automotion.client;

import net.minecraft.block.BlockState;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.util.math.MatrixStack;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class AutomotionBlockModels {
	private static final Set<BlockState> RENDERERS = new HashSet<>();

	public static boolean isHandlerRegistered(BlockState blockState) {
		return RENDERERS.contains(blockState);
	}

	public static void put(BlockState blockState) {
		RENDERERS.add(blockState);
	}
}
