package hephaestus.dev.automotion.common.util;

import net.minecraft.util.Identifier;
import net.minecraft.world.chunk.Chunk;

import java.util.HashMap;
import java.util.Map;

public class ChunkDataRegistry {
	private static final Map<Identifier, ChunkData.Factory<?>> FACTORIES = new HashMap<>();

	public static <T extends ChunkData> ChunkDataDefinition<T> register(Identifier id, ChunkData.Factory<T> factory) {
		FACTORIES.put(id, factory);
		return chunk -> get(id, chunk);
	}

	public static <T extends ChunkData> T get(Identifier id, Chunk chunk) {
		return ((ChunkDataHolder) chunk).getChunkData(id);
	}

	@SuppressWarnings("unchecked")
	public static <T extends ChunkData> T create(Identifier id, Chunk chunk) {
		return (T) FACTORIES.get(id).create(chunk);
	}

}
