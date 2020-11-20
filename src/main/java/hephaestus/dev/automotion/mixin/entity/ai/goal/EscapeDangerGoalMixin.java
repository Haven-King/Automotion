package hephaestus.dev.automotion.mixin.entity.ai.goal;

import hephaestus.dev.automotion.common.block.transportation.conveyors.ConveyorBelt;
import net.minecraft.block.BlockState;
import net.minecraft.entity.ai.goal.EscapeDangerGoal;
import net.minecraft.entity.mob.PathAwareEntity;
import net.minecraft.state.property.Properties;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(EscapeDangerGoal.class)
public class EscapeDangerGoalMixin {
	@Redirect(method = "canStart", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/mob/PathAwareEntity;isOnFire()Z", ordinal = 0))
	private boolean dontEscapeDangerOnConveyorBelts(PathAwareEntity pathAwareEntity) {
		BlockState state = pathAwareEntity.world.getBlockState(pathAwareEntity.getBlockPos());
		if ((state.getBlock() instanceof ConveyorBelt && state.get(Properties.ENABLED))) {
			return false;
		} else {
			return pathAwareEntity.isOnFire();
		}
	}
}
