package hephaestus.dev.automotion.mixin.entity;

import hephaestus.dev.automotion.common.item.Conveyable;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ItemEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ItemEntity.class)
public abstract class ItemEntityMixin extends Entity implements Conveyable {
	@Shadow public abstract ItemStack getStack();

	@Shadow public abstract boolean isFireImmune();

	public ItemEntityMixin(EntityType<?> type, World world) {
		super(type, world);
	}

	@Unique
	private int lastConveyed;

	@Inject(method = "<init>(Lnet/minecraft/entity/EntityType;Lnet/minecraft/world/World;)V", at = @At("TAIL"))
	private void setStepHeight(EntityType<? extends ItemEntity> entityType, World world, CallbackInfo ci) {
		this.stepHeight = 0.7F;
	}

	@Inject(method = "tick", at = @At("TAIL"))
	private void adjustAge(CallbackInfo ci) {
		boolean bl1 = prevX != getX();
		boolean bl2 = prevY != getY();
		boolean bl3 = prevZ != getZ();
		if (isBeingConveyed() && (bl1 || bl2 || bl3))
			--this.age;
	}

	@Inject(method = "canMerge()Z", at = @At("HEAD"), cancellable = true)
	private void dontMergeOnConveyor(CallbackInfoReturnable<Boolean> cir) {
		if (isBeingConveyed()) {
			if (prevX != getX() || prevY != getY() || prevZ != getZ()) {
				cir.setReturnValue(false);
			}
		}
	}

	@Override
	public boolean isBeingConveyed() {
		return this.age - this.lastConveyed >= 15;
	}

	@Override
	public void convey() {
		this.lastConveyed = this.age;
	}
}
