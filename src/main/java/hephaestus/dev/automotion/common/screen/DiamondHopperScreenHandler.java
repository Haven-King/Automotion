package hephaestus.dev.automotion.common.screen;

import hephaestus.dev.automotion.common.Automotion;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.Slot;

public class DiamondHopperScreenHandler extends ScreenHandler {
	private final Inventory inventory;

	public DiamondHopperScreenHandler(int syncId, PlayerInventory playerInventory) {
		this(syncId, playerInventory, new SimpleInventory(25));
	}

	public DiamondHopperScreenHandler(int syncId, PlayerInventory playerInventory, Inventory inventory) {
		super(Automotion.DIAMOND_HOPPER, syncId);
		this.inventory = inventory;
		checkSize(inventory, 25);
		inventory.onOpen(playerInventory.player);

		for (int y = 0; y < 5; ++y) {
			for (int x = 0; x < 5; ++x) {
				this.addSlot(new Slot(inventory, y * 5 + x, 44 + x * 18, 20 + 26 * y));
			}
		}

		for (int y = 0; y < 3; ++y) {
			for (int x = 0; x < 9; ++x) {
				this.addSlot(new Slot(playerInventory, x + y * 9 + 9, 8 + x * 18, y * 18 + 155));
			}
		}

		for (int x = 0; x < 9; ++x) {
			this.addSlot(new Slot(playerInventory, x, 8 + x * 18, 213));
		}
	}

	@Override
	public boolean canUse(PlayerEntity player) {
		return this.inventory.canPlayerUse(player);
	}

	public ItemStack transferSlot(PlayerEntity player, int index) {
		ItemStack itemStack = ItemStack.EMPTY;
		Slot slot = this.slots.get(index);

		if (slot != null && slot.hasStack()) {
			ItemStack itemStack2 = slot.getStack();
			itemStack = itemStack2.copy();
			if (index < this.inventory.size()) {
				if (!this.insertItem(itemStack2, this.inventory.size(), this.slots.size(), true)) {
					return ItemStack.EMPTY;
				}
			} else if (!this.insertItem(itemStack2, 0, this.inventory.size(), false)) {
				return ItemStack.EMPTY;
			}

			if (itemStack2.isEmpty()) {
				slot.setStack(ItemStack.EMPTY);
			} else {
				slot.markDirty();
			}
		}

		return itemStack;
	}

	public void close(PlayerEntity player) {
		super.close(player);
		this.inventory.onClose(player);
	}
}
