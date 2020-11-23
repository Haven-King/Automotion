package hephaestus.dev.automotion.mixin.entity;

import hephaestus.dev.automotion.common.block.transportation.DuctBlock;
import hephaestus.dev.automotion.common.item.Conveyable;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ItemEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import static net.minecraft.state.property.Properties.WATERLOGGED;

@Mixin(ItemEntity.class)
public abstract class ItemEntityMixin extends Entity implements Conveyable {
	@Shadow public abstract ItemStack getStack();

	@Shadow public abstract boolean isFireImmune();

	public ItemEntityMixin(EntityType<?> type, World world) {
		super(type, world);
	}

	@Inject(method = "<init>(Lnet/minecraft/entity/EntityType;Lnet/minecraft/world/World;)V", at = @At("TAIL"))
	private void setStepHeight(EntityType<? extends ItemEntity> entityType, World world, CallbackInfo ci) {
		this.stepHeight = 0.7F;
	}

	@Inject(method = "tick", at = @At("TAIL"))
	private void adjustAge(CallbackInfo ci) {
		boolean bl1 = prevX != getX();
		boolean bl2 = prevY != getY();
		boolean bl3 = prevZ != getZ();

		if (isBeingConveyed() && (bl1 || bl2 || bl3)) {
			--this.age;
		}

		BlockPos pos = this.getBlockPos();
		BlockState state = this.world.getBlockState(pos);
		if (state.getBlock() instanceof DuctBlock && state.get(WATERLOGGED) && state.get(DuctBlock.DRAG) != DuctBlock.Drag.NONE) {
			Vec3d center = new Vec3d(pos.getX() + 0.5, this.getY(), pos.getZ() + 0.5);
			double distance = this.getPos().distanceTo(center);
			if (distance > 0.25D) {
				Vec3d dif = center.subtract(this.getPos()).normalize().multiply(0.01);
				this.addVelocity(dif.x, dif.y, dif.z);
			}
		}
	}

	@Inject(method = "canMerge()Z", at = @At("HEAD"), cancellable = true)
	private void dontMergeOnConveyor(CallbackInfoReturnable<Boolean> cir) {
		if (isBeingConveyed()) {
			if (prevX != getX() || prevY != getY() || prevZ != getZ()) {
				cir.setReturnValue(false);
			}
		}
	}
}
