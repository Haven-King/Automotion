package hephaestus.dev.automotion.mixin.block.entity;

import hephaestus.dev.automotion.block.ConveyorBelt;
import hephaestus.dev.automotion.block.entity.GoldenHopperBlockEntity;
import net.minecraft.block.HopperBlock;
import net.minecraft.block.entity.*;
import net.minecraft.entity.ItemEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(HopperBlockEntity.class)
public abstract class HopperBlockEntityMixin extends LootableContainerBlockEntity {
	@Shadow public abstract double getHopperX();

	@Shadow public abstract double getHopperY();

	@Shadow public abstract double getHopperZ();

	@Shadow protected abstract void setCooldown(int cooldown);

	protected HopperBlockEntityMixin(BlockEntityType<?> blockEntityType) {
		super(blockEntityType);
	}

	@Inject(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/block/entity/HopperBlockEntity;insertAndExtract(Ljava/util/function/Supplier;)Z"), cancellable = true)
	private void dropItemOnConveyorBelt(CallbackInfo ci) {
		Direction direction = this.getCachedState().get(HopperBlock.FACING);
		if (world != null && world.getBlockState(this.getPos().offset(direction)).getBlock() instanceof ConveyorBelt && this.getCachedState().get(HopperBlock.ENABLED)) {
			for(int i = 0; i < this.size(); ++i) {
				ItemStack itemStack = this.getStack(i);
				//noinspection ConstantConditions
				if ((Object)this instanceof GoldenHopperBlockEntity ? this.getStack(i).getCount() > 1 : !this.getStack(i).isEmpty()) {
					Vec3d spawnPos = new Vec3d(getHopperX(), getHopperY(), getHopperZ());

					double offset = 0.75;
					switch (direction) {
						case DOWN:
							spawnPos = spawnPos.add(0, -offset, 0);
							break;
						case NORTH:
							spawnPos = spawnPos.add(0, -0.2F, -offset);
							break;
						case EAST:
							spawnPos = spawnPos.add(offset, -0.2F, 0);
							break;
						case SOUTH:
							spawnPos = spawnPos.add(0, -0.2F, offset);
							break;
						case WEST:
							spawnPos = spawnPos.add(-offset, -0.2F, 0);
							break;
					}

					ItemEntity item = new ItemEntity(world, spawnPos.getX(), spawnPos.getY(), spawnPos.getZ(), new ItemStack(itemStack.getItem()));
					item.setVelocity(0, 0, 0);
					world.spawnEntity(item);
					itemStack.decrement(1);
					setCooldown(8);
					ci.cancel();
					return;
				}
			}
		}
	}
}
