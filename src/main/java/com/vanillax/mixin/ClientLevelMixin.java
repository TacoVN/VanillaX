package com.vanillax.mixin;

import com.vanillax.VanillaXConfig;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.particles.ExplosionParticleInfo;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.util.random.WeightedList;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientLevel.class)
public class ClientLevelMixin {
	@Inject(method = "trackExplosionEffects", at = @At("HEAD"), cancellable = true)
	private void vanillax$explosionBlockParticles(
		Vec3 center, float radius, int blockCount, WeightedList<ExplosionParticleInfo> blockParticles, CallbackInfo info
	) {
		if (!VanillaXConfig.get().explosionParticles) {
			info.cancel();
		}
	}

	@Inject(method = "addParticle", at = @At("HEAD"), cancellable = true)
	private void vanillax$explosionParticles(
		ParticleOptions particle, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed, CallbackInfo info
	) {
		if (VanillaXConfig.get().explosionParticles) {
			return;
		}

		if (particle.getType() == ParticleTypes.EXPLOSION || particle.getType() == ParticleTypes.EXPLOSION_EMITTER) {
			info.cancel();
		}
	}
}
