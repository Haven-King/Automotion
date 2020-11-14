package hephaestus.dev.automotion.common;

import hephaestus.dev.automotion.common.data.BlockTemperature;
import hephaestus.dev.automotion.common.util.ChunkDataDefinition;
import hephaestus.dev.automotion.common.util.ChunkDataRegistry;
import io.netty.buffer.Unpooled;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.FabricKeyBinding;
import net.fabricmc.fabric.api.client.keybinding.KeyBindingRegistry;
import net.fabricmc.fabric.api.network.ClientSidePacketRegistry;
import net.fabricmc.fabric.api.network.ServerSidePacketRegistry;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.util.InputUtil;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.glfw.GLFW;

import java.util.HashMap;
import java.util.UUID;

public class Automotion implements ModInitializer, ClientModInitializer {
	public static final String MOD_NAME = "Automotion";
	public static final String MOD_ID = "automotion";

	public static final Logger LOG = LogManager.getLogger(MOD_NAME);

	public static final Identifier ALTERNATE_PLACEMENT_ID = newID("alternate_placement");

	public static final float FUZZ = 0.13F;

	public static final ChunkDataDefinition<BlockTemperature> WATER_TEMPERATURE = ChunkDataRegistry.register(newID("data", "water_temperature"), BlockTemperature::new);

	@Environment(EnvType.CLIENT)
	public static FabricKeyBinding alternatePlacementKeybind;

	private static final HashMap<UUID, Boolean> ALTERNATE_PLACEMENTS = new HashMap<>();

	public static boolean isAlternate(PlayerEntity player) {
		return ALTERNATE_PLACEMENTS.getOrDefault(player.getUuid(), false);
	}

	@Override
	public void onInitialize() {
		AutomotionBlocks.init();
		AutomotionItems.init();
		AutomotionEntities.init();
		AutomotionNetworking.init();

		ServerSidePacketRegistry.INSTANCE.register(ALTERNATE_PLACEMENT_ID, ((packetContext, packetByteBuf) -> {
			PlayerEntity playerEntity = packetContext.getPlayer();
			boolean alternatePlacement = packetByteBuf.readBoolean();
			packetContext.getTaskQueue().execute(() -> {
				ALTERNATE_PLACEMENTS.put(playerEntity.getUuid(), alternatePlacement);
			});
		}));
	}

	public static Identifier newID(String... path) {
		return new Identifier(MOD_ID, String.join(".", path));
	}

	@Override
	@Environment(EnvType.CLIENT)
	public void onInitializeClient() {
		AutomotionBlocks.initClient();
		AutomotionEntities.initClient();
		AutomotionNetworking.initClient();

		KeyBindingRegistry.INSTANCE.addCategory(MOD_NAME);

		alternatePlacementKeybind = FabricKeyBinding.Builder.create(
						ALTERNATE_PLACEMENT_ID,
						InputUtil.Type.KEYSYM,
						GLFW.GLFW_KEY_LEFT_ALT,
						MOD_NAME
		).build();

		KeyBindingRegistry.INSTANCE.register(alternatePlacementKeybind);

		ClientTickEvents.START_WORLD_TICK.register(event -> {
			MinecraftClient client = MinecraftClient.getInstance();
			if (client.getServer() != null ||
					(client.getNetworkHandler() != null &&
									client.getNetworkHandler().getConnection().isOpen())) {
				PacketByteBuf buf = new PacketByteBuf(Unpooled.buffer());
				buf.writeBoolean(alternatePlacementKeybind.isPressed());
				ClientSidePacketRegistry.INSTANCE.sendToServer(ALTERNATE_PLACEMENT_ID, buf);
			}
		});
	}
}
