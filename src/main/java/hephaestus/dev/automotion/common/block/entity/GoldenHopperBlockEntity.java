package hephaestus.dev.automotion.common.block.entity;

import hephaestus.dev.automotion.common.AutomotionBlocks;
import net.minecraft.block.entity.HopperBlockEntity;
import net.minecraft.inventory.SidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.Direction;

public class GoldenHopperBlockEntity extends HopperBlockEntity implements SidedInventory {
	int[] available = new int[this.getInvStackList().size()];

	public GoldenHopperBlockEntity() {
		super();
		this.type = AutomotionBlocks.GOLDEN_HOPPER_TYPE;
		for (int i = 0; i < this.getInvStackList().size(); ++i) {
			available[i] = i;
		}
	}

	@Override
	public int[] getAvailableSlots(Direction side) {
		return available.clone();
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
}
