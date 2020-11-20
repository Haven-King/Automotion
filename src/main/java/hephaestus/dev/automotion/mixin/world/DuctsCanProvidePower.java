package hephaestus.dev.automotion.mixin.world;

import hephaestus.dev.automotion.common.block.transportation.DuctBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.Material;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(World.class)
public abstract class DuctsCanProvidePower {
	@Shadow public abstract int getReceivedStrongRedstonePower(BlockPos pos);

	@Inject(method = "getEmittedRedstonePower", at = @At("RETURN"), cancellable = true, locals = LocalCapture.CAPTURE_FAILHARD)
	private void ductsCanCarryPower(BlockPos pos, Direction direction, CallbackInfoReturnable<Integer> cir, BlockState state, int power) {
		if (state.getBlock() instanceof DuctBlock && state.getMaterial() == Material.METAL) {
			cir.setReturnValue(Math.max(power, this.getReceivedStrongRedstonePower(pos)));
		}
	}
}
