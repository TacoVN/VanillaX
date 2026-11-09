package com.vanillax.mixin;

import com.vanillax.VanillaXConfig;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LivingEntity.class)
public class LivingEntityMixin {
	@Inject(method = "spawnItemParticles", at = @At("HEAD"), cancellable = true)
	private void vanillax$eatingParticles(ItemStack itemStack, int count, CallbackInfo info) {
		if (!VanillaXConfig.get().eatingParticles) {
			info.cancel();
		}
	}
}
