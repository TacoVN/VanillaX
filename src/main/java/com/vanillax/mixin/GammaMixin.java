package com.vanillax.mixin;

import com.vanillax.GammaValueSet;
import net.minecraft.client.OptionInstance;
import net.minecraft.client.Options;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.Slice;

/**
 * Swaps the value set of the vanilla brightness option so it can reach 500% while the Gamma 5
 * toggle is on.
 */
@Mixin(Options.class)
public class GammaMixin {
	@ModifyArg(
		method = "<init>",
		at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/client/OptionInstance;<init>(Ljava/lang/String;Lnet/minecraft/client/OptionInstance$TooltipSupplier;Lnet/minecraft/client/OptionInstance$CaptionBasedToString;Lnet/minecraft/client/OptionInstance$ValueSet;Ljava/lang/Object;Lnet/minecraft/client/OptionInstance$ValueUpdateListener;)V",
			ordinal = 0
		),
		slice = @Slice(from = @At(value = "CONSTANT", args = "stringValue=options.gamma")),
		index = 3
	)
	private OptionInstance.ValueSet<?> vanillax$widenGamma(OptionInstance.ValueSet<?> original) {
		return GammaValueSet.INSTANCE;
	}
}
