package hephaestus.dev.automotion.mixin.block;

import hephaestus.dev.automotion.common.block.EntityProvider;
import net.minecraft.block.EntityShapeContext;
import net.minecraft.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(EntityShapeContext.class)
public class EntityShapeContextMixin implements EntityProvider {
	@Unique private Entity entity = null;

	@Inject(method = "<init>(Lnet/minecraft/entity/Entity;)V", at = @At("TAIL"))
	private void captureEntity(Entity entity, CallbackInfo ci) {
		this.entity = entity;
	}

	@Override
	public Entity getEntity() {
		return this.entity;
	}
}
