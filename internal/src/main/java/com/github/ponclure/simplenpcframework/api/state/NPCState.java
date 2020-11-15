package com.github.ponclure.simplenpcframework.api.state;

import java.util.Collection;

public enum NPCState {
	ON_FIRE((byte) 1),
	CROUCHED((byte) 2),
	INVISIBLE((byte) 32);

	private final byte b;

	NPCState(final byte b) {
		this.b = b;
	}

	public byte getByte() {
		return b;
	}

	public static byte getMasked(final NPCState... states) {
		byte mask = 0;
		for (final NPCState state : states) {
			mask |= state.getByte();
		}
		return mask;
	}

	public static byte getMasked(final Collection<NPCState> states) {
		byte mask = 0;
		for (final NPCState state : states) {
			mask |= state.getByte();
		}
		return mask;
	}
}
