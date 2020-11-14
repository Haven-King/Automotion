package hephaestus.dev.automotion.common.entity;

import io.netty.buffer.Unpooled;
import net.fabricmc.fabric.api.network.ServerSidePacketRegistry;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.packet.s2c.play.EntitySpawnS2CPacket;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;

import java.util.UUID;

public abstract class AutomotionEntity extends Entity {
	public AutomotionEntity(EntityType<?> type, World world) {
		super(type, world);
	}

	@Override
	protected void initDataTracker() {

	}

	public Identifier createSpawnPacket(PacketByteBuf buf) {
		buf.writeVarInt(this.getEntityId());
		buf.writeUuid(this.getUuid());
		buf.writeDouble(this.getX());
		buf.writeDouble(this.getY());
		buf.writeDouble(this.getZ());

		return null;
	}

	@Override
	public Packet<?> createSpawnPacket() {
		for (ServerPlayerEntity playerEntity : ((ServerWorld) this.world).getPlayers()) {
			PacketByteBuf buf = new PacketByteBuf(Unpooled.buffer());
			ServerSidePacketRegistry.INSTANCE.sendToPlayer(playerEntity, this.createSpawnPacket(buf), buf);
		}

		return new EntitySpawnS2CPacket(this);
	}

	protected static class EntityData {
		public final int id;
		public final UUID uuid;
		public final double x;
		public final double y;
		public final double z;

		protected EntityData(PacketByteBuf buf) {
			this.id = buf.readVarInt();
			this.uuid = buf.readUuid();
			this.x = buf.readDouble();
			this.y = buf.readDouble();
			this.z = buf.readDouble();
		}
	}
}
