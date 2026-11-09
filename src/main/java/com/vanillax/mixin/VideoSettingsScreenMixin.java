package com.vanillax.mixin;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.vanillax.VanillaXConfig;
import net.minecraft.client.OptionInstance;
import net.minecraft.client.Options;
import net.minecraft.client.gui.components.OptionsList;
import net.minecraft.client.gui.screens.options.VideoSettingsScreen;
import net.minecraft.network.chat.Component;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

/**
 * Simple Mode: trims the vanilla Video Settings screen down to the handful of options that matter for
 * performance tuning. Hidden options keep their stored values - only the widgets disappear.
 */
@Mixin(VideoSettingsScreen.class)
public abstract class VideoSettingsScreenMixin {
	@Shadow
	@Final
	private static Component PREFERENCES_HEADER;

	@ModifyReturnValue(method = "displayOptions", at = @At("RETURN"))
	private static OptionInstance<?>[] vanillax$simpleDisplay(OptionInstance<?>[] original, Options options) {
		if (!VanillaXConfig.simpleModeActive()) {
			return original;
		}

		return new OptionInstance<?>[] {
			options.gamma(),
			options.framerateLimit(),
			options.guiScale()
		};
	}

	@ModifyReturnValue(method = "qualityOptions", at = @At("RETURN"))
	private static OptionInstance<?>[] vanillax$simpleQuality(OptionInstance<?>[] original, Options options) {
		if (!VanillaXConfig.simpleModeActive()) {
			return original;
		}

		return new OptionInstance<?>[] {
			options.renderDistance(),
			options.simulationDistance(),
			options.entityDistanceScaling(),
			options.particles()
		};
	}

	@ModifyReturnValue(method = "preferenceOptions", at = @At("RETURN"))
	private static OptionInstance<?>[] vanillax$simplePreferences(OptionInstance<?>[] original, Options options) {
		return VanillaXConfig.simpleModeActive() ? new OptionInstance<?>[0] : original;
	}

	@WrapOperation(
		method = "addOptions",
		at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/client/gui/components/OptionsList;addHeader(Lnet/minecraft/network/chat/Component;)V"
		)
	)
	private void vanillax$skipPreferencesHeader(OptionsList list, Component header, Operation<Void> original) {
		if (VanillaXConfig.simpleModeActive() && header == PREFERENCES_HEADER) {
			return;
		}

		original.call(list, header);
	}

	@WrapOperation(
		method = "addOptions",
		at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/client/gui/components/OptionsList;addBig(Lnet/minecraft/client/OptionInstance;)V"
		)
	)
	private void vanillax$skipBigOptions(OptionsList list, OptionInstance<?> option, Operation<Void> original) {
		// The only two full-width options on this screen are Exclusive Fullscreen Mode and Preset.
		if (VanillaXConfig.simpleModeActive()) {
			return;
		}

		original.call(list, option);
	}
}
