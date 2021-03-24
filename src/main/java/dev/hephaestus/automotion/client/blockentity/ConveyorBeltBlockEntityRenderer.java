package dev.hephaestus.automotion.client.blockentity;

import com.mojang.blaze3d.systems.RenderSystem;
import dev.hephaestus.automotion.common.block.entity.ConveyorBeltBlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Matrix4f;

public class ConveyorBeltBlockEntityRenderer implements BlockEntityRenderer<ConveyorBeltBlockEntity> {
    @Override
    public void render(ConveyorBeltBlockEntity entity, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay) {
        HitResult hitResult = MinecraftClient.getInstance().crosshairTarget;
        if (hitResult != null && hitResult.getType() == HitResult.Type.BLOCK && ((BlockHitResult) hitResult).getBlockPos().equals(entity.getPos())) {
            VertexConsumer consumer = vertexConsumers.getBuffer(RenderLayer.getLines());

            BlockPos pivot = entity.getPivot();

            if (pivot != null) {
                int x = pivot.getX() - entity.getPos().getX();
                int z = pivot.getZ() - entity.getPos().getZ();

                Matrix4f matrix4f = matrices.peek().getModel();

                RenderSystem.lineWidth(3);
                consumer.vertex(matrix4f, x, 0, z).color(0F, 1F, 1F, 0.75F).normal(1, 0, -1).next();
                consumer.vertex(matrix4f, x, 255, z).color(0F, 1F, 1F, 0.75F).normal(1, 0, -1).next();
            }
        }
    }
}
