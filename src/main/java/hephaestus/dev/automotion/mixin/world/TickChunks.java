package hephaestus.dev.automotion.mixin.world;

import hephaestus.dev.automotion.common.Automotion;
import net.minecraft.server.world.ChunkHolder;
import net.minecraft.server.world.ServerChunkManager;
import net.minecraft.world.SpawnHelper;
import net.minecraft.world.chunk.WorldChunk;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.util.Optional;

@Mixin(ServerChunkManager.class)
public class TickChunks {
	@Inject(method = "method_20801", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/world/ChunkHolder;getPos()Lnet/minecraft/util/math/ChunkPos;"), locals = LocalCapture.CAPTURE_FAILHARD)
	private void tickChunk(long l, boolean b1, SpawnHelper.Info info, boolean b2, int i, ChunkHolder chunkHolder, CallbackInfo ci, Optional<?> o1, Optional<?> o2, WorldChunk chunk) {
		Automotion.WATER_TEMPERATURE.of(chunk).tick();
	}
}
