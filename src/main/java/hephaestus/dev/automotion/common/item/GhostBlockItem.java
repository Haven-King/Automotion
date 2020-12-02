package hephaestus.dev.automotion.common.item;

import hephaestus.dev.automotion.client.AutomotionRenderLayers;
import hephaestus.dev.automotion.client.AutomotionBlockModels;
import hephaestus.dev.automotion.client.WorldRendererCallback;
import hephaestus.dev.automotion.client.model.AutomotionModel;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.render.model.BakedQuad;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.util.math.Vector3f;
import net.minecraft.client.util.math.Vector4f;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.state.property.Properties;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Matrix4f;
import net.minecraft.util.math.Vec3i;
import org.lwjgl.system.MemoryStack;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.util.List;

public class GhostBlockItem extends BlockItem {
	public GhostBlockItem(Block block, Settings settings) {
		super(block, settings);
	}

	@Environment(EnvType.CLIENT)
	public static void initClient() {
		WorldRendererCallback.EVENT.register((bufferBuilders, renderer, matrixStack, ticks, tickDelta, camera, frustum) -> {
			MinecraftClient client = MinecraftClient.getInstance();
			ClientPlayerEntity player = client.player;

			ItemStack stack;
			if (player != null && (stack = player.getStackInHand(Hand.MAIN_HAND)).getItem() instanceof GhostBlockItem && client.crosshairTarget instanceof BlockHitResult && client.crosshairTarget.getType() != HitResult.Type.MISS && client.world != null) {
				BlockState blockState = ((GhostBlockItem) player.getStackInHand(Hand.MAIN_HAND).getItem()).getBlock().getPlacementState(new ItemPlacementContext(player, Hand.MAIN_HAND, stack, (BlockHitResult) client.crosshairTarget));

				if (blockState != null) {
					BlockPos pos = ((BlockHitResult) client.crosshairTarget).getBlockPos();
					BakedModel model = client.getBlockRenderManager().getModel(blockState);
					List<BakedQuad> quads = model.getQuads(blockState, null, client.world.random);


					boolean canPlaceAt = client.world.getBlockState(pos).canReplace(new ItemPlacementContext(client.player, Hand.MAIN_HAND, player.getStackInHand(Hand.MAIN_HAND), (BlockHitResult) client.crosshairTarget));

					if (!canPlaceAt) {
						pos = pos.offset(((BlockHitResult) client.crosshairTarget).getSide());
					}

					canPlaceAt = client.world.getBlockState(pos).canReplace(new ItemPlacementContext(client.player, Hand.MAIN_HAND, player.getStackInHand(Hand.MAIN_HAND), (BlockHitResult) client.crosshairTarget));

					matrixStack.push();
					matrixStack.translate(-camera.getPos().x, -camera.getPos().y, -camera.getPos().z);
					matrixStack.translate(pos.getX(), pos.getY(), pos.getZ());
//					matrixStack.multiply(Vector3f.POSITIVE_Y.getDegreesQuaternion(45));

					if (blockState.contains(Properties.HORIZONTAL_FACING) && AutomotionBlockModels.isHandlerRegistered(blockState)) {
						matrixStack.translate(0.5, 0.5, 0.5);

						float r = AutomotionModel.angle(blockState);

						matrixStack.multiply(Vector3f.POSITIVE_Y.getDegreesQuaternion(r));
						matrixStack.translate(-0.5, -0.5, -0.5);
					}

					float scale = 1F - (1F / 60) + ((float) Math.sin((ticks + tickDelta) / 4.5F) / 60);
					matrixStack.translate(-0.5 * scale, -0.5 * scale, -0.5 * scale);
					matrixStack.scale(scale, scale, scale);
					matrixStack.translate(0.5 / scale, 0.5 / scale, 0.5 / scale);

//					VertexConsumer vertexConsumer1 = bufferBuilders.getBlockBufferBuilders().get(RenderLayers.getBlockLayer(blockState));
//					BakedModel model = client.getBlockRenderManager().getModel(blockState);
//					client.getBlockRenderManager().getModelRenderer().render(client.world, model, blockState, pos, matrixStack, vertexConsumer1, false, client.world.random, 0, 1);



					boolean valid = blockState.canPlaceAt(client.world, pos) && canPlaceAt;

					int c = 0;
					int r = valid ? c : 255, g = valid ? 255 : c, b = valid ? 64 : 0;
					int a = 64;

					MatrixStack.Entry entry = matrixStack.peek();

					// TODO: Make this work on Fabulous graphics.
					VertexConsumer faces = bufferBuilders.getEffectVertexConsumers().getBuffer(AutomotionRenderLayers.TRANSLUCENT_UNLIT);
					for (BakedQuad quad : quads) {
						render(quad, entry, faces, r, g, b, a);
					}

					VertexConsumer lines = bufferBuilders.getEffectVertexConsumers().getBuffer(RenderLayer.LINES);
					for (BakedQuad quad : quads) {
						renderLines(quad, entry, lines, r, g, b, a);
					}

					matrixStack.pop();
				}
			}
		});
	}

