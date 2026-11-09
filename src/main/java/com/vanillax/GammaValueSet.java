package com.vanillax;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import java.util.Optional;
import net.minecraft.client.OptionInstance;

/**
 * Value set for the vanilla brightness option.
 *
 * <p>Minecraft 1.19 clamped brightness to 0..1, so the slider could not go above 100%. This set
 * widens the range to 0..5, which the slider shows as 0%..500%.
 */
public enum GammaValueSet implements OptionInstance.SliderableValueSet<Double> {
	INSTANCE;

	public static final double MAX_GAMMA = 5.0;

	@Override
	public Optional<Double> validateValue(Double value) {
		return value >= 0.0 && value <= MAX_GAMMA ? Optional.of(value) : Optional.empty();
	}

	@Override
	public double toSliderValue(Double value) {
		return value / MAX_GAMMA;
	}

	@Override
	public Double fromSliderValue(double progress) {
		return progress * MAX_GAMMA;
	}

	@Override
	public Codec<Double> codec() {
		return Codec.either(Codec.doubleRange(0.0, MAX_GAMMA), Codec.BOOL)
			.xmap(either -> either.map(value -> value, value -> value ? 1.0 : 0.0), Either::left);
	}
}
