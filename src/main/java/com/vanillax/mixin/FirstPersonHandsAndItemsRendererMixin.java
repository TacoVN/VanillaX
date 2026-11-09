package com.vanillax.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import com.mojang.blaze3d.vertex.PoseStack;
import com.vanillax.VanillaXConfig;
import net.minecraft.client.renderer.FirstPersonHandsAndItemsRenderer;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.state.AvatarRenderState;
import net.minecraft.client.renderer.item.ItemStackRenderState;
import net.minecraft.client.renderer.state.level.FirstPersonHandsAndItemsRenderState;
import net.minecraft.client.renderer.state.level.PlayerRenderState;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(FirstPersonHandsAndItemsRenderer.class)
public class FirstPersonHandsAndItemsRendererMixin {
	@Unique
	private boolean vanillax$wasSpinning;

	/**
	 * Vanilla throws the held item far in front of the camera during a Riptide spin attack, which hides
	 * the whole view behind the item model. Clearing the flag for the first person pass keeps every item
	 * in its normal position; the flag is restored right after so third person keeps the vanilla spin.
	 */
	@Inject(method = "submitHandsWithItems", at = @At("HEAD"))
	private void vanillax$hideSpinAttack(
		float partialTick,
		PoseStack poseStack,
		SubmitNodeCollector collector,
		PlayerRenderState playerState,
		FirstPersonHandsAndItemsRenderState handsState,
		CallbackInfo info
	) {
		AvatarRenderState avatar = playerState.avatarRenderState;

		if (avatar == null || !VanillaXConfig.get().riptideFix) {
			return;
		}

		this.vanillax$wasSpinning = avatar.isAutoSpinAttack;
		avatar.isAutoSpinAttack = false;
	}

	@Inject(method = "submitHandsWithItems", at = @At("RETURN"))
	private void vanillax$restoreSpinAttack(
		float partialTick,
		PoseStack poseStack,
		SubmitNodeCollector collector,
		PlayerRenderState playerState,
		FirstPersonHandsAndItemsRenderState handsState,
		CallbackInfo info
	) {
		if (!this.vanillax$wasSpinning) {
			return;
		}

		AvatarRenderState avatar = playerState.avatarRenderState;

		if (avatar != null) {
			avatar.isAutoSpinAttack = true;
		}

		this.vanillax$wasSpinning = false;
	}

	@WrapOperation(
		method = "submitArmWithItem",
		at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/client/renderer/item/ItemStackRenderState;submit(Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/SubmitNodeCollector;III)V",
			ordinal = 1
		)
	)
	private void vanillax$applyHeldItemSettings(
		ItemStackRenderState state,
		PoseStack poseStack,
		SubmitNodeCollector collector,
		int lightCoords,
		int overlayCoords,
		int outlineColor,
		Operation<Void> original,
		@Local(argsOnly = true) ItemStack itemStack
	) {
		VanillaXConfig config = VanillaXConfig.get();

		if (itemStack.is(Items.SHIELD)) {
			if (config.shieldHeight == 100) {
				original.call(state, poseStack, collector, lightCoords, overlayCoords, outlineColor);
				return;
			}

			poseStack.pushPose();

			try {
				poseStack.translate(0.0F, config.shieldHeightOffset(), 0.0F);
				original.call(state, poseStack, collector, lightCoords, overlayCoords, outlineColor);
			} finally {
				poseStack.popPose();
			}
			return;
		}

		if (!itemStack.is(Items.TOTEM_OF_UNDYING) || config.totemScale == 100) {
			original.call(state, poseStack, collector, lightCoords, overlayCoords, outlineColor);
			return;
		}

		float scale = config.totemScaleFactor();
		poseStack.pushPose();

		try {
			poseStack.scale(scale, scale, scale);
			original.call(state, poseStack, collector, lightCoords, overlayCoords, outlineColor);
		} finally {
			poseStack.popPose();
		}
	}
}
