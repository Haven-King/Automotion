package hephaestus.dev.automotion.common.block.entity;

import hephaestus.dev.automotion.common.AutomotionBlocks;
import hephaestus.dev.automotion.common.block.transportation.DiamondHopperBlock;
import hephaestus.dev.automotion.common.block.transportation.conveyors.ConveyorBelt;
import hephaestus.dev.automotion.common.screen.DiamondHopperScreenHandler;
import net.minecraft.block.BlockState;
import net.minecraft.block.HopperBlock;
import net.minecraft.block.entity.Hopper;
import net.minecraft.block.entity.LootableContainerBlockEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventories;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Tickable;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.function.BooleanBiFunction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.shape.VoxelShapes;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.function.Supplier;
import java.util.stream.IntStream;

import static net.minecraft.block.entity.HopperBlockEntity.*;

public class DiamondHopperBlockEntity extends LootableContainerBlockEntity implements Hopper, Tickable, SidedInventory {
	private static final int[] AVAILABLE = new int[25];

	static {
		for (int i = 0; i < 25; ++i) {
			AVAILABLE[i] = i;
		}
	}

	private DefaultedList<ItemStack> inventory;
	private int dropCooldown;
	private int transferCooldown;
	private long lastTickTime;

	public DiamondHopperBlockEntity() {
		super(AutomotionBlocks.DIAMOND_HOPPER_TYPE);
		this.inventory = DefaultedList.ofSize(25, ItemStack.EMPTY);
		this.transferCooldown = -1;
	}

	@Override
	public double getHopperX() {
		return this.pos.getX() + 0.5;
	}

	@Override
	public double getHopperY() {
		return this.pos.getY() + 0.5;
	}

	@Override
	public double getHopperZ() {
		return this.pos.getZ() + 0.5;
	}

	@Override
	protected DefaultedList<ItemStack> getInvStackList() {
		return this.inventory;
	}

	@Override
	protected void setInvStackList(DefaultedList<ItemStack> list) {
		this.inventory = list;
	}

	@Override
	protected Text getContainerName() {
		return new TranslatableText("container.hopper");
	}

	@Override
	protected ScreenHandler createScreenHandler(int syncId, PlayerInventory playerInventory) {
		return new DiamondHopperScreenHandler(syncId, playerInventory, this);
	}

	@Override
	public int size() {
		return 25;
	}

	@Override
	public CompoundTag toTag(CompoundTag tag) {
		if (!this.serializeLootTable(tag)) {
			Inventories.toTag(tag, this.inventory);
		}

		tag.putInt("TransferCooldown", this.transferCooldown);
		return super.toTag(tag);
	}

	@Override
	public void fromTag(BlockState state, CompoundTag tag) {
		super.fromTag(state, tag);
		this.inventory = DefaultedList.ofSize(this.size(), ItemStack.EMPTY);
		if (!this.deserializeLootTable(tag)) {
			Inventories.fromTag(tag, this.inventory);
		}

		this.transferCooldown = tag.getInt("TransferCooldown");
	}

	@Override
	public void setStack(int slot, ItemStack stack) {
		super.setStack(slot, stack);

		if (this.world instanceof ServerWorld) {
			this.world.setBlockState(this.pos, ((DiamondHopperBlock) AutomotionBlocks.DIAMOND_HOPPER).getState(this.world, this.pos, this.getCachedState()));
		}
	}

	public List<ItemStack> getInventory() {
		return this.inventory;
	}

	@Override
	public void tick() {
		if (this.world != null && !this.world.isClient) {
			--this.transferCooldown;
			this.lastTickTime = world.getTime();

			if (this.dropCooldown <= 0) {
				for (int y = 0; y < DiamondHopperBlock.DIRECTIONS.length; ++y) {
					Direction direction = DiamondHopperBlock.DIRECTIONS[y];
					if (world.getBlockState(this.getPos().offset(direction)).getBlock() instanceof ConveyorBelt && this.getCachedState().get(DiamondHopperBlock.DIRECTIONS_PROPERTIES[y])) {
						for (int x = 0; x < 5; ++x) {
							ItemStack stack = this.getStack(x + y * 5);
							if (stack.getCount() > 1) {
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

								ItemEntity item = new ItemEntity(world, spawnPos.getX(), spawnPos.getY(), spawnPos.getZ(), new ItemStack(stack.getItem()));
								item.setVelocity(0, 0, 0);
								world.spawnEntity(item);
								stack.decrement(1);
								this.dropCooldown = 8;
								break;
							}
						}
					}
				}
			}

			if (!this.needsCooldown()) {
				this.setCooldown(0);
				this.insertAndExtract(() -> extract(this));
			}

			this.dropCooldown--;
		}
	}

