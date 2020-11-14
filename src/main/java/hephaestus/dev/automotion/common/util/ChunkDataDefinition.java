package hephaestus.dev.automotion.common.util;

import net.minecraft.world.chunk.Chunk;

public interface ChunkDataDefinition<T extends ChunkData> {
	T of(Chunk chunk);
}
