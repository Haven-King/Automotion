package hephaestus.dev.automotion.common.util.quantity;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class Quantity<TYPE> {
	public final @NotNull TYPE type;
	public final int quantity;

	private Quantity(@NotNull TYPE type, int quantity) {
		this.type = type;
		this.quantity = quantity;
	}

	public static <T> @Nullable Quantity<T> of(@NotNull T type, int cost) {
		if (cost == 0) {
			return null;
		} else {
			return new Quantity<>(type, cost);
		}
	}

	public Quantity<TYPE> with(int cost) {
		return Quantity.of(type, cost);
	}

}
