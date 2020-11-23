package hephaestus.dev.automotion.client;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.BlockState;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.util.math.MatrixStack;

@Environment(EnvType.CLIENT)
public interface BlockFaceRenderer {
	void render(MatrixStack matrixStack, VertexConsumer vertexConsumer, BlockState blockState, float r, float g, float b, float a);
}
