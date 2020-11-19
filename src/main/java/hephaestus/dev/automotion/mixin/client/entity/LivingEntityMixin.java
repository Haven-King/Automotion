package hephaestus.dev.automotion.mixin.client.entity;

import hephaestus.dev.automotion.common.item.Conveyable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LivingEntity.class)
@Environment(EnvType.CLIENT)
public abstract class LivingEntityMixin extends Entity implements Conveyable {
	public LivingEntityMixin(EntityType<?> type, World world) {
		super(type, world);
	}

	@Inject(method = "tick", at = @At("HEAD"))
	private void updateStuff(CallbackInfo ci) {
		if (this.world.isClient) {
			this.checkBlockCollision();
		}
	}
}
