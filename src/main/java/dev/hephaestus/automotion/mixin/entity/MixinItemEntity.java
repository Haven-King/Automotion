package dev.hephaestus.automotion.mixin.entity;

import dev.hephaestus.automotion.common.util.EntityExtensions;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ItemEntity;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ItemEntity.class)
public abstract class MixinItemEntity extends Entity implements EntityExtensions {
    public MixinItemEntity(EntityType<?> type, World world) {
        super(type, world);
    }

    @Inject(method = "tick", at = @At("TAIL"))
    private void dontAgeOnConveyors(CallbackInfo ci) {
        boolean bl1 = prevX != getX();
        boolean bl2 = prevY != getY();
        boolean bl3 = prevZ != getZ();

        if (isBeingConveyed() && (bl1 || bl2 || bl3)) {
            --this.age;
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
