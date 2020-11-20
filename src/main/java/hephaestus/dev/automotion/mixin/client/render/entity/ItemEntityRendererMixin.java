package hephaestus.dev.automotion.mixin.client.render.entity;

import hephaestus.dev.automotion.common.item.Conveyable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.ItemEntityRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.ItemEntity;
import net.minecraft.util.math.MathHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ItemEntityRenderer.class)
@Environment(EnvType.CLIENT)
public class ItemEntityRendererMixin {
//	@Redirect(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/ItemEntity;method_27314(F)F"))
//	private float dontRotateOnConveyors(ItemEntity itemEntity, float f) {
//		if (itemEntity.world.getBlockState(itemEntity.getBlockPos()).getBlock() == Conveyance.CONVEYOR_BELT) {
//			return 0F;
//		} else {
//			return itemEntity.method_27314(f);
//		}
//	}
//
	private ItemEntity itemEntity;
	@Inject(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/math/MathHelper;sin(F)F"))
	private void captureItemEntity(ItemEntity itemEntity, float f, float g, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i, CallbackInfo ci) {
		this.itemEntity = itemEntity;
	}

	@Redirect(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/math/MathHelper;sin(F)F"))
	private float dontHoverOnConveyors(float f) {
		if (((Conveyable) this.itemEntity).isBeingConveyed()) {
			return -2.1F + MathHelper.sin(f) / 10;
		} else {
			return MathHelper.sin(f);
		}
	}
}
