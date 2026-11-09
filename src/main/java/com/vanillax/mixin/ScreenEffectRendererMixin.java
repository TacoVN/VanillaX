package com.vanillax.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.vanillax.VanillaXConfig;
import net.minecraft.client.renderer.ScreenEffectRenderer;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.item.ItemStackRenderState;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.resources.Identifier;
import org.joml.Matrix4f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ScreenEffectRenderer.class)
public class ScreenEffectRendererMixin {
	@Inject(method = "submitBlockSprite", at = @At("HEAD"), cancellable = true)
	private static void vanillax$easterEgg(
		Identifier atlasLocation,
		float u0,
		float v0,
		float u1,
		float v1,
		PoseStack poseStack,
		SubmitNodeCollector submitNodeCollector,
		int color,
		CallbackInfo info
	) {
		if (VanillaXConfig.get().easterEgg) {
			info.cancel();
		}
	}

	@Inject(method = "buildFireQuad", at = @At("HEAD"))
	private static void vanillax$fireHeight(TextureAtlasSprite sprite, VertexConsumer builder, Matrix4f pose, CallbackInfo info) {
		VanillaXConfig config = VanillaXConfig.get();
		if (config.fireHeight != 100) {
			pose.translate(0.0F, config.fireHeightOffset(), 0.0F);
		}
	}

	@WrapOperation(
		method = "renderItemActivationAnimation",
		at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/client/renderer/item/ItemStackRenderState;submit(Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/SubmitNodeCollector;III)V"
		)
	)
	private void vanillax$applyTotemPopSettings(
		ItemStackRenderState state,
		PoseStack poseStack,
		SubmitNodeCollector collector,
		int lightCoords,
		int overlayCoords,
		int outlineColor,
		Operation<Void> original
	) {
		VanillaXConfig config = VanillaXConfig.get();
		boolean applySize = config.totemPopSize != 100;
		boolean applyHeight = config.totemPopHeight != 100;

		if (!applySize && !applyHeight) {
			original.call(state, poseStack, collector, lightCoords, overlayCoords, outlineColor);
			return;
		}

		poseStack.pushPose();

		try {
			if (applySize) {
				float scale = config.totemPopScale();
				poseStack.scale(scale, scale, scale);
			}
			if (applyHeight) {
				poseStack.translate(0.0F, config.totemPopHeightOffset(), 0.0F);
			}

			original.call(state, poseStack, collector, lightCoords, overlayCoords, outlineColor);
		} finally {
			poseStack.popPose();
		}
	}
}