	private void insertAndExtract(Supplier<Boolean> extractMethod) {
		if (this.world != null && !this.world.isClient) {
			if (!this.needsCooldown()) {
				boolean bl = false;
				if (!this.isEmpty()) {
					bl = this.insert();
				}

				if (!this.isFull()) {
					bl |= extractMethod.get();
				}

				if (bl) {
					this.setCooldown(8);
					this.markDirty();
				}
			}
		}
	}

	private boolean isFull() {
		for (ItemStack stack : this.inventory) {
			if (stack.isEmpty()) {
				return false;
			}
		}
		return true;
	}

	@Override
	public int[] getAvailableSlots(Direction side) {
		return AVAILABLE;
	}

	@Override
	public boolean canInsert(int slot, ItemStack stack, Direction dir) {
		ItemStack present = this.getStack(slot);
		return present.getCount() < present.getMaxCount() && present.getItem() == stack.getItem();
	}

	@Override
	public boolean canExtract(int slot, ItemStack stack, Direction dir) {
		return this.getStack(slot).getCount() > 1;
	}


	private boolean insert() {
		boolean result = false;

		for (Inventory inventory : this.getOutputInventories()) {
			if (inventory != null) {
				Direction direction = this.getCachedState().get(HopperBlock.FACING).getOpposite();
				if (!this.isInventoryFull(inventory, direction)) {
					for (int i = 0; i < this.size(); ++i) {
						if (!this.getStack(i).isEmpty()) {
							ItemStack itemStack = this.getStack(i).copy();
							ItemStack itemStack2 = transfer(this, inventory, this.removeStack(i, 1), direction);
							if (itemStack2.isEmpty()) {
								inventory.markDirty();
								result = true;
							}

							this.setStack(i, itemStack);
						}
					}

				}
			}
		}

		return result;
	}

	private List<Inventory> getOutputInventories() {
		List<Inventory> inventories = new ArrayList<>(5);
		for (int i = 0; i < DiamondHopperBlock.DIRECTIONS.length; ++i) {
			if (this.getCachedState().get(DiamondHopperBlock.DIRECTIONS_PROPERTIES[i]) && this.getWorld() != null) {
				Direction direction = DiamondHopperBlock.DIRECTIONS[i];
				inventories.add(getInventoryAt(this.getWorld(), this.pos.offset(direction)));
			}
		}

		return inventories;
	}

	private boolean isInventoryFull(Inventory inv, Direction direction) {
		return getAvailableSlots(inv, direction).allMatch((i) -> {
			ItemStack itemStack = inv.getStack(i);
			return itemStack.getCount() >= itemStack.getMaxCount();
		});
	}

	public void onEntityCollided(Entity entity) {
		if (entity instanceof ItemEntity) {
			BlockPos blockPos = this.getPos();
			if (VoxelShapes.matchesAnywhere(VoxelShapes.cuboid(entity.getBoundingBox().offset(-blockPos.getX(), -blockPos.getY(), -blockPos.getZ())), this.getInputAreaShape(), BooleanBiFunction.AND)) {
				this.insertAndExtract(() -> extract(this, (ItemEntity)entity));
			}
		}

	}

	private static IntStream getAvailableSlots(Inventory inventory, Direction side) {
		return inventory instanceof SidedInventory ? IntStream.of(((SidedInventory)inventory).getAvailableSlots(side)) : IntStream.range(0, inventory.size());
	}

	private boolean needsCooldown() {
		return this.transferCooldown > 0;
	}

	private void setCooldown(int cooldown) {
		this.transferCooldown = cooldown;
	}

	private static boolean isInventoryEmpty(Inventory inv, Direction facing) {
		return getAvailableSlots(inv, facing).allMatch((i) -> {
			return inv.getStack(i).isEmpty();
		});
	}

	public static boolean extract(DiamondHopperBlockEntity hopper) {
		Inventory inventory = getInputInventory(hopper);
		if (inventory != null) {
			Direction direction = Direction.DOWN;
			return !isInventoryEmpty(inventory, direction) && getAvailableSlots(inventory, direction).anyMatch((i) -> extract(hopper, inventory, i, direction));
		} else {
			Iterator<ItemEntity> var2 = getInputItemEntities(hopper).iterator();

			ItemEntity itemEntity;
			do {
				if (!var2.hasNext()) {
					return false;
				}

				itemEntity = var2.next();
			} while(!extract(hopper, itemEntity));

			return true;
		}
	}

