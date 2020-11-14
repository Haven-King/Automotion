package hephaestus.dev.automotion.mixin.client.render;

import hephaestus.dev.automotion.client.BlockOutlineRenderer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.BlockState;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(WorldRenderer.class)
@Environment(EnvType.CLIENT)
public class WorldRendererMixin {
	@Inject(method = "drawBlockOutline", at = @At("HEAD"), cancellable = true)
	private void drawBlockOutline(MatrixStack matrixStack, VertexConsumer vertexConsumer, Entity entity, double d, double e, double f, BlockPos blockPos, BlockState blockState, CallbackInfo ci) {
		if (BlockOutlineRenderer.shouldHandle(blockState)) {
			BlockOutlineRenderer.drawBlockOutline(matrixStack, vertexConsumer, entity, (float) d, (float) e, (float) f, blockPos, blockState);
			ci.cancel();
		}
	}
}
