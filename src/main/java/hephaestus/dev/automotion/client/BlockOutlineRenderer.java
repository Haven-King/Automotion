package hephaestus.dev.automotion.client;

import hephaestus.dev.automotion.common.block.transportation.conveyors.ConveyorBelt;
import net.minecraft.block.BlockState;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.util.math.Vector3f;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Matrix4f;

public class BlockOutlineRenderer {
	public static void drawBlockOutline(MatrixStack matrixStack, VertexConsumer vertexConsumer, Entity entity, float x, float y, float z, BlockPos blockPos, BlockState blockState) {
		matrixStack.push();

		x = blockPos.getX() - x;
		y = blockPos.getY() - y;
		z = blockPos.getZ() - z;

		matrixStack.translate(x + 0.5, y + 0.5, z + 0.5);

		switch (blockState.get(ConveyorBelt.FACING)) {
			case EAST:
				matrixStack.multiply(Vector3f.POSITIVE_Y.getDegreesQuaternion(90));
				break;
			case NORTH:
				matrixStack.multiply(Vector3f.POSITIVE_Y.getDegreesQuaternion(180));
				break;
			case WEST:
				matrixStack.multiply(Vector3f.POSITIVE_Y.getDegreesQuaternion(270));
				break;
		}

		if (blockState.get(ConveyorBelt.ANGLE) == ConveyorBelt.Angle.DOWN) {
			matrixStack.multiply(Vector3f.POSITIVE_Y.getDegreesQuaternion(180));
		}

		matrixStack.translate(-x - 0.5, -y - 0.5, -z - 0.5);

		Matrix4f matrix4f = matrixStack.peek().getModel();

		// Bottom face
		vertexConsumer.vertex(matrix4f,  x,  y,  z).color(0, 0, 0, 0.4F).next();
		vertexConsumer.vertex(matrix4f,  x,  y + 3/16F,  z).color(0, 0, 0, 0.4F).next();

		vertexConsumer.vertex(matrix4f,  x + 1,  y,  z).color(0, 0, 0, 0.4F).next();
		vertexConsumer.vertex(matrix4f,  x + 1,  y + 3/16F,  z).color(0, 0, 0, 0.4F).next();

		vertexConsumer.vertex(matrix4f,  x,  y,  z).color(0, 0, 0, 0.4F).next();
		vertexConsumer.vertex(matrix4f,  x + 1,  y,  z).color(0, 0, 0, 0.4F).next();

		vertexConsumer.vertex(matrix4f,  x,  y + 3/16F,  z).color(0, 0, 0, 0.4F).next();
		vertexConsumer.vertex(matrix4f,  x + 1,  y + 3/16F,  z).color(0, 0, 0, 0.4F).next();

		// Top face
		vertexConsumer.vertex(matrix4f,  x,  y + 1,  z + 1).color(0, 0, 0, 0.4F).next();
		vertexConsumer.vertex(matrix4f,  x,  y + 19/16F,  z + 1).color(0, 0, 0, 0.4F).next();

		vertexConsumer.vertex(matrix4f,  x + 1,  y + 1,  z + 1).color(0, 0, 0, 0.4F).next();
		vertexConsumer.vertex(matrix4f,  x + 1,  y + 19/16F,  z + 1).color(0, 0, 0, 0.4F).next();

		vertexConsumer.vertex(matrix4f,  x,  y + 1,  z + 1).color(0, 0, 0, 0.4F).next();
		vertexConsumer.vertex(matrix4f,  x + 1,  y + 1,  z + 1).color(0, 0, 0, 0.4F).next();

		vertexConsumer.vertex(matrix4f,  x,  y + 19/16F,  z + 1).color(0, 0, 0, 0.4F).next();
		vertexConsumer.vertex(matrix4f,  x + 1,  y + 19/16F,  z + 1).color(0, 0, 0, 0.4F).next();

		// Left rail
		vertexConsumer.vertex(matrix4f, x + 1, y, z).color(0, 0, 0, 0.4F).next();
		vertexConsumer.vertex(matrix4f, x + 1, y + 1, z + 1).color(0, 0, 0, 0.4F).next();

		vertexConsumer.vertex(matrix4f, x + 1, y + 3/16F, z).color(0, 0, 0, 0.4F).next();
		vertexConsumer.vertex(matrix4f, x + 1, y + 19/16F, z + 1).color(0, 0, 0, 0.4F).next();

		// Right rail
		vertexConsumer.vertex(matrix4f, x, y, z).color(0, 0, 0, 0.4F).next();
		vertexConsumer.vertex(matrix4f, x, y + 1, z + 1).color(0, 0, 0, 0.4F).next();

		vertexConsumer.vertex(matrix4f, x, y + 3/16F, z).color(0, 0, 0, 0.4F).next();
		vertexConsumer.vertex(matrix4f, x, y + 19/16F, z + 1).color(0, 0, 0, 0.4F).next();
		matrixStack.pop();
	}

	public static boolean shouldHandle(BlockState state) {
		return state.getBlock() instanceof ConveyorBelt && ((ConveyorBelt) state.getBlock()).canSlope && state.get(ConveyorBelt.ANGLE) != ConveyorBelt.Angle.FLAT;
	}
}
