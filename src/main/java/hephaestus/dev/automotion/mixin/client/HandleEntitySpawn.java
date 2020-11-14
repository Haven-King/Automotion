package hephaestus.dev.automotion.mixin.client;

import hephaestus.dev.automotion.common.AutomotionEntities;
import hephaestus.dev.automotion.common.entity.SteamCloudEntity;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.network.packet.s2c.play.EntitySpawnS2CPacket;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Environment(EnvType.CLIENT)
@Mixin(ClientPlayNetworkHandler.class)
public class HandleEntitySpawn {
	@Shadow private ClientWorld world;

	@Inject(method = "onEntitySpawn", at = @At("TAIL"), locals = LocalCapture.CAPTURE_FAILHARD)
	private void spawnOurEntity(EntitySpawnS2CPacket packet, CallbackInfo ci, double x, double y, double z, Entity ignored, EntityType<?> entityType) {
		if (entityType == AutomotionEntities.STEAM) {
			int i = packet.getId();
			Entity entity = new SteamCloudEntity(AutomotionEntities.STEAM, world);
			entity.updateTrackedPosition(x, y, z);
			entity.refreshPositionAfterTeleport(x, y, z);
			entity.pitch = (float)(packet.getPitch() * 360) / 256.0F;
			entity.yaw = (float)(packet.getYaw() * 360) / 256.0F;
			entity.setEntityId(i);
			entity.setUuid(packet.getUuid());
			this.world.addEntity(i, entity);
		}
	}
}
