package com.vanillax.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.mojang.renderpearl.api.pipeline.RenderPipeline;
import com.vanillax.VanillaXConfig;
import com.vanillax.render.VanillaXColors;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.Hud;
import net.minecraft.resources.Identifier;
import org.joml.Matrix3x2fStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

@Mixin(Hud.class)
public class HudMixin {
	private static final Identifier PUMPKIN_OVERLAY = Identifier.withDefaultNamespace("textures/misc/pumpkinblur.png");
	private static final Identifier CROSSHAIR_SPRITE = Identifier.withDefaultNamespace("hud/crosshair");
	private static final String WITHERED_HEART_PREFIX = "hud/heart/withered_";
	private static final int SCOPE_BACKGROUND = -16777216;
	private static final int WITHER_HEART_BODY = 0xFF736156;

	private static final byte EDGE = 1;
	private static final byte BODY = 2;
	private static final byte SHINE = 3;

	private static final byte[][] WITHER_HEART_FULL = {
		{0, 0, 0, 0, 0, 0, 0, 0, 0},
		{0, 0, BODY, BODY, 0, BODY, BODY, 0, 0},
		{0, BODY, SHINE, BODY, BODY, BODY, BODY, BODY, 0},
		{0, BODY, BODY, BODY, BODY, BODY, BODY, BODY, 0},
		{0, EDGE, BODY, BODY, BODY, BODY, BODY, EDGE, 0},
		{0, 0, EDGE, BODY, BODY, BODY, EDGE, 0, 0},
		{0, 0, 0, EDGE, BODY, EDGE, 0, 0, 0},
		{0, 0, 0, 0, EDGE, 0, 0, 0, 0},
		{0, 0, 0, 0, 0, 0, 0, 0, 0}
	};

	private static final byte[][] WITHER_HEART_HALF = {
		{0, 0, 0, 0, 0, 0, 0, 0, 0},
		{0, 0, BODY, BODY, 0, 0, 0, 0, 0},
		{0, BODY, SHINE, BODY, BODY, 0, 0, 0, 0},
		{0, BODY, BODY, BODY, BODY, 0, 0, 0, 0},
		{0, EDGE, BODY, BODY, BODY, 0, 0, 0, 0},
		{0, 0, EDGE, BODY, BODY, 0, 0, 0, 0},
		{0, 0, 0, EDGE, BODY, 0, 0, 0, 0},
		{0, 0, 0, 0, EDGE, 0, 0, 0, 0},
		{0, 0, 0, 0, 0, 0, 0, 0, 0}
	};

