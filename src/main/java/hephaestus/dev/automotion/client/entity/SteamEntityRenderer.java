package hephaestus.dev.automotion.client.entity;

import hephaestus.dev.automotion.client.AutomotionRenderLayers;
import hephaestus.dev.automotion.common.entity.SteamCloudEntity;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Matrix4f;

import java.util.Random;

public class SteamEntityRenderer extends EntityRenderer<SteamCloudEntity> {
	private static final Identifier TEXTURE = new Identifier("textures/entity/zombie/zombie.png");
	private final Random random = new Random();

	public SteamEntityRenderer(EntityRenderDispatcher dispatcher) {
		super(dispatcher);
	}

	@Override
	public Identifier getTexture(SteamCloudEntity entity) {
		return TEXTURE;
	}

	@Override
	public void render(SteamCloudEntity entity, float yaw, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light) {
		matrices.push();
		random.setSeed(entity.getUuid().getMostSignificantBits());
		float size = (float) ((float) MathHelper.clamp(entity.getTemperature() / 100F, 0F, 1F) - (Math.sin((entity.age + random.nextDouble() * 10) / 7.5F) + 1) / 7.5F) - 0.01F;

		int c = (int) (235 - 20 * random.nextDouble());

		//noinspection UnnecessaryLocalVariable
		int r = c, g = c, b = c;
		int a = 225;

		float x = 0, y = 0, z = 0;

		float minX = x - size / 2, minY = y - size / 2, minZ = z - size / 2;
		float maxX = x + size / 2, maxY = y + size / 2, maxZ = z + size / 2;
		
		VertexConsumer consumer = vertexConsumers.getBuffer(AutomotionRenderLayers.TRANSLUCENT);
		Matrix4f matrix4f = matrices.peek().getModel();

		int NSD = -15;
		int EWD = -7;
		int BD = -30;

		// Bottom
		consumer.vertex(matrix4f, maxX, minY, maxZ).color(r + BD, g + BD, b + BD, a).light(light).next();
		consumer.vertex(matrix4f, minX, minY, maxZ).color(r + BD, g + BD, b + BD, a).light(light).next();
		consumer.vertex(matrix4f, minX, minY, minZ).color(r + BD, g + BD, b + BD, a).light(light).next();
		consumer.vertex(matrix4f, maxX, minY, minZ).color(r + BD, g + BD, b + BD, a).light(light).next();

		// Top
		consumer.vertex(matrix4f, minX, maxY, maxZ).color(c, c, c, a).light(light).next();
		consumer.vertex(matrix4f, maxX, maxY, maxZ).color(c, c, c, a).light(light).next();
		consumer.vertex(matrix4f, maxX, maxY, minZ).color(c, c, c, a).light(light).next();
		consumer.vertex(matrix4f, minX, maxY, minZ).color(c, c, c, a).light(light).next();

		// North
		consumer.vertex(matrix4f, maxX, minY, minZ).color(c + NSD, c + NSD, c + NSD, a).light(light).next();
		consumer.vertex(matrix4f, minX, minY, minZ).color(c + NSD, c + NSD, c + NSD, a).light(light).next();
		consumer.vertex(matrix4f, minX, maxY, minZ).color(c + NSD, c + NSD, c + NSD, a).light(light).next();
		consumer.vertex(matrix4f, maxX, maxY, minZ).color(c + NSD, c + NSD, c + NSD, a).light(light).next();

		// South
		consumer.vertex(matrix4f, minX, minY, maxZ).color(c + NSD, c + NSD, c + NSD, a).light(light).next();
		consumer.vertex(matrix4f, maxX, minY, maxZ).color(c + NSD, c + NSD, c + NSD, a).light(light).next();
		consumer.vertex(matrix4f, maxX, maxY, maxZ).color(c + NSD, c + NSD, c + NSD, a).light(light).next();
		consumer.vertex(matrix4f, minX, maxY, maxZ).color(c + NSD, c + NSD, c + NSD, a).light(light).next();

		// East
		consumer.vertex(matrix4f, maxX, minY, maxZ).color(c + EWD, c + EWD, c + EWD, a).light(light).next();
		consumer.vertex(matrix4f, maxX, minY, minZ).color(c + EWD, c + EWD, c + EWD, a).light(light).next();
		consumer.vertex(matrix4f, maxX, maxY, minZ).color(c + EWD, c + EWD, c + EWD, a).light(light).next();
		consumer.vertex(matrix4f, maxX, maxY, maxZ).color(c + EWD, c + EWD, c + EWD, a).light(light).next();

		// West
		consumer.vertex(matrix4f, minX, minY, minZ).color(c + EWD, c + EWD, c + EWD, a).light(light).next();
		consumer.vertex(matrix4f, minX, minY, maxZ).color(c + EWD, c + EWD, c + EWD, a).light(light).next();
		consumer.vertex(matrix4f, minX, maxY, maxZ).color(c + EWD, c + EWD, c + EWD, a).light(light).next();
		consumer.vertex(matrix4f, minX, maxY, minZ).color(c + EWD, c + EWD, c + EWD, a).light(light).next();

		matrices.pop();
	}
}
