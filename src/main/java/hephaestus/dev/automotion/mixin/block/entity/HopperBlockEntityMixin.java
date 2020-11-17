package hephaestus.dev.automotion.mixin.block.entity;

import hephaestus.dev.automotion.common.block.Connectable;
import hephaestus.dev.automotion.common.block.entity.GoldenHopperBlockEntity;
import net.minecraft.block.HopperBlock;
import net.minecraft.block.entity.*;
import net.minecraft.entity.ItemEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

@Mixin(HopperBlockEntity.class)
public abstract class HopperBlockEntityMixin extends LootableContainerBlockEntity {
	@Shadow public abstract double getHopperX();

	@Shadow public abstract double getHopperY();

	@Shadow public abstract double getHopperZ();

	@Shadow public static List<ItemEntity> getInputItemEntities(Hopper hopper) {return null;}

	@Shadow
	public static Inventory getInputInventory(Hopper hopper) {return null;}

	protected HopperBlockEntityMixin(BlockEntityType<?> blockEntityType) {
		super(blockEntityType);
	}

//	@Redirect(method = "extract(Lnet/minecraft/block/entity/Hopper;)Z", at = @At(value = "INVOKE", target = "Lnet/minecraft/block/entity/HopperBlockEntity;getInputInventory(Lnet/minecraft/block/entity/Hopper;)Lnet/minecraft/inventory/Inventory;"))
//	private static Inventory dontAttemptToTakeFromInsertionConveyor(Hopper hopper) {
//		BlockPos pos = new BlockPos(hopper.getHopperX(), hopper.getHopperY() + 1.0D, hopper.getHopperZ());
//		if (hopper.getWorld() != null && hopper.getWorld().getBlockState(pos).getBlock() instanceof InsertionConveyor)
//			return null;
//		else
//			return getInputInventory(hopper);
//	}

	private int dropCooldown;
	@Inject(method = "tick", at = @At(value = "INVOKE_ASSIGN", target = "Lnet/minecraft/world/World;getTime()J"))
	private void dropItemOnConnectablesBelt(CallbackInfo ci) {
		Direction direction = this.getCachedState().get(HopperBlock.FACING);
		if (this.dropCooldown <= 0 && world != null && world.getBlockState(this.getPos().offset(direction)).getBlock() instanceof Connectable && this.getCachedState().get(HopperBlock.ENABLED)) {
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
					this.dropCooldown = 8;
					return;
				}
			}
		}
		dropCooldown--;
	}

	@Redirect(method = "getAvailableSlots", at = @At(value = "INVOKE", target = "Lnet/minecraft/inventory/SidedInventory;getAvailableSlots(Lnet/minecraft/util/math/Direction;)[I"))
	private static int[] getAllSlots0(SidedInventory sidedInventory, Direction side) {
		if (side != Direction.DOWN) {
			HashSet<Integer> slots = new HashSet<>();
			for (Direction direction : Direction.values()) {
				for (int slot : sidedInventory.getAvailableSlots(direction)) {
					slots.add(slot);
				}
			}

			int[] allSlots = new int[slots.size()];
			int i = 0;
			for (int s : slots) {
				allSlots[i++] = s;
			}

			return allSlots;
		} else {
			return sidedInventory.getAvailableSlots(side);
		}
	}

	@Redirect(method = "transfer(Lnet/minecraft/inventory/Inventory;Lnet/minecraft/inventory/Inventory;Lnet/minecraft/item/ItemStack;Lnet/minecraft/util/math/Direction;)Lnet/minecraft/item/ItemStack;", at = @At(value = "INVOKE", target = "Lnet/minecraft/inventory/SidedInventory;getAvailableSlots(Lnet/minecraft/util/math/Direction;)[I"))
	private static int[] getAllSlots1(SidedInventory sidedInventory, Direction side) {
		return getAllSlots0(sidedInventory, side);
	}

	@Unique private static boolean GOLDEN = false;

	@Redirect(method = "insert", at = @At(value = "INVOKE", target = "Lnet/minecraft/item/ItemStack;isEmpty()Z", ordinal = 0))
	private boolean dontInsertAllItems(ItemStack itemStack) {
		//noinspection ConstantConditions
		if ((Object) this instanceof GoldenHopperBlockEntity) {
			GOLDEN = true;
			return itemStack.getCount() <= 1;
		} else {
			GOLDEN = false;
			return itemStack.isEmpty();
		}
	}

	@Redirect(method = "extract(Lnet/minecraft/block/entity/Hopper;)Z", at = @At(value = "INVOKE", target = "Lnet/minecraft/block/entity/HopperBlockEntity;getInputItemEntities(Lnet/minecraft/block/entity/Hopper;)Ljava/util/List;"))
	private static List<ItemEntity> dontPickupEntitiesIfInserterIsNotFull(Hopper hopper) {
		return getInputItemEntities(hopper);
	}
//
//	@Redirect(method = "onEntityCollided", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/shape/VoxelShapes;matchesAnywhere(Lnet/minecraft/util/shape/VoxelShape;Lnet/minecraft/util/shape/VoxelShape;Lnet/minecraft/util/function/BooleanBiFunction;)Z"))
//	private boolean dontPickupEntitiesIfInserterIsNotFull(VoxelShape shape1, VoxelShape shape2, BooleanBiFunction predicate) {
//		BlockPos pos = new BlockPos(this.getHopperX(), this.getHopperY()+1, this.getHopperZ());
//		if (world != null && world.getBlockState(pos).getBlock() instanceof InsertionConveyor) {
//			BlockEntity blockEntity = world.getBlockEntity(pos);
//			if (blockEntity instanceof InsertionConveyorBlockEntity && ((InsertionConveyorBlockEntity) blockEntity).isFull())
//				return false;
//		}
//
//		return VoxelShapes.matchesAnywhere(shape1, shape2, predicate);
//	}
}
