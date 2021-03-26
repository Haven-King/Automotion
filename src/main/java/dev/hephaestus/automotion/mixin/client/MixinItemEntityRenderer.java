package dev.hephaestus.automotion.mixin.client;

import dev.hephaestus.automotion.common.util.EntityExtensions;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.ItemEntityRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.ItemEntity;
import net.minecraft.util.math.MathHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Environment(EnvType.CLIENT)
@Mixin(ItemEntityRenderer.class)
public class MixinItemEntityRenderer {
    @Unique private ItemEntity itemEntity;

    @Inject(method = "render", at = @At("HEAD"))
    private void captureItemEntity(ItemEntity itemEntity, float f, float g, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i, CallbackInfo ci) {
        this.itemEntity = itemEntity;
    }

    @Redirect(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/math/MathHelper;sin(F)F"))
    private float dontHoverOnConveyors(float f) {
        if (((EntityExtensions) this.itemEntity).isBeingConveyed()) {
            return -2.1F + MathHelper.sin(f) / 10;
        } else {
            return MathHelper.sin(f);
        }
    }

}
