package hephaestus.dev.automotion.mixin.client.render;

import hephaestus.dev.automotion.client.BlockOutlineRenderers;
import hephaestus.dev.automotion.client.WorldRendererCallback;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.BlockState;
import net.minecraft.client.render.*;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Matrix4f;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(WorldRenderer.class)
@Environment(EnvType.CLIENT)
public class WorldRendererMixin {
	@Shadow private @Nullable Frustum capturedFrustum;

	@Shadow @Final private BufferBuilderStorage bufferBuilders;

	@Shadow private int ticks;

	@Inject(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/WorldRenderer;renderLayer(Lnet/minecraft/client/render/RenderLayer;Lnet/minecraft/client/util/math/MatrixStack;DDD)V", ordinal = 2, shift= At.Shift.AFTER))
	private void worldRendererCallback(MatrixStack matrices, float tickDelta, long limitTime, boolean renderBlockOutline, Camera camera, GameRenderer gameRenderer, LightmapTextureManager lightmapTextureManager, Matrix4f matrix4f, CallbackInfo ci) {
		WorldRendererCallback.EVENT.invoker().render(bufferBuilders, (WorldRenderer) (Object) this, matrices, this.ticks, tickDelta, camera, capturedFrustum);
	}

	@Inject(method = "drawBlockOutline", at = @At("HEAD"), cancellable = true)
	private void drawBlockOutline(MatrixStack matrixStack, VertexConsumer vertexConsumer, Entity entity, double d, double e, double f, BlockPos blockPos, BlockState blockState, CallbackInfo ci) {
		if (BlockOutlineRenderers.isHandlerRegistered(blockState)) {

			float x = (float) (blockPos.getX() - d);
			float y = (float) (blockPos.getY() - e);
			float z = (float) (blockPos.getZ() - f);

			matrixStack.push();
			matrixStack.translate(x, y, z);
			BlockOutlineRenderers.render(matrixStack, vertexConsumer, blockState, 0F, 0F, 0F, 0.4F);

			matrixStack.pop();
			ci.cancel();
		}
	}
}
