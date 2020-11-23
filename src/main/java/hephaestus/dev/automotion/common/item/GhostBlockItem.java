package hephaestus.dev.automotion.common.item;

import hephaestus.dev.automotion.client.AutomotionRenderLayers;
import hephaestus.dev.automotion.client.BlockFaceRenderers;
import hephaestus.dev.automotion.client.BlockOutlineRenderers;
import hephaestus.dev.automotion.client.WorldRendererCallback;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Matrix4f;

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

					boolean canPlaceAt = client.world.getBlockState(pos).canReplace(new ItemPlacementContext(client.player, Hand.MAIN_HAND, player.getStackInHand(Hand.MAIN_HAND), (BlockHitResult) client.crosshairTarget));

					if (!canPlaceAt) {
						pos = pos.offset(((BlockHitResult) client.crosshairTarget).getSide());
					}

					canPlaceAt = client.world.getBlockState(pos).canReplace(new ItemPlacementContext(client.player, Hand.MAIN_HAND, player.getStackInHand(Hand.MAIN_HAND), (BlockHitResult) client.crosshairTarget));

					matrixStack.push();
					matrixStack.translate(-camera.getPos().x, -camera.getPos().y, -camera.getPos().z);
					matrixStack.translate(pos.getX(), pos.getY(), pos.getZ());
//					matrixStack.multiply(Vector3f.POSITIVE_Y.getDegreesQuaternion(45));
					float scale = 1F - (1F / 60) + ((float) Math.sin((ticks + tickDelta) / 4.5F) / 60);
					matrixStack.translate(-0.5 * scale, -0.5 * scale, -0.5 * scale);
					matrixStack.scale(scale, scale, scale);
					matrixStack.translate(0.5 / scale, 0.5 / scale, 0.5 / scale);

//					VertexConsumer vertexConsumer1 = bufferBuilders.getBlockBufferBuilders().get(RenderLayers.getBlockLayer(blockState));
//					BakedModel model = client.getBlockRenderManager().getModel(blockState);
//					client.getBlockRenderManager().getModelRenderer().render(client.world, model, blockState, pos, matrixStack, vertexConsumer1, false, client.world.random, 0, 1);

					VertexConsumer vertexConsumer = bufferBuilders.getEffectVertexConsumers().getBuffer(AutomotionRenderLayers.TRANSLUCENT_UNLIT);
					Matrix4f matrix4f = matrixStack.peek().getModel();

					boolean valid = blockState.canPlaceAt(client.world, pos) && canPlaceAt;

					int c = 0;
					int r = valid ? c : 255, g = valid ? 255 : c, b = valid ? 64 : 0;
					int a = 64;

					if (BlockFaceRenderers.isHandlerRegistered(blockState)) {
						BlockFaceRenderers.render(matrixStack, vertexConsumer, blockState, r, g, b, a);
						VertexConsumer lines = bufferBuilders.getEffectVertexConsumers().getBuffer(RenderLayer.LINES);
						BlockOutlineRenderers.render(matrixStack, lines, blockState, a, g, b, a * 2);
					} else {
						blockState.getOutlineShape(client.world, ((BlockHitResult) client.crosshairTarget).getBlockPos()).forEachBox((minX, minY, minZ, maxX, maxY, maxZ) -> {
							// Bottom
							vertexConsumer.vertex(matrix4f, (float) maxX, (float) minY, (float) maxZ).color(r, g, b, a).next();
							vertexConsumer.vertex(matrix4f, (float) minX, (float) minY, (float) maxZ).color(r, g, b, a).next();
							vertexConsumer.vertex(matrix4f, (float) minX, (float) minY, (float) minZ).color(r, g, b, a).next();
							vertexConsumer.vertex(matrix4f, (float) maxX, (float) minY, (float) minZ).color(r, g, b, a).next();

							// Top
							vertexConsumer.vertex(matrix4f, (float) minX, (float) maxY, (float) maxZ).color(r, g, b, a).next();
							vertexConsumer.vertex(matrix4f, (float) maxX, (float) maxY, (float) maxZ).color(r, g, b, a).next();
							vertexConsumer.vertex(matrix4f, (float) maxX, (float) maxY, (float) minZ).color(r, g, b, a).next();
							vertexConsumer.vertex(matrix4f, (float) minX, (float) maxY, (float) minZ).color(r, g, b, a).next();

							// North
							vertexConsumer.vertex(matrix4f, (float) maxX, (float) minY, (float) minZ).color(r, g, b, a).next();
							vertexConsumer.vertex(matrix4f, (float) minX, (float) minY, (float) minZ).color(r, g, b, a).next();
							vertexConsumer.vertex(matrix4f, (float) minX, (float) maxY, (float) minZ).color(r, g, b, a).next();
							vertexConsumer.vertex(matrix4f, (float) maxX, (float) maxY, (float) minZ).color(r, g, b, a).next();

							// South
							vertexConsumer.vertex(matrix4f, (float) minX, (float) minY, (float) maxZ).color(r, g, b, a).next();
							vertexConsumer.vertex(matrix4f, (float) maxX, (float) minY, (float) maxZ).color(r, g, b, a).next();
							vertexConsumer.vertex(matrix4f, (float) maxX, (float) maxY, (float) maxZ).color(r, g, b, a).next();
							vertexConsumer.vertex(matrix4f, (float) minX, (float) maxY, (float) maxZ).color(r, g, b, a).next();

							// East
							vertexConsumer.vertex(matrix4f, (float) maxX, (float) minY, (float) maxZ).color(r, g, b, a).next();
							vertexConsumer.vertex(matrix4f, (float) maxX, (float) minY, (float) minZ).color(r, g, b, a).next();
							vertexConsumer.vertex(matrix4f, (float) maxX, (float) maxY, (float) minZ).color(r, g, b, a).next();
							vertexConsumer.vertex(matrix4f, (float) maxX, (float) maxY, (float) maxZ).color(r, g, b, a).next();

							// West
							vertexConsumer.vertex(matrix4f, (float) minX, (float) minY, (float) minZ).color(r, g, b, a).next();
							vertexConsumer.vertex(matrix4f, (float) minX, (float) minY, (float) maxZ).color(r, g, b, a).next();
							vertexConsumer.vertex(matrix4f, (float) minX, (float) maxY, (float) maxZ).color(r, g, b, a).next();
							vertexConsumer.vertex(matrix4f, (float) minX, (float) maxY, (float) minZ).color(r, g, b, a).next();
						});

						VertexConsumer lines = bufferBuilders.getEffectVertexConsumers().getBuffer(RenderLayer.LINES);
						blockState.getOutlineShape(client.world, pos).forEachEdge((minX, minY, minZ, maxX, maxY, maxZ) -> {
							lines.vertex(matrix4f, (float) (minX), (float) (minY), (float) (minZ)).color(r / 2, g / 2, b / 2, a * 2).next();
							lines.vertex(matrix4f, (float) (maxX), (float) (maxY), (float) (maxZ)).color(r / 2, g / 2, b / 2, a * 2).next();
						});
					}

					matrixStack.pop();
				}
			}
		});
	}
}
