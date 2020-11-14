package hephaestus.dev.automotion.common;

import hephaestus.dev.automotion.client.entity.SteamEntityRenderer;
import hephaestus.dev.automotion.common.entity.SteamCloudEntity;
import net.fabricmc.fabric.api.client.rendereregistry.v1.EntityRendererRegistry;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricEntityTypeBuilder;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.util.registry.Registry;

public class AutomotionEntities {
	public static EntityType<SteamCloudEntity> STEAM = FabricEntityTypeBuilder.create(SpawnGroup.MISC, SteamCloudEntity::new).dimensions(new EntityDimensions(0.45F, 0.45F, true)).trackable(128, 10).build();

	public static void init() {
		Registry.register(Registry.ENTITY_TYPE, Automotion.newID("steam"), STEAM);
	}

	public static void initClient() {
		EntityRendererRegistry.INSTANCE.register(STEAM, (entityRenderDispatcher, context) -> {
			return new SteamEntityRenderer(entityRenderDispatcher);
		});
	}
}
