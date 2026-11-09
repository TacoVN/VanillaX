package com.vanillax.mixin;

import com.vanillax.gui.VanillaXSettingsScreen;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.screens.CreditsAndAttributionScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(CreditsAndAttributionScreen.class)
public abstract class CreditsAndAttributionScreenMixin extends Screen {
	@Unique
	private Button vanillax$settingsButton;

	protected CreditsAndAttributionScreenMixin(Component title) {
		super(title);
	}

	@Inject(method = "init", at = @At("TAIL"))
	private void vanillax$addSettingsButton(CallbackInfo info) {
		this.vanillax$settingsButton = Button.builder(Component.literal("VanillaX"), pressed -> {
			this.minecraft.gui.setScreen(new VanillaXSettingsScreen(this, this.minecraft.options));
		}).width(210).build();
		this.addRenderableWidget(this.vanillax$settingsButton);
		this.vanillax$positionSettingsButton();
	}

	@Inject(method = "repositionElements", at = @At("TAIL"))
	private void vanillax$repositionSettingsButton(CallbackInfo info) {
		this.vanillax$positionSettingsButton();
	}

	@Unique
	private void vanillax$positionSettingsButton() {
		if (this.vanillax$settingsButton == null) {
			return;
		}

		Component licenses = Component.translatable("credits_and_attribution.button.licenses");
		Button anchor = null;

		for (GuiEventListener child : this.children()) {
			if (child != this.vanillax$settingsButton
				&& child instanceof Button button
				&& button.getMessage().getString().equals(licenses.getString())) {
				anchor = button;
				break;
			}
		}

		if (anchor != null) {
			this.vanillax$settingsButton.setX(anchor.getX());
			this.vanillax$settingsButton.setY(anchor.getY() + anchor.getHeight() + 8);
			this.vanillax$settingsButton.setWidth(anchor.getWidth());
		} else {
			this.vanillax$settingsButton.setX((this.width - 210) / 2);
			this.vanillax$settingsButton.setY(this.height / 2);
			this.vanillax$settingsButton.setWidth(210);
		}
	}
}
