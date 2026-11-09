package com.vanillax.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.vanillax.VanillaXConfig;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.entity.player.Input;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(LocalPlayer.class)
public class PermanentSprintMixin {
	/**
	 * Only the start-sprint key check is replaced, so vanilla still stops sprinting while
	 * sneaking, using items or colliding with a wall.
	 */
	@WrapOperation(
		method = "aiStep",
		at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/player/Input;sprint()Z")
	)
	private boolean vanillax$permanentSprint(Input input, Operation<Boolean> original) {
		boolean sprint = original.call(input);

		if (sprint || !VanillaXConfig.get().permanentSprint) {
			return sprint;
		}

		return input.forward() && !input.backward();
	}
}
