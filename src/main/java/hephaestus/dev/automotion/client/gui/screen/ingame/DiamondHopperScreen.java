package hephaestus.dev.automotion.client.gui.screen.ingame;

import com.mojang.blaze3d.systems.RenderSystem;
import hephaestus.dev.automotion.common.Automotion;
import hephaestus.dev.automotion.common.screen.DiamondHopperScreenHandler;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public class DiamondHopperScreen extends HandledScreen<DiamondHopperScreenHandler> {
	private static final TranslatableText NORTH = new TranslatableText("automotion.gui.north");
	private static final TranslatableText SOUTH = new TranslatableText("automotion.gui.south");
	private static final TranslatableText EAST = new TranslatableText("automotion.gui.east");
	private static final TranslatableText WEST = new TranslatableText("automotion.gui.west");
	private static final TranslatableText DOWN = new TranslatableText("automotion.gui.down");

	private static final Identifier TEXTURE = Automotion.newID("textures/gui/container/diamond_hopper.png");

	public DiamondHopperScreen(DiamondHopperScreenHandler handler, PlayerInventory inventory, Text title) {
		super(handler, inventory, title);
		this.passEvents = false;
		this.backgroundHeight = 240;
		this.playerInventoryTitleY = this.backgroundHeight - 97;
	}

	public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
		this.renderBackground(matrices);
		super.render(matrices, mouseX, mouseY, delta);

		int offset = y - 1;
		final int dY = 26;
		final int dX = 140;
		textRenderer.draw(matrices, NORTH, x + dX, offset += dY, 0x404040);
		textRenderer.draw(matrices, SOUTH, x + dX, offset += dY, 0x404040);
		textRenderer.draw(matrices, EAST, x + dX, offset += dY, 0x404040);
		textRenderer.draw(matrices, WEST, x + dX, offset += dY, 0x404040);
		textRenderer.draw(matrices, DOWN, x + dX, offset += dY, 0x404040);

		this.drawMouseoverTooltip(matrices, mouseX, mouseY);
	}

	protected void drawBackground(MatrixStack matrices, float delta, int mouseX, int mouseY) {
		RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
		this.client.getTextureManager().bindTexture(TEXTURE);
		int i = (this.width - this.backgroundWidth) / 2;
		int j = (this.height - this.backgroundHeight) / 2;
		this.drawTexture(matrices, i, j, 0, 0, this.backgroundWidth, this.backgroundHeight);
	}
}
