package hephaestus.dev.automotion.mixin.world;

import hephaestus.dev.automotion.common.item.Conveyable;
import net.minecraft.entity.Entity;
import net.minecraft.server.world.ServerWorld;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerWorld.class)
public class WorldMixin {
	@Inject(method = "tickEntity", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/Entity;tick()V", shift = At.Shift.AFTER))
	private void doConveyance(Entity entity, CallbackInfo ci) {
		((Conveyable) entity).doConveyance();
	}
}
