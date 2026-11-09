package com.vanillax.render;

public final class VanillaXColors {
	private VanillaXColors() {
	}

	public static int applyColorAlpha(int color, float alpha) {
		if (alpha >= 1.0F) {
			return color;
		}

		int a = Math.round(((color >>> 24) & 0xFF) * alpha);
		return (a << 24) | (color & 0x00FFFFFF);
	}
}
