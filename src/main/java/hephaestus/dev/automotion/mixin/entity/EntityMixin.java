package hephaestus.dev.automotion.mixin.entity;

import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(Entity.class)
public class EntityMixin {
	@Shadow private BlockPos blockPos;

	@Shadow public World world;

	// So that sprinting on conveyor belts actually spawns the conveyor belt particles instead of the block underneath
	// of the conveyor belt.
	@Redirect(method = "spawnSprintingParticles", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/World;getBlockState(Lnet/minecraft/util/math/BlockPos;)Lnet/minecraft/block/BlockState;"))
	private BlockState getCurrentBlockStateIfNotAir(World world, BlockPos pos) {
		BlockState currentState = world.getBlockState(this.blockPos);
		return currentState.isAir() ? world.getBlockState(pos) : currentState;
	}
}
