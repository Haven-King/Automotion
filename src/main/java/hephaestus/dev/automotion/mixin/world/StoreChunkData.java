package hephaestus.dev.automotion.mixin.world;

import hephaestus.dev.automotion.common.util.ChunkData;
import hephaestus.dev.automotion.common.util.ChunkDataHolder;
import hephaestus.dev.automotion.common.util.ChunkDataRegistry;
import net.minecraft.util.Identifier;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.ProtoChunk;
import net.minecraft.world.chunk.WorldChunk;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

import java.util.HashMap;
import java.util.Map;

@Mixin(value = {WorldChunk.class, ProtoChunk.class})
public class StoreChunkData implements ChunkDataHolder {
	@Unique
	private final Map<Identifier, ChunkData> chunkData = new HashMap<>();

	@Override
	public Map<Identifier, ChunkData> getChunkData() {
		return chunkData;
	}

	@Override
	public void put(Identifier id, ChunkData data) {
		chunkData.put(id, data);
	}

	@Override
	@SuppressWarnings("unchecked")
	public <T extends ChunkData> T getChunkData(Identifier id) {
		chunkData.putIfAbsent(id, ChunkDataRegistry.create(id, (Chunk) this));
		return (T) chunkData.get(id);
	}
}
