package hephaestus.dev.automotion.common.util;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.ProtoChunk;
import net.minecraft.world.chunk.WorldChunk;

public interface ChunkData {
	Identifier getId();
	void copy(ProtoChunk from, WorldChunk to);
	CompoundTag serialize(ServerWorld world, Chunk chunk, CompoundTag compoundTag);
	CompoundTag deserialize(ServerWorld world, Chunk chunk, CompoundTag tag);

	interface Factory<T extends ChunkData> {
		T create(Chunk chunk);
	}
}
