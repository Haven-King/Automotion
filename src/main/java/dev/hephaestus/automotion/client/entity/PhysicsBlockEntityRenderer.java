package dev.hephaestus.automotion.client.entity;

import dev.hephaestus.automotion.common.entity.PhysicsBlockEntity;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayers;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.BlockRenderManager;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.Random;

public class PhysicsBlockEntityRenderer extends EntityRenderer<PhysicsBlockEntity> {
    public PhysicsBlockEntityRenderer(EntityRendererFactory.Context ctx) {
        super(ctx);
    }

    @Override
    public Identifier getTexture(PhysicsBlockEntity entity) {
        return null;
    }

    @Override
    public void render(PhysicsBlockEntity entity, float yaw, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light) {
        BlockState blockState = entity.getBlockState();
        if (blockState.getRenderType() == BlockRenderType.MODEL) {
            World world = entity.world;
            if (blockState != world.getBlockState(entity.getBlockPos()) && blockState.getRenderType() != BlockRenderType.INVISIBLE) {
                matrices.push();
                BlockPos blockPos = new BlockPos(entity.getX(), entity.getBoundingBox().maxY, entity.getZ());
                matrices.translate(-0.5D, 0.0D, -0.5D);
                BlockRenderManager blockRenderManager = MinecraftClient.getInstance().getBlockRenderManager();
                blockRenderManager.getModelRenderer().render(world, blockRenderManager.getModel(blockState), blockState, blockPos, matrices, vertexConsumers.getBuffer(RenderLayers.getMovingBlockLayer(blockState)), false, new Random(), 0, OverlayTexture.DEFAULT_UV);
                matrices.pop();
                super.render(entity, yaw, tickDelta, matrices, vertexConsumers, light);
            }
        }
    }
}