	@WrapOperation(
		method = "extractHeart",
		at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/client/gui/GuiGraphicsExtractor;blitSprite(Lcom/mojang/renderpearl/api/pipeline/RenderPipeline;Lnet/minecraft/resources/Identifier;IIII)V"
		)
	)
	private void vanillax$witherHearts(
		GuiGraphicsExtractor graphics,
		RenderPipeline pipeline,
		Identifier sprite,
		int x,
		int y,
		int width,
		int height,
		Operation<Void> original
	) {
		String path = sprite.getPath();

		if (VanillaXConfig.get().witherFix && path.startsWith(WITHERED_HEART_PREFIX)) {
			boolean blinking = path.contains("blinking");
			byte[][] mask = path.contains("_half") ? WITHER_HEART_HALF : WITHER_HEART_FULL;
			// Body is #736156; the edge and the highlight are derived from it so the pixel art keeps its shading.
			int edge = blinking ? 0xFF68584E : 0xFF554840;
			int body = blinking ? 0xFF8C7669 : WITHER_HEART_BODY;
			int shine = blinking ? 0xFFBB9D8B : 0xFF998172;

			for (int row = 0; row < mask.length; row++) {
				byte[] line = mask[row];
				int column = 0;

				while (column < line.length) {
					byte value = line[column];

					if (value == 0) {
						column++;
						continue;
					}

					int start = column;

					while (column < line.length && line[column] == value) {
						column++;
					}

					graphics.fill(x + start, y + row, x + column, y + row + 1, value == EDGE ? edge : (value == BODY ? body : shine));
				}
			}
			return;
		}

		original.call(graphics, pipeline, sprite, x, y, width, height);
	}

	@WrapOperation(
		method = "extractCrosshair",
		at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/client/gui/GuiGraphicsExtractor;blitSprite(Lcom/mojang/renderpearl/api/pipeline/RenderPipeline;Lnet/minecraft/resources/Identifier;IIII)V"
		)
	)
	private void vanillax$centerCrosshair(
		GuiGraphicsExtractor graphics,
		RenderPipeline pipeline,
		Identifier sprite,
		int x,
		int y,
		int width,
		int height,
		Operation<Void> original
	) {
		VanillaXConfig config = VanillaXConfig.get();

		if (!config.crosshairFix || !CROSSHAIR_SPRITE.equals(sprite)) {
			original.call(graphics, pipeline, sprite, x, y, width, height);
			return;
		}

		float centeredX = (graphics.guiWidth() - width) / 2.0F;
		float centeredY = (graphics.guiHeight() - height) / 2.0F;

		if (centeredX == x && centeredY == y) {
			original.call(graphics, pipeline, sprite, x, y, width, height);
			return;
		}

		Matrix3x2fStack pose = graphics.pose();
		pose.pushMatrix();
		pose.translate(centeredX - x, centeredY - y);
		original.call(graphics, pipeline, sprite, x, y, width, height);
		pose.popMatrix();
	}

	@WrapOperation(
		method = "extractTextureOverlay",
		at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/client/gui/GuiGraphicsExtractor;blit(Lcom/mojang/renderpearl/api/pipeline/RenderPipeline;Lnet/minecraft/resources/Identifier;IIFFIIIII)V"
		)
	)
	private void vanillax$pumpkinOpacity(
		GuiGraphicsExtractor graphics,
		RenderPipeline pipeline,
		Identifier texture,
		int x,
		int y,
		float u,
		float v,
		int width,
		int height,
		int textureWidth,
		int textureHeight,
		int color,
		Operation<Void> original
	) {
		VanillaXConfig config = VanillaXConfig.get();

		if (config.pumpkinOpacity != 100 && PUMPKIN_OVERLAY.equals(texture)) {
			color = VanillaXColors.applyColorAlpha(color, config.pumpkinAlpha());
		}

		original.call(graphics, pipeline, texture, x, y, u, v, width, height, textureWidth, textureHeight, color);
	}

	@WrapOperation(
		method = "extractSpyglassOverlay",
		at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/client/gui/GuiGraphicsExtractor;blit(Lcom/mojang/renderpearl/api/pipeline/RenderPipeline;Lnet/minecraft/resources/Identifier;IIFFIIII)V"
		)
	)
	private void vanillax$spyglassOpacity(
		GuiGraphicsExtractor graphics,
		RenderPipeline pipeline,
		Identifier texture,
		int x,
		int y,
		float u,
		float v,
		int width,
		int height,
		int textureWidth,
		int textureHeight,
		Operation<Void> original
	) {
		VanillaXConfig config = VanillaXConfig.get();

		if (config.spyglassOpacity == 100) {
			original.call(graphics, pipeline, texture, x, y, u, v, width, height, textureWidth, textureHeight);
			return;
		}

		graphics.blit(
			pipeline,
			texture,
			x,
			y,
			u,
			v,
			width,
			height,
			width,
			height,
			textureWidth,
			textureHeight,
			VanillaXColors.applyColorAlpha(-1, config.spyglassAlpha())
		);
	}

	@ModifyConstant(method = "extractSpyglassOverlay", constant = @Constant(intValue = SCOPE_BACKGROUND))
	private int vanillax$spyglassBackgroundOpacity(int color) {
		VanillaXConfig config = VanillaXConfig.get();

		if (config.spyglassOpacity == 100) {
			return color;
		}

		return VanillaXColors.applyColorAlpha(color, config.spyglassAlpha());
	}
}
