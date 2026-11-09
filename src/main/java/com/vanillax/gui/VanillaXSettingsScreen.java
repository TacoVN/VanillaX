package com.vanillax.gui;

import com.vanillax.VanillaXConfig;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import net.minecraft.ChatFormatting;
import net.minecraft.client.OptionInstance;
import net.minecraft.client.Options;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.CycleButton;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.options.OptionsSubScreen;
import net.minecraft.network.chat.Component;

public class VanillaXSettingsScreen extends OptionsSubScreen {
	private static final Component TITLE = Component.literal("VanillaX Settings");
	private CycleButton<Boolean> simpleModeButton;

	public VanillaXSettingsScreen(Screen lastScreen, Options options) {
		super(lastScreen, options, TITLE);
	}

	@Override
	protected void addOptions() {
		VanillaXConfig config = VanillaXConfig.get();
		this.addGeneralSection(config);
		this.addSection(
			"Shield & Fire",
			config,
			VanillaXConfig.Setting.SHIELD_HEIGHT,
			VanillaXConfig.Setting.FIRE_HEIGHT
		);
		this.addSection(
			"Totem",
			config,
			VanillaXConfig.Setting.TOTEM_SCALE,
			VanillaXConfig.Setting.TOTEM_POP_SCALE,
			VanillaXConfig.Setting.TOTEM_POP_HEIGHT
		);
		this.addSection("Overlay", config, VanillaXConfig.Setting.PUMPKIN_OPACITY, VanillaXConfig.Setting.SPYGLASS_OPACITY);
		this.addOthersSection(config);
	}

	@Override
	protected void addContents() {
		this.list = this.layout.addToContents(new VanillaXOptionsList(this.minecraft, this.width, this));
		this.addOptions();
	}

	private void addGeneralSection(VanillaXConfig config) {
		this.addToggleSection(
			"General",
			createToggle("Permanent Sprint", config.permanentSprint, value -> config.permanentSprint = value, config),
			createToggle("Fog", config.fog, value -> config.fog = value, config),
			createToggle("Eating Particles", config.eatingParticles, value -> config.eatingParticles = value, config),
			createToggle("Explosion Particles", config.explosionParticles, value -> config.explosionParticles = value, config),
			createToggle("Clean Hitboxes", config.cleanHitboxes, value -> config.cleanHitboxes = value, config)
		);
	}

	private void addOthersSection(VanillaXConfig config) {
		CycleButton<Boolean> vanillaVideoSettings = createToggle(
			"Vanilla Video Settings",
			config.vanillaVideoSettings,
			value -> {
				config.vanillaVideoSettings = value;

				if (this.simpleModeButton != null) {
					this.simpleModeButton.active = value;
				}
			},
			config
		);

		// Simple Mode is only reachable while the vanilla video screen is in use; the stored value is
		// kept, the button is just locked like vanilla does for unavailable options.
		this.simpleModeButton = createToggle("Simple Mode", config.simpleMode, value -> config.simpleMode = value, config);
		this.simpleModeButton.active = config.vanillaVideoSettings;

		this.addToggleSection(
			"Others",
			vanillaVideoSettings,
			this.simpleModeButton,
			createToggle("Crosshair Fix", config.crosshairFix, value -> config.crosshairFix = value, config),
			createToggle("Riptide Fix", config.riptideFix, value -> config.riptideFix = value, config),
			createToggle("Wither Fix", config.witherFix, value -> config.witherFix = value, config),
			createToggle("Easter Egg", config.easterEgg, value -> config.easterEgg = value, config)
		);
	}

	private void addToggleSection(String title, AbstractWidget... widgets) {
		this.addHeader(title);
		this.list.addSmall(List.of(widgets));
	}

	private CycleButton<Boolean> createToggle(String label, boolean initialValue, Consumer<Boolean> setter, VanillaXConfig config) {
		return CycleButton.onOffBuilder(initialValue).create(Component.literal(label), (button, value) -> {
			setter.accept(value);
			config.save();
		});
	}

	private void addHeader(String title) {
		this.list.addHeader(Component.literal(title).withStyle(ChatFormatting.UNDERLINE, ChatFormatting.BOLD));
	}

	private void addSection(String title, VanillaXConfig config, VanillaXConfig.Setting... settings) {
		this.addHeader(title);
		List<AbstractWidget> widgets = new ArrayList<>(settings.length);

		for (VanillaXConfig.Setting setting : settings) {
			widgets.add(this.createSlider(setting, config));
		}

		this.list.addSmall(widgets);
	}

	private AbstractWidget createSlider(VanillaXConfig.Setting setting, VanillaXConfig config) {
		OptionInstance<Integer> option = new OptionInstance<>(
			setting.label(),
			OptionInstance.noTooltip(),
			(caption, value) -> Component.literal(caption.getString() + ": " + value),
			new OptionInstance.IntRange(0, 100),
			setting.get(config),
			value -> {
				setting.set(config, value);
				config.save();
			}
		);
		return option.createButton(this.options);
	}

	@Override
	public void removed() {
		super.removed();
		VanillaXConfig.get().save();
	}
}
