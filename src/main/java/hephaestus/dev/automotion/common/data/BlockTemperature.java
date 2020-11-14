package hephaestus.dev.automotion.common.data;

import hephaestus.dev.automotion.common.block.HeatTickable;
import hephaestus.dev.automotion.common.util.ChunkData;
import net.minecraft.block.Block;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.ProtoChunk;
import net.minecraft.world.chunk.WorldChunk;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class BlockTemperature implements ChunkData {
	private final Chunk chunk;
	private final Map<BlockPos, Integer> temperatures = new ConcurrentHashMap<>();

	public BlockTemperature(Chunk chunk) {
		this.chunk = chunk;
	}

	@Override
	public Identifier getId() {
		return null;
	}

	@Override
	public void copy(ProtoChunk from, WorldChunk to) {

	}

	@Override
	public CompoundTag serialize(ServerWorld world, Chunk chunk, CompoundTag compoundTag) {
		for (Map.Entry<BlockPos, Integer> entry : temperatures.entrySet()) {
			compoundTag.putInt(String.valueOf(entry.getKey().asLong()), entry.getValue());
		}

		return compoundTag;
	}

	@Override
	public CompoundTag deserialize(ServerWorld world, Chunk chunk, CompoundTag tag) {
		for (String key : tag.getKeys()) {
			temperatures.put(BlockPos.fromLong(Long.parseLong(key)), tag.getInt(key));
		}

		return tag;
	}

	public void heat(BlockPos pos, int amount) {
		temperatures.merge(pos, amount, Integer::sum);
	}

	public void tick() {
		if (this.chunk instanceof WorldChunk && !((WorldChunk) this.chunk).getWorld().isClient) {
			for (BlockPos key : temperatures.keySet()) {
				temperatures.merge(key, -1, Integer::sum);

				Block block = chunk.getBlockState(key).getBlock();

				if (block instanceof HeatTickable) {
					((HeatTickable) block).heatTick(((WorldChunk) chunk).getWorld(), key, temperatures.get(key));
				}

				block = chunk.getBlockState(key).getBlock();

				if (temperatures.get(key) <= 0 || !(block instanceof HeatTickable)) {
					temperatures.remove(key);
				}
			}
		}
	}
}
