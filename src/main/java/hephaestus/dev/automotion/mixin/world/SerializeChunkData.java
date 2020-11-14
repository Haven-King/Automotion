package hephaestus.dev.automotion.mixin.world;

import hephaestus.dev.automotion.common.util.ChunkData;
import hephaestus.dev.automotion.common.util.ChunkDataHolder;
import hephaestus.dev.automotion.common.util.ChunkDataRegistry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.structure.StructureManager;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.ChunkSerializer;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.ProtoChunk;
import net.minecraft.world.chunk.ReadOnlyChunk;
import net.minecraft.world.poi.PointOfInterestStorage;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Map;

@Mixin(ChunkSerializer.class)
public class SerializeChunkData {
	@Inject(method = "serialize", at = @At("RETURN"))
	private static void serializeChunkData(ServerWorld world, Chunk chunk, CallbackInfoReturnable<CompoundTag> cir) {
		if (chunk instanceof ChunkDataHolder) {
			CompoundTag tag = getOrCreate(cir.getReturnValue(), "AutomotionChunkData");

			for (Map.Entry<Identifier, ChunkData> entry : ((ChunkDataHolder) chunk).getChunkData().entrySet()) {
				tag.put(entry.getKey().toString(), entry.getValue().serialize(world, chunk, new CompoundTag()));
			}
		}
	}

	@Inject(method = "deserialize", at = @At("RETURN"))
	private static void deserializeChunkData(ServerWorld world, StructureManager structureManager, PointOfInterestStorage poiStorage, ChunkPos pos, CompoundTag tag, CallbackInfoReturnable<ProtoChunk> cir) {
		Chunk chunk = cir.getReturnValue();
		ChunkDataHolder chunkData = chunk instanceof ReadOnlyChunk
				? (ChunkDataHolder) ((ReadOnlyChunk) chunk).getWrappedChunk()
				: (ChunkDataHolder) chunk;

		tag = getOrCreate(tag, "AutomotionChunkData");

		for (String key : tag.getKeys()) {
			chunkData.getChunkData(new Identifier(key)).deserialize(world, cir.getReturnValue(), tag.getCompound(key));
		}
	}

	private static CompoundTag getOrCreate(CompoundTag tag, String key) {
		return getOrCreate(tag, key, new CompoundTag());
	}

	@SuppressWarnings("unchecked")
	private static <T extends Tag> T getOrCreate(CompoundTag tag, String key, T newTag) {
		if (tag.contains(key, newTag.getType())) {
			return (T) tag.get(key);
		} else {
			tag.put(key, newTag);
			return newTag;
		}
	}
}
