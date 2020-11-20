package hephaestus.dev.automotion.mixin.client.world;

import hephaestus.dev.automotion.common.item.Conveyable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientWorld.class)
@Environment(EnvType.CLIENT)
public class ClientWorldMixin {
	@Inject(method = "tickEntity", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/Entity;tick()V", shift = At.Shift.AFTER))
	private void doConveyance(Entity entity, CallbackInfo ci) {
		((Conveyable) entity).doConveyance();
	}
}
