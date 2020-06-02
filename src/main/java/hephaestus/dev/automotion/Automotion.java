package hephaestus.dev.automotion;

import io.netty.buffer.Unpooled;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.fabricmc.fabric.api.client.keybinding.FabricKeyBinding;
import net.fabricmc.fabric.api.client.keybinding.KeyBindingRegistry;
import net.fabricmc.fabric.api.event.client.ClientTickCallback;
import net.fabricmc.fabric.api.network.ClientSidePacketRegistry;
import net.fabricmc.fabric.api.network.ServerSidePacketRegistry;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.util.InputUtil;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;
import org.lwjgl.glfw.GLFW;

import java.util.HashMap;
import java.util.UUID;

public class Automotion implements ModInitializer, ClientModInitializer {
	public static final String MOD_NAME = "Automotion";
	public static final String MOD_ID = "automotion";

	public static final Identifier ALTERNATE_PLACEMENT_ID = newID("alternate_placement");

	@Environment(EnvType.CLIENT)
	public static FabricKeyBinding alternatePlacementKeybind;

	private static final HashMap<UUID, Boolean> ALTERNATE_PLACEMENTS = new HashMap<>();

	public static boolean isAlternate(PlayerEntity player) {
		return ALTERNATE_PLACEMENTS.getOrDefault(player.getUuid(), false);
	}

	@Override
	public void onInitialize() {
		AutomotionBlocks.init();

		ServerSidePacketRegistry.INSTANCE.register(ALTERNATE_PLACEMENT_ID, ((packetContext, packetByteBuf) -> {
			PlayerEntity playerEntity = packetContext.getPlayer();
			boolean alternatePlacement = packetByteBuf.readBoolean();
			packetContext.getTaskQueue().execute(() -> {
				ALTERNATE_PLACEMENTS.put(playerEntity.getUuid(), alternatePlacement);
			});
		}));
	}

	public static Identifier newID(String id) {
		return new Identifier(MOD_ID, id);
	}

	@Override
	@Environment(EnvType.CLIENT)
	public void onInitializeClient() {
		BlockRenderLayerMap.INSTANCE.putBlock(AutomotionBlocks.GLASS_WALL, RenderLayer.getCutout());
		BlockRenderLayerMap.INSTANCE.putBlock(AutomotionBlocks.HEATED_CONVEYOR_BELT, RenderLayer.getCutout());

		KeyBindingRegistry.INSTANCE.addCategory(MOD_NAME);

		alternatePlacementKeybind = FabricKeyBinding.Builder.create(
						ALTERNATE_PLACEMENT_ID,
						InputUtil.Type.KEYSYM,
						GLFW.GLFW_KEY_LEFT_ALT,
						MOD_NAME
		).build();

		KeyBindingRegistry.INSTANCE.register(alternatePlacementKeybind);

		ClientTickCallback.EVENT.register(event -> {
			MinecraftClient client = MinecraftClient.getInstance();
			if (client.getServer() != null ||
					(client.getNetworkHandler() != null &&
									client.getNetworkHandler().getConnection() != null)) {
				PacketByteBuf buf = new PacketByteBuf(Unpooled.buffer());
				buf.writeBoolean(alternatePlacementKeybind.isPressed());
				ClientSidePacketRegistry.INSTANCE.sendToServer(ALTERNATE_PLACEMENT_ID, buf);
			}
		});
	}
}
