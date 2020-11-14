package hephaestus.dev.automotion.mixin.client.render;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerEntity;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(GameRenderer.class)
@Environment(EnvType.CLIENT)
public abstract class GameRendererMixin {
	@Inject(method = "bobView", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/math/MathHelper;lerp(FFF)F"), cancellable = true, locals = LocalCapture.CAPTURE_FAILHARD)
	private void dontBobUnlessMovingWillingly(MatrixStack matrixStack, float f, CallbackInfo ci, PlayerEntity playerEntity) {
		ClientPlayerEntity player = (ClientPlayerEntity)playerEntity;
		if (!player.input.pressingBack && !player.input.pressingForward && !player.input.pressingLeft && !player.input.pressingRight)
			ci.cancel();
	}
}
