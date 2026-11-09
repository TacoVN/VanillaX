package com.vanillax.mixin;

import com.vanillax.VanillaXConfig;
import net.minecraft.client.renderer.debug.EntityHitboxDebugRenderer;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(EntityHitboxDebugRenderer.class)
public class EntityHitboxDebugRendererMixin {
	@Inject(
		method = "showHitboxes",
		at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/gizmos/Gizmos;cuboid(Lnet/minecraft/world/phys/AABB;Lnet/minecraft/gizmos/GizmoStyle;)Lnet/minecraft/gizmos/GizmoProperties;",
			ordinal = 0,
			shift = At.Shift.AFTER
		),
		cancellable = true
	)
	private void vanillax$cleanHitboxes(Entity entity, float partialTicks, boolean isServerEntity, CallbackInfo info) {
		if (VanillaXConfig.get().cleanHitboxes) {
			info.cancel();
		}
	}
}