	private static boolean canInsert(Inventory inventory, ItemStack stack, int slot, @Nullable Direction side) {
		if (!inventory.isValid(slot, stack)) {
			return false;
		} else {
			return !(inventory instanceof SidedInventory) || ((SidedInventory)inventory).canInsert(slot, stack, side);
		}
	}

	private static boolean canExtract(Inventory inv, ItemStack stack, int slot, Direction facing) {
		return !(inv instanceof SidedInventory) || ((SidedInventory)inv).canExtract(slot, stack, facing);
	}

	private static boolean extract(Hopper hopper, Inventory inventory, int slot, Direction side) {
		ItemStack itemStack = inventory.getStack(slot);
		if (!itemStack.isEmpty() && canExtract(inventory, itemStack, slot, side)) {
			ItemStack itemStack2 = itemStack.copy();
			ItemStack itemStack3 = transfer(inventory, hopper, inventory.removeStack(slot, 1), (Direction)null);
			if (itemStack3.isEmpty()) {
				inventory.markDirty();
				return true;
			}

			inventory.setStack(slot, itemStack2);
		}

		return false;
	}

	private static boolean canMergeItems(ItemStack first, ItemStack second) {
		if (first.getItem() != second.getItem()) {
			return false;
		} else if (first.getDamage() != second.getDamage()) {
			return false;
		} else if (first.getCount() > first.getMaxCount()) {
			return false;
		} else {
			return ItemStack.areTagsEqual(first, second);
		}
	}

	public static ItemStack transfer(@Nullable Inventory from, Inventory to, ItemStack stack, @Nullable Direction side) {
		if (to instanceof SidedInventory && side != null) {
			SidedInventory sidedInventory = (SidedInventory)to;
			int[] is = sidedInventory.getAvailableSlots(side);

			for(int i = 0; i < is.length && !stack.isEmpty(); ++i) {
				stack = transfer(from, to, stack, is[i], side);
			}
		} else {
			int j = to.size();

			for(int k = 0; k < j && !stack.isEmpty(); ++k) {
				stack = transfer(from, to, stack, k, side);
			}
		}

		return stack;
	}

	private static ItemStack transfer(@Nullable Inventory from, Inventory to, ItemStack stack, int slot, @Nullable Direction direction) {
		ItemStack itemStack = to.getStack(slot);
		if (canInsert(to, stack, slot, direction)) {
			boolean bl = false;
			boolean bl2 = to.isEmpty();
			if (itemStack.isEmpty()) {
				to.setStack(slot, stack);
				stack = ItemStack.EMPTY;
				bl = true;
			} else if (canMergeItems(itemStack, stack)) {
				int i = stack.getMaxCount() - itemStack.getCount();
				int j = Math.min(stack.getCount(), i);
				stack.decrement(j);
				itemStack.increment(j);
				bl = j > 0;
			}

//			if (bl) {
//				if (bl2 && to instanceof HopperBlockEntity) {
//					HopperBlockEntity hopperBlockEntity = (HopperBlockEntity)to;
//					if (!hopperBlockEntity.isDisabled()) {
//						int k = 0;
//						if (from instanceof HopperBlockEntity) {
//							HopperBlockEntity hopperBlockEntity2 = (HopperBlockEntity)from;
//							if (hopperBlockEntity.lastTickTime >= hopperBlockEntity2.lastTickTime) {
//								k = 1;
//							}
//						}
//
//						hopperBlockEntity.setCooldown(8 - k);
//					}
//				}
//
//				to.markDirty();
//			}
		}

		return stack;
	}

	public static boolean extract(DiamondHopperBlockEntity inventory, ItemEntity itemEntity) {
		boolean bl = false;
		ItemStack itemStack = itemEntity.getStack().copy();
		ItemStack itemStack2 = transfer(null, inventory, itemStack, null);

		for (int i = 0; i < 25; ++i) {
			if (inventory.canInsert(i, itemStack2, null)) {
				itemStack2 = transfer(null, inventory, itemStack2, i, null);
			}
		}

		if (itemStack2.isEmpty()) {
			bl = true;
			itemEntity.remove();
		} else {
			itemEntity.setStack(itemStack2);
		}

		return bl;
	}
}