	private static void render(BakedQuad quad, MatrixStack.Entry entry, VertexConsumer consumer, float r, float g, float b, float a) {
		int[] is = quad.getVertexData();
		Vec3i vec3i = quad.getFace().getVector();
		Vector3f vector3f = new Vector3f((float)vec3i.getX(), (float)vec3i.getY(), (float)vec3i.getZ());
		Matrix4f matrix4f = entry.getModel();
		vector3f.transform(entry.getNormal());

		int j = is.length / 8;
		MemoryStack memoryStack = MemoryStack.stackPush();
		Throwable var17 = null;

		try {
			ByteBuffer byteBuffer = memoryStack.malloc(VertexFormats.POSITION_COLOR_TEXTURE_LIGHT_NORMAL.getVertexSize());
			IntBuffer intBuffer = byteBuffer.asIntBuffer();

			for(int k = 0; k < j; ++k) {
				intBuffer.clear();
				intBuffer.put(is, k * 8, 8);
				float x = byteBuffer.getFloat(0);
				float y = byteBuffer.getFloat(4);
				float z = byteBuffer.getFloat(8);
				float v;
				float w;

				int u = -1;
				v = byteBuffer.getFloat(16);
				w = byteBuffer.getFloat(20);
				Vector4f vector4f = new Vector4f(x, y, z, 1.0F);
				vector4f.transform(matrix4f);
				consumer.vertex(vector4f.getX(), vector4f.getY(), vector4f.getZ(), r/255F, g/255F, b/255F, a/255F, v, w, 1, u, vector3f.getX(), vector3f.getY(), vector3f.getZ());
			}
		} catch (Throwable var38) {
			var17 = var38;
			throw var38;
		} finally {
			if (var17 != null) {
				try {
					memoryStack.close();
				} catch (Throwable var37) {
					var17.addSuppressed(var37);
				}
			} else {
				memoryStack.close();
			}
		}
	}

	private static void renderLines(BakedQuad quad, MatrixStack.Entry entry, VertexConsumer consumer, float r, float g, float b, float a) {
		int[] is = quad.getVertexData();
		Vec3i vec3i = quad.getFace().getVector();
		Vector3f vector3f = new Vector3f((float)vec3i.getX(), (float)vec3i.getY(), (float)vec3i.getZ());
		Matrix4f matrix4f = entry.getModel();
		vector3f.transform(entry.getNormal());

		int j = is.length / 8;
		MemoryStack memoryStack = MemoryStack.stackPush();
		Throwable var17 = null;

		try {
			ByteBuffer byteBuffer = memoryStack.malloc(VertexFormats.POSITION_COLOR_TEXTURE_LIGHT_NORMAL.getVertexSize());
			IntBuffer intBuffer = byteBuffer.asIntBuffer();

			for(int k = 0; k < j; ++k) {
				{
					intBuffer.clear();
					intBuffer.put(is, k * 8, 8);
					float x = byteBuffer.getFloat(0);
					float y = byteBuffer.getFloat(4);
					float z = byteBuffer.getFloat(8);
					float v;
					float w;

					int u = -1;
					v = byteBuffer.getFloat(16);
					w = byteBuffer.getFloat(20);
					Vector4f vector4f = new Vector4f(x, y, z, 1.0F);
					vector4f.transform(matrix4f);
					consumer.vertex(vector4f.getX(), vector4f.getY(), vector4f.getZ(), r / 255F, g / 255F, b / 255F, a / 255F, v, w, 1, u, vector3f.getX(), vector3f.getY(), vector3f.getZ());
				}
				{
					intBuffer.clear();
					intBuffer.put(is, ((k + 1) % j) * 8, 8);
					float x = byteBuffer.getFloat(0);
					float y = byteBuffer.getFloat(4);
					float z = byteBuffer.getFloat(8);
					float v;
					float w;

					int u = -1;
					v = byteBuffer.getFloat(16);
					w = byteBuffer.getFloat(20);
					Vector4f vector4f = new Vector4f(x, y, z, 1.0F);
					vector4f.transform(matrix4f);
					consumer.vertex(vector4f.getX(), vector4f.getY(), vector4f.getZ(), r/255F, g/255F, b/255F, a/128F, v, w, 1, u, vector3f.getX(), vector3f.getY(), vector3f.getZ());
				}
			}
		} catch (Throwable var38) {
			var17 = var38;
			throw var38;
		} finally {
			if (var17 != null) {
				try {
					memoryStack.close();
				} catch (Throwable var37) {
					var17.addSuppressed(var37);
				}
			} else {
				memoryStack.close();
			}
		}
	}
}
