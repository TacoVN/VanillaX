package com.vanillax;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.function.ObjIntConsumer;
import java.util.function.ToIntFunction;
import net.fabricmc.loader.api.FabricLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class VanillaXConfig {
	private static final Logger LOGGER = LoggerFactory.getLogger("VanillaX");
	private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
	private static VanillaXConfig instance;

	public int shieldHeight = 50;
	public int fireHeight = 20;
	public int totemScale = 80;
	public int totemPopSize = 20;
	public int totemPopHeight = 50;
	public int pumpkinOpacity = 70;
	public int spyglassOpacity = 70;
	public boolean permanentSprint = true;
	public boolean eatingParticles = false;
	public boolean explosionParticles = false;
	public boolean cleanHitboxes = true;
	public boolean vanillaVideoSettings = true;
	public boolean simpleMode = false;
	public boolean crosshairFix = true;
	public boolean witherFix = true;
	public boolean riptideFix = true;
	public boolean easterEgg = false;

	public static VanillaXConfig get() {
		if (instance == null) {
			load();
		}
		return instance;
	}

	/**
	 * Simple Mode only trims the video settings screen that Vanilla Video Settings opens, so it stays
	 * inert while that toggle is off.
	 */
	public static boolean simpleModeActive() {
		VanillaXConfig config = get();
		return config.simpleMode && config.vanillaVideoSettings;
	}

	public static void load() {
		Path path = path();
		VanillaXConfig loaded = null;

		if (Files.exists(path)) {
			try (Reader reader = Files.newBufferedReader(path)) {
				loaded = GSON.fromJson(reader, VanillaXConfig.class);
			} catch (IOException | RuntimeException exception) {
				LOGGER.warn("Could not read {}", path, exception);
			}
		}

		instance = loaded != null ? loaded : new VanillaXConfig();
		instance.clamp();
	}

	public void save() {
		clamp();
		Path path = path();

		try {
			Files.createDirectories(path.getParent());
			try (Writer writer = Files.newBufferedWriter(path)) {
				GSON.toJson(this, writer);
			}
		} catch (IOException exception) {
			LOGGER.warn("Could not save {}", path, exception);
		}
	}

	public float shieldHeightOffset() {
		return -0.5F * (1.0F - shieldHeight / 100.0F);
	}

	public float fireHeightOffset() {
		return -0.4F * (1.0F - fireHeight / 100.0F);
	}

	public float totemScaleFactor() {
		return totemScale / 100.0F;
	}

	public float totemPopScale() {
		return totemPopSize / 100.0F;
	}

	public float totemPopHeightOffset() {
		return -3.0F * (1.0F - totemPopHeight / 100.0F);
	}

	public float pumpkinAlpha() {
		return opacity(pumpkinOpacity);
	}

	public float spyglassAlpha() {
		return opacity(spyglassOpacity);
	}

	private static float opacity(int value) {
		return value / 100.0F;
	}

	private void clamp() {
		shieldHeight = clamp(shieldHeight);
		fireHeight = clamp(fireHeight);
		totemScale = clamp(totemScale);
		totemPopSize = clamp(totemPopSize);
		totemPopHeight = clamp(totemPopHeight);
		pumpkinOpacity = clamp(pumpkinOpacity);
		spyglassOpacity = clamp(spyglassOpacity);
	}

	private static int clamp(int value) {
		return Math.max(0, Math.min(100, value));
	}

	private static Path path() {
		return FabricLoader.getInstance().getConfigDir().resolve("vanillax.json");
	}

	public enum Setting {
		SHIELD_HEIGHT("Shield Height", config -> config.shieldHeight, (config, value) -> config.shieldHeight = value),
		FIRE_HEIGHT("Fire Height", config -> config.fireHeight, (config, value) -> config.fireHeight = value),
		TOTEM_SCALE("Totem Scale", config -> config.totemScale, (config, value) -> config.totemScale = value),
		TOTEM_POP_SCALE("Totem Pop Scale", config -> config.totemPopSize, (config, value) -> config.totemPopSize = value),
		TOTEM_POP_HEIGHT("Totem Pop Height", config -> config.totemPopHeight, (config, value) -> config.totemPopHeight = value),
		PUMPKIN_OPACITY("Pumpkin Opacity", config -> config.pumpkinOpacity, (config, value) -> config.pumpkinOpacity = value),
		SPYGLASS_OPACITY("Spyglass Opacity", config -> config.spyglassOpacity, (config, value) -> config.spyglassOpacity = value);

		private final String label;
		private final ToIntFunction<VanillaXConfig> getter;
		private final ObjIntConsumer<VanillaXConfig> setter;

		Setting(String label, ToIntFunction<VanillaXConfig> getter, ObjIntConsumer<VanillaXConfig> setter) {
			this.label = label;
			this.getter = getter;
			this.setter = setter;
		}

		public String label() {
			return label;
		}

		public int get(VanillaXConfig config) {
			return getter.applyAsInt(config);
		}

		public void set(VanillaXConfig config, int value) {
			setter.accept(config, value);
		}
	}
}
