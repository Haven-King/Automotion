package hephaestus.dev.automotion.mixin.entity;

import hephaestus.dev.automotion.block.ConveyorBelt;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ItemEntity;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ItemEntity.class)
public abstract class ItemEntityMixin extends Entity {
	public ItemEntityMixin(EntityType<?> type, World world) {
		super(type, world);
	}

	@Inject(method = "canMerge()Z", at = @At("HEAD"), cancellable = true)
	private void dontMergeOnConveyor(CallbackInfoReturnable<Boolean> cir) {
		if (world.getBlockState(this.getBlockPos()).getBlock() instanceof ConveyorBelt) {
			if (prevX != getX() || prevY != getY() || prevZ != getZ()) {
				cir.setReturnValue(false);
			}
		}
	}
}
