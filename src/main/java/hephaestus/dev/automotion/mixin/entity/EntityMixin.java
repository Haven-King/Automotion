package hephaestus.dev.automotion.mixin.entity;

import hephaestus.dev.automotion.common.Automotion;
import hephaestus.dev.automotion.common.block.transportation.conveyors.ConveyorBelt;
import hephaestus.dev.automotion.common.item.Conveyable;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.ShapeContext;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.crash.CrashException;
import net.minecraft.util.crash.CrashReport;
import net.minecraft.util.crash.CrashReportSection;
import net.minecraft.util.function.BooleanBiFunction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Entity.class)
public abstract class EntityMixin implements Conveyable {
	@Shadow private BlockPos blockPos;
	@Shadow public World world;

	@Shadow public int age;

	@Shadow public abstract void setVelocity(Vec3d velocity);

	@Shadow public abstract Box getBoundingBox();

	@Shadow protected abstract void onBlockCollision(BlockState state);

	@Shadow public abstract Vec3d getVelocity();

	@Shadow public abstract void addVelocity(double deltaX, double deltaY, double deltaZ);

	@Unique private int lastConveyed;
	@Unique private int conveyedBy;
	@Unique private Vec3d conveyance = Vec3d.ZERO;

	// So that sprinting on conveyor belts actually spawns the conveyor belt particles instead of the block underneath
	// of the conveyor belt.
	@Redirect(method = "spawnSprintingParticles", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/World;getBlockState(Lnet/minecraft/util/math/BlockPos;)Lnet/minecraft/block/BlockState;"))
	private BlockState getCurrentBlockStateIfNotAir(World world, BlockPos pos) {
		BlockState currentState = world.getBlockState(this.blockPos);
		return currentState.isAir() ? world.getBlockState(pos) : currentState;
	}

	/**
	 * @author Haven King
	 */
	@Overwrite
	// TODO: Make this not an overwrite
	public void checkBlockCollision() {
		Box box = this.getBoundingBox();
		BlockPos blockPos = new BlockPos(box.minX - Automotion.FUZZ, box.minY - Automotion.FUZZ, box.minZ - Automotion.FUZZ);
		BlockPos blockPos2 = new BlockPos(box.maxX + Automotion.FUZZ, box.maxY + Automotion.FUZZ, box.maxZ + Automotion.FUZZ);
		BlockPos.Mutable mutable = new BlockPos.Mutable();

		if (this.world.isRegionLoaded(blockPos, blockPos2)) {
			for (int i = blockPos.getX(); i <= blockPos2.getX(); ++i) {
				for (int j = blockPos.getY(); j <= blockPos2.getY(); ++j) {
					for (int k = blockPos.getZ(); k <= blockPos2.getZ(); ++k) {
						mutable.set(i, j, k);
						BlockState blockState = this.world.getBlockState(mutable);

						try {
							if (this.collidesWith(mutable, blockState)) {
								blockState.onEntityCollision(this.world, mutable, (Entity) (Object) this);
								this.onBlockCollision(blockState);
							}
						} catch (Throwable var12) {
							CrashReport crashReport = CrashReport.create(var12, "Colliding entity with block");
							CrashReportSection crashReportSection = crashReport.addElement("Block being collided with");
							CrashReportSection.addBlockInfo(crashReportSection, mutable, blockState);
							throw new CrashException(crashReport);
						}
					}
				}
			}
		}
	}

	public void doConveyance() {
		this.conveyance = this.conveyance.multiply(1D / this.conveyedBy);

		if (this.conveyance.length() > 0.1) {
			this.setVelocity(this.conveyance);
		}

		this.conveyedBy = 0;
		this.conveyance = Vec3d.ZERO;
	}

	@Inject(method = "dropStack(Lnet/minecraft/item/ItemStack;F)Lnet/minecraft/entity/ItemEntity;", at = @At("RETURN"), cancellable = true)
	private void adjustStackVelocity(ItemStack stack, float yOffset, CallbackInfoReturnable<@Nullable ItemEntity> cir) {
		if (cir.getReturnValue() != null && this.isBeingConveyed()) {
			cir.getReturnValue().setVelocity(this.getVelocity());
		}
	}

	@Inject(method = "playSound", at = @At("HEAD"), cancellable = true)
	private void dontPlayStepSound(SoundEvent sound, float volume, float pitch, CallbackInfo ci) {
		String callingMethod = Thread.currentThread().getStackTrace()[3].getMethodName();
		if (this.isBeingConveyed() && (callingMethod.equals("playStepSound") || callingMethod.equals("method_5712"))) {
			ci.cancel();
		}
	}

	@Redirect(method = "playStepSound", at = @At(value = "INVOKE", target = "Lnet/minecraft/block/BlockState;isOf(Lnet/minecraft/block/Block;)Z"))
	private boolean isSnowOrConveyorBelt(BlockState blockState, Block block) {
		return blockState.isOf(block) || blockState.getBlock() instanceof ConveyorBelt;
	}

//	@ModifyConstant(method = "checkBlockCollision", constant = @Constant(doubleValue = Automotion.FUZZ))
//	private double changeFuzz(double oldFuzz) {
//		return -Automotion.FUZZ;
//	}

	@Override
	public boolean isBeingConveyed() {
		return this.age - this.lastConveyed <= 15;
	}

	@Override
	@SuppressWarnings("ConstantConditions")
	public void convey(Vec3d direction) {
		this.lastConveyed = this.age;
		++this.conveyedBy;
		this.conveyance = this.conveyance.add(direction);

		if ((Object) this instanceof LivingEntity) {
			((LivingEntity) (Object) this).limbDistance = 0F;
		}

		if ((Object) this instanceof ItemEntity) {
			((ItemEntity) (Object) this).setPickupDelay(15);
		}
	}

	@Unique
	public boolean collidesWith(BlockPos blockPos, BlockState blockState) {
		VoxelShape voxelShape = blockState.getCollisionShape(this.world, blockPos, ShapeContext.of((Entity) (Object) this));
		VoxelShape voxelShape2 = voxelShape.offset(blockPos.getX(), blockPos.getY(), blockPos.getZ());
		return VoxelShapes.matchesAnywhere(voxelShape2, VoxelShapes.cuboid(this.getBoundingBox().expand(0.1F)), BooleanBiFunction.AND);
	}
}
