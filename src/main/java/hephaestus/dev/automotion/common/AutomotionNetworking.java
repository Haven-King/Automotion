package hephaestus.dev.automotion.common;

import hephaestus.dev.automotion.common.entity.SteamCloudEntity;
import net.fabricmc.fabric.api.network.ClientSidePacketRegistry;
import net.minecraft.util.Identifier;

public class AutomotionNetworking {
	public static final Identifier SPAWN_STEAM = packet("entity", "spawn", "steam");

	public static void init() {

	}

	public static void initClient() {
		ClientSidePacketRegistry.INSTANCE.register(SPAWN_STEAM, SteamCloudEntity::spawn);
	}

	private static Identifier packet(String... path) {
		return Automotion.newID(path);
	}
}
