package com.vanillax.gui;

import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.FocusableTextWidget;
import net.minecraft.client.gui.components.OptionsList;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.narration.NarratableEntry;
import net.minecraft.client.gui.screens.options.OptionsSubScreen;
import net.minecraft.network.chat.Component;

public class VanillaXOptionsList extends OptionsList {
	private static final int HEADER_HEIGHT = 14;
	private static final int FIRST_HEADER_HEIGHT = 14;
	private static final int HEADER_TEXT_OFFSET = -4;
	private static final int FIRST_HEADER_TEXT_OFFSET = -3;
	private final OptionsSubScreen screen;
	private boolean firstHeader = true;

	public VanillaXOptionsList(Minecraft minecraft, int width, OptionsSubScreen screen) {
		super(minecraft, width, screen);
		this.screen = screen;
	}

	@Override
	public void addHeader(Component text) {
		boolean first = this.firstHeader;
		this.addEntry(new CompactHeaderEntry(text, first), first ? FIRST_HEADER_HEIGHT : HEADER_HEIGHT);
		this.firstHeader = false;
	}

	private final class CompactHeaderEntry extends OptionsList.AbstractEntry {
		private final FocusableTextWidget widget;
		private final boolean first;

		private CompactHeaderEntry(Component text, boolean first) {
			this.first = first;
			this.widget = FocusableTextWidget.builder(text, VanillaXOptionsList.this.screen.getFont())
				.alwaysShowBorder(false)
				.backgroundFill(FocusableTextWidget.BackgroundFill.ON_FOCUS)
				.build();
		}

		@Override
		public void extractContent(GuiGraphicsExtractor graphics, int mouseX, int mouseY, boolean hovered, float a) {
			int offset = this.first ? FIRST_HEADER_TEXT_OFFSET : HEADER_TEXT_OFFSET;
			this.widget.setPosition(VanillaXOptionsList.this.screen.width / 2 - this.widget.getWidth() / 2, this.getContentY() + offset);
			this.widget.extractRenderState(graphics, mouseX, mouseY, a);
		}

		@Override
		public List<? extends GuiEventListener> children() {
			return List.of(this.widget);
		}

		@Override
		public List<? extends NarratableEntry> narratables() {
			return List.of(this.widget);
		}
	}
}
