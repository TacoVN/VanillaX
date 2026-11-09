package com.vanillax.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import com.vanillax.VanillaXConfig;
import net.minecraft.client.Camera;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.fog.FogData;
import net.minecraft.client.renderer.fog.FogRenderer;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.material.FogType;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(FogRenderer.class)
public class FogRendererMixin {
	private static final float NO_FOG_DISTANCE = 1.0E7F;

	// The distances are cleared before setupFog returns so that every RETURN callback sees the
	// final values; Sodium captures FogData at RETURN for its own shaders.
	@Inject(
		method = "setupFog",
		at = @At(
			value = "FIELD",
			target = "Lnet/minecraft/client/renderer/fog/FogData;renderDistanceEnd:F",
			opcode = Opcodes.PUTFIELD
		)
	)
	private void vanillax$removeFog(
		Camera camera,
		int renderDistance,
		DeltaTracker deltaTracker,
		float partialTick,
		ClientLevel level,
		CallbackInfoReturnable<FogData> info,
		@Local FogData fogData
	) {
		// OFF removes the fog, ON restores the vanilla one.
		if (level == null || VanillaXConfig.get().fog) {
			return;
		}

		FogType fluid = camera.getFluidInCamera();
		boolean removesFluidFog = fluid == FogType.WATER || fluid == FogType.LAVA;
		boolean removesAtmosphericFog = level.dimension() == Level.NETHER || level.dimension() == Level.END;
		if (!removesFluidFog && !removesAtmosphericFog) {
			return;
		}

		// Powder snow keeps its fog, and blindness/darkness replace the water or atmospheric fog in
		// vanilla; only the environment fog of this feature is removed.
		if (fluid == FogType.POWDER_SNOW) {
			return;
		}
		if (fluid != FogType.LAVA) {
			Entity entity = camera.entity();
			if (entity instanceof LivingEntity livingEntity
				&& (livingEntity.hasEffect(MobEffects.BLINDNESS) || livingEntity.hasEffect(MobEffects.DARKNESS))) {
				return;
			}
		}

		fogData.environmentalStart = NO_FOG_DISTANCE;
		fogData.environmentalEnd = NO_FOG_DISTANCE;
		fogData.renderDistanceStart = NO_FOG_DISTANCE;
		fogData.renderDistanceEnd = NO_FOG_DISTANCE;
		fogData.skyEnd = NO_FOG_DISTANCE;
		fogData.cloudEnd = NO_FOG_DISTANCE;
	}
}
