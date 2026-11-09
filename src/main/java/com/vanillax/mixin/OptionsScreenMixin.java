package com.vanillax.mixin;

import com.vanillax.VanillaXConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.options.OptionsScreen;
import net.minecraft.client.gui.screens.options.VideoSettingsScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Forces the Video Settings button to open the vanilla screen when another mod (Sodium, for example)
 * replaced it. Applies at the main menu and in-game.
 *
 * <p>Priority 900 so this runs before Sodium's own {@code OptionsScreen} hook, which sits at the
 * default 1000.
 */
@Mixin(value = OptionsScreen.class, priority = 900)
public abstract class OptionsScreenMixin {
	@Inject(method = "lambda$init$2", at = @At("HEAD"), cancellable = true)
	private void vanillax$openVanillaVideoSettings(CallbackInfoReturnable<Screen> info) {
		if (!VanillaXConfig.get().vanillaVideoSettings) {
			return;
		}

		Minecraft minecraft = Minecraft.getInstance();
		info.setReturnValue(new VideoSettingsScreen((Screen) (Object) this, minecraft, minecraft.options));
	}
}
