package hephaestus.dev.automotion.common.util;

import java.util.Objects;

public class BitField {
	private final boolean[] values;

	public BitField(int fields) {
		this.values = new boolean[fields];
	}

	public void set(int i, boolean value) {
		this.values[i] = value;
	}

	public boolean get(int i) {
		return this.values[i];
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		BitField bitField = (BitField) o;

		if (values.length != bitField.values.length) return false;

		for (int i = 0; i < values.length; ++i) {
			if (values[i] != bitField.values[i]) return false;
		}

		return true;
	}

	@Override
	public int hashCode() {
		int hashCode = 1;

		for (boolean value : values) {
			hashCode = 31 * hashCode + Objects.hashCode(value);
		}

		return hashCode;
	}
}
