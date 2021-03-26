package dev.hephaestus.automotion.common.networking;

import dev.hephaestus.automotion.Automotion;
import dev.hephaestus.automotion.client.AutomotionClient;
import dev.hephaestus.automotion.common.util.PlayerExtensions;
import io.netty.buffer.Unpooled;
import net.fabricmc.api.EnvType;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;

public class AlternatePlaceHandler implements ServerPlayNetworking.PlayChannelHandler {
    private static final Identifier ID = Automotion.id("alternate_placement");

    @Override
    public void receive(MinecraftServer server, ServerPlayerEntity player, ServerPlayNetworkHandler handler, PacketByteBuf buf, PacketSender responseSender) {
        boolean bl = buf.readBoolean();
        server.execute(() -> ((PlayerExtensions) player).setAltPlacement(bl));
    }

    public static void init() {
        AlternatePlaceHandler alternatePlaceHandler = new AlternatePlaceHandler();

        ServerPlayConnectionEvents.INIT.register((handler, server) -> {
            ServerPlayNetworking.registerReceiver(handler, ID, alternatePlaceHandler);
        });

        if (FabricLoader.getInstance().getEnvironmentType() == EnvType.CLIENT) {
            ClientTickEvents.START_WORLD_TICK.register(world -> {
                if ((AutomotionClient.ALT.isPressed() && AutomotionClient.ALT.wasPressed())
                        || (AutomotionClient.ALT.wasPressed() && !AutomotionClient.ALT.isPressed())) {
                    PacketByteBuf buf = new PacketByteBuf(Unpooled.buffer());
                    buf.writeBoolean(AutomotionClient.ALT.isPressed());

                    ClientPlayNetworking.send(ID, buf);
                }
            });
        }
    }
}
