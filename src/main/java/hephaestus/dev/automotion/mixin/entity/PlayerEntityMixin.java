package hephaestus.dev.automotion.mixin.entity;

import hephaestus.dev.automotion.common.item.Conveyable;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerEntity.class)
public abstract class PlayerEntityMixin implements Conveyable {
	@Inject(method = "playSound(Lnet/minecraft/sound/SoundEvent;FF)V", at = @At("HEAD"), cancellable = true)
	private void dontPlayStepSound(SoundEvent sound, float volume, float pitch, CallbackInfo ci) {
		String callingMethod = Thread.currentThread().getStackTrace()[3].getMethodName();
		if (this.isBeingConveyed() && (callingMethod.equals("playStepSound") || callingMethod.equals("method_5712"))) {
			ci.cancel();
		}
	}

	@Inject(method = "playSound(Lnet/minecraft/sound/SoundEvent;Lnet/minecraft/sound/SoundCategory;FF)V", at = @At("HEAD"), cancellable = true)
	private void dontPlayStepSound(SoundEvent event, SoundCategory category, float volume, float pitch, CallbackInfo ci) {
		String callingMethod = Thread.currentThread().getStackTrace()[3].getMethodName();
		if (this.isBeingConveyed() && (callingMethod.equals("playStepSound") || callingMethod.equals("method_5712"))) {
			ci.cancel();
		}
	}
}
