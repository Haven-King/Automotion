package hephaestus.dev.automotion.common.entity;

import hephaestus.dev.automotion.common.AutomotionEntities;
import hephaestus.dev.automotion.common.AutomotionNetworking;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.network.PacketContext;
import net.minecraft.block.ShapeContext;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityType;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;
import net.minecraft.util.collection.ReusableStream;
import net.minecraft.util.function.BooleanBiFunction;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.World;

import java.util.stream.Stream;

public class SteamCloudEntity extends AutomotionEntity {
	private int temperature;

	public SteamCloudEntity(EntityType<?> type, World world) {
		super(type, world);
		this.inanimate = true;
		this.stepHeight = 0.25F;
	}

	@Override
	public Identifier createSpawnPacket(PacketByteBuf buf) {
		super.createSpawnPacket(buf);
		buf.writeVarInt(this.temperature);

		return AutomotionNetworking.SPAWN_STEAM;
	}

	public SteamCloudEntity init(int temperature) {
		this.temperature = temperature;
		this.setVelocity(0, Math.sqrt((this.temperature)) / 50D, 0);
		return this;
	}

	@Override
	public void tick() {
		this.temperature--;

		if (this.temperature <= 0) {
			this.remove();
		}

//		this.move(MovementType.SELF, this.getVelocity());

		for (SteamCloudEntity entity : this.world.getEntitiesByType(AutomotionEntities.STEAM, this.getBoundingBox(), e -> true)) {
			if (entity.temperature < this.temperature && !entity.removed && entity.temperature > 0) {
				this.temperature++;
				entity.temperature--;
			}
		}

		this.setVelocity(this.getVelocity().x / 2, Math.sqrt((this.temperature)) / 50D, this.getVelocity().z / 2);

		for (SteamCloudEntity entity : this.world.getEntitiesByType(AutomotionEntities.STEAM, this.getBoundingBox().expand(1D, 1D, 1D), e -> true)) {
			if (entity.temperature > this.temperature && !entity.removed) {
				this.addVelocity(
						(entity.getX() - this.getX()) / 100,
						(entity.getY() - this.getY()) / 100,
						(entity.getZ() - this.getZ()) / 100
				);
			}
		}

		Vec3d velocity = this.adjustMovementForCollisions(this.getVelocity());
		this.updatePosition(this.getX() + velocity.x, this.getY() + velocity.y, this.getZ() + velocity.z);
	}

	private Vec3d adjustMovementForCollisions(Vec3d movement) {
		Box box = this.getBoundingBox();
		ShapeContext shapeContext = ShapeContext.of(this);
		VoxelShape voxelShape = this.world.getWorldBorder().asVoxelShape();
		Stream<VoxelShape> stream = VoxelShapes.matchesAnywhere(voxelShape, VoxelShapes.cuboid(box.contract(1.0E-7D)), BooleanBiFunction.AND) ? Stream.empty() : Stream.of(voxelShape);
		Stream<VoxelShape> stream2 = this.world.getEntityCollisions(this, box.stretch(movement), (entity) -> true);
		ReusableStream<VoxelShape> reusableStream = new ReusableStream<>(Stream.concat(stream2, stream));
		Vec3d vec3d = movement.lengthSquared() == 0.0D ? movement : adjustMovementForCollisions(this, movement, box, this.world, shapeContext, reusableStream);
		boolean bl = movement.x != vec3d.x;
		boolean bl2 = movement.y != vec3d.y;
		boolean bl3 = movement.z != vec3d.z;
		boolean bl4 = this.onGround || bl2 && movement.y < 0.0D;
		if (this.stepHeight > 0.0F && bl4 && (bl || bl3)) {
			Vec3d vec3d2 = adjustMovementForCollisions(this, new Vec3d(movement.x, this.stepHeight, movement.z), box, this.world, shapeContext, reusableStream);
			Vec3d vec3d3 = adjustMovementForCollisions(this, new Vec3d(0.0D, this.stepHeight, 0.0D), box.stretch(movement.x, 0.0D, movement.z), this.world, shapeContext, reusableStream);
			if (vec3d3.y < (double)this.stepHeight) {
				Vec3d vec3d4 = adjustMovementForCollisions(this, new Vec3d(movement.x, 0.0D, movement.z), box.offset(vec3d3), this.world, shapeContext, reusableStream).add(vec3d3);
				if (squaredHorizontalLength(vec3d4) > squaredHorizontalLength(vec3d2)) {
					vec3d2 = vec3d4;
				}
			}

			if (squaredHorizontalLength(vec3d2) > squaredHorizontalLength(vec3d)) {
				return vec3d2.add(adjustMovementForCollisions(this, new Vec3d(0.0D, -vec3d2.y + movement.y, 0.0D), box.offset(vec3d2), this.world, shapeContext, reusableStream));
			}
		}

		return vec3d;
	}

	@Override
	protected boolean canClimb() {
		return false;
	}

	@Override
	public boolean collides() {
		return !this.removed;
	}

	@Override
	public boolean isAttackable() {
		return false;
	}

	@Override
	protected void readCustomDataFromTag(CompoundTag tag) {
		this.temperature = tag.getInt("Temperature");
	}

	@Override
	protected void writeCustomDataToTag(CompoundTag tag) {
		tag.putInt("Temperature", this.temperature);
	}

	@Override
	public boolean isTouchingWater() {
		return false;
	}

	@Override
	public void remove() {
		super.remove();
	}

	@Environment(EnvType.CLIENT)
	public static void spawn(PacketContext ctx, PacketByteBuf buf) {
		EntityData data = new EntityData(buf);
		int temperature = buf.readVarInt();

		ctx.getTaskQueue().execute(() -> {
			SteamCloudEntity entity = new SteamCloudEntity(AutomotionEntities.STEAM, ctx.getPlayer().world);
			entity.init(temperature);
			entity.updateTrackedPosition(data.x, data.y, data.z);
			entity.refreshPositionAfterTeleport(data.x, data.y, data.z);
			entity.setEntityId(data.id);
			entity.setUuid(data.uuid);
			((ClientWorld) ctx.getPlayer().world).addEntity(data.id, entity);
		});
	}

	@Environment(EnvType.CLIENT)
	public boolean doesRenderOnFire() {
		return false;
	}

	public double getTemperature() {
		return this.temperature;
	}
}
