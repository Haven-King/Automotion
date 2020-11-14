package hephaestus.dev.automotion.mixin.block.entity;

import hephaestus.dev.automotion.common.Automotion;
import hephaestus.dev.automotion.common.block.HeatTickable;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.AbstractFurnaceBlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.block.entity.LockableContainerBlockEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.Recipe;
import net.minecraft.util.math.BlockPos;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import static net.minecraft.block.entity.AbstractFurnaceBlockEntity.canUseAsFuel;

@Mixin(AbstractFurnaceBlockEntity.class)
public abstract class FurnaceBlockEntityMixin extends LockableContainerBlockEntity {
    @Shadow protected abstract boolean canAcceptRecipeOutput(Recipe<?> recipe);

    @Shadow protected abstract boolean isBurning();

    protected FurnaceBlockEntityMixin(BlockEntityType<?> blockEntityType) {
        super(blockEntityType);
    }

    @Redirect(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/block/entity/AbstractFurnaceBlockEntity;canAcceptRecipeOutput(Lnet/minecraft/recipe/Recipe;)Z"))
    private boolean burnIfPoweredAbove(AbstractFurnaceBlockEntity abstractFurnaceBlockEntity, Recipe<?> recipe) {
        if (this.world != null && this.world.getBlockState(this.pos.up()).getBlock() instanceof HeatTickable) {
            return true;
        } else {
            return canAcceptRecipeOutput(recipe);
        }
    }

    @Redirect(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/item/ItemStack;isEmpty()Z", ordinal = 1))
    private boolean burnIfPoweredAbove(ItemStack itemStack) {
        if (this.world != null && this.world.getBlockState(this.pos.up()).getBlock() instanceof HeatTickable) {
            return false;
        } else {
            return itemStack.isEmpty();
        }
    }

    @Inject(method = "tick", at = @At(value = "TAIL"))
    private void tickHeatTickable(CallbackInfo ci) {
        BlockPos pos = this.pos.up();
        if (world != null && !world.isClient && this.world.getBlockState(pos).getBlock() instanceof HeatTickable && this.isBurning()) {
            Automotion.WATER_TEMPERATURE.of(this.world.getChunk(pos)).heat(pos, 2);
            //            ((HeatTickable) state.getBlock()).heatTick(world, this.pos.up(), state);
        }
    }

    @Inject(method = "isValid", at = @At("HEAD"), cancellable = true)
    private void dontTakeFuelIntoTopSlot(int slot, ItemStack stack, CallbackInfoReturnable<Boolean> cir) {
        if (slot == 0 && canUseAsFuel(stack)) {
            cir.setReturnValue(false);
        }
    }
}
