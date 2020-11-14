package hephaestus.dev.automotion.mixin.block;

import hephaestus.dev.automotion.common.AutomotionEntities;
import hephaestus.dev.automotion.common.block.HeatTickable;
import hephaestus.dev.automotion.common.entity.SteamCloudEntity;
import net.minecraft.block.Blocks;
import net.minecraft.block.FluidBlock;
import net.minecraft.entity.Entity;
import net.minecraft.fluid.FlowableFluid;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.tag.FluidTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(FluidBlock.class)
public class FluidBlockMixin implements HeatTickable {
	@Shadow @Final protected FlowableFluid fluid;

	@Override
	public boolean heatTick(World world, BlockPos pos, int newTemperature) {
		if (!this.fluid.isIn(FluidTags.WATER)) {
			return false;
		}

		if (newTemperature >= 120) {
			world.setBlockState(pos, Blocks.AIR.getDefaultState());
			Entity steam = new SteamCloudEntity(AutomotionEntities.STEAM, world).init(200);
			steam.setPos(pos.getX() + 0.5, pos.getY() + 1, pos.getZ() + 0.5);
			world.spawnEntity(steam);
			world.playSound(null, pos, SoundEvents.BLOCK_LAVA_EXTINGUISH, SoundCategory.BLOCKS, 1F, 1F);
		}

		return true;
	}
//
//	@Inject(method = "randomTick", at = @At("HEAD"))
//	private void playBoilingWaterSound(BlockState state, ServerWorld world, BlockPos pos, Random random, CallbackInfo ci) {
//		if (this.fluid.isIn(FluidTags.WATER) && temperatures.get(pos) >= 30 && random.nextInt(100) == 0) {
//			double d = (double)pos.getX() + random.nextDouble();
//			double e = (double)pos.getY() + 1.0D;
//			double f = (double)pos.getZ() + random.nextDouble();
//			world.addParticle(ParticleTypes.LAVA, d, e, f, 0.0D, 0.0D, 0.0D);
//			world.playSound(d, e, f, SoundEvents.BLOCK_LAVA_POP, SoundCategory.BLOCKS, 0.2F + random.nextFloat() * 0.2F, 0.9F + random.nextFloat() * 0.15F, false);
//		}
//	}
//
//	@Inject(method = "onEntityCollision", at = @At("HEAD"))
//	private void doBoilingWaterDamage(BlockState state, World world, BlockPos pos, Entity entity, CallbackInfo ci) {
//		if (temperatures.getOrDefault(pos, 0) >= 60) {
//			entity.damage(DamageSource.HOT_FLOOR, 1);
//		}
//	}
}
