package hephaestus.dev.automotion.common.util.quantity;

import net.minecraft.util.Pair;
import org.jetbrains.annotations.Nullable;

import java.util.function.Predicate;

public interface QuantityHandler {
	static QuantityHandler getPassThrough() {
		return quantity -> quantity;
	}

	@Nullable Quantity<?> handle(@Nullable Quantity<?> quantity);

	default @Nullable Quantity<?> handle(Object type, int cost) {
		return this.handle(Quantity.of(type, cost));
	}

	static <T> Pair<@Nullable Quantity<?>, Long> handle(@Nullable Quantity<T> quantity, Predicate<T> predicate, long current, long max) {
		if (quantity != null && predicate.test(quantity.type)) {
			long result = current + quantity.quantity;

			if (result > max) {
				return new Pair<>(quantity.with((int) (result - max)), max);
			} else if (result >= 0) {
				return new Pair<>(null, result);
			} else {
				return new Pair<>(quantity.with((int) result), 0L);
			}
		} else {
			return new Pair<>(quantity, current);
		}
	}
}
