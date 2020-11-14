package hephaestus.dev.automotion.mixin.world;

import hephaestus.dev.automotion.common.util.ChunkData;
import hephaestus.dev.automotion.common.util.ChunkDataHolder;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;
import net.minecraft.world.chunk.ProtoChunk;
import net.minecraft.world.chunk.WorldChunk;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Map;

@Mixin(WorldChunk.class)
public abstract class UpgradeChunkData implements ChunkDataHolder {
	@Inject(method = "<init>(Lnet/minecraft/world/World;Lnet/minecraft/world/chunk/ProtoChunk;)V", at = @At("TAIL"))
	private void copy(World world, ProtoChunk protoChunk, CallbackInfo ci) {
		for (Map.Entry<Identifier, ChunkData> entry : ((ChunkDataHolder) protoChunk).getChunkData().entrySet()) {
			entry.getValue().copy(protoChunk, (WorldChunk) (Object) this);
		}
	}
}
