package com.memebeams.chonk.client.gui;

import com.memebeams.chonk.Chonk;
import com.memebeams.chonk.Config;
import com.memebeams.chonk.container.ChunkLoaderContainer;
import com.memebeams.chonk.network.PacketChunkLoaderSetRadius;
import com.memebeams.chonk.network.PacketChunkLoaderToggleHighlight;
import com.memebeams.chonk.tile.ChunkLoaderTile;
import com.memebeams.chonk.util.PacketHandler;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ChunkLoaderScreen extends ContainerScreen<ChunkLoaderContainer> {
    private static final Logger LOGGER = LogManager.getLogger("Chonk ChunkLoaderScreen");
    private static final ResourceLocation BG_TEXTURE = new ResourceLocation(Chonk.MOD_ID, "textures/gui/chunk_loader.png");

    public ChunkLoaderScreen(ChunkLoaderContainer screenContainer, PlayerInventory inv, ITextComponent titleIn) {
        super(screenContainer, inv, titleIn);
        this.xSize = 177;
        this.ySize = 91;
    }

    @Override
    public void render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
        this.renderBackground(matrixStack);
        super.render(matrixStack, mouseX, mouseY, partialTicks);
        this.renderHoveredTooltip(matrixStack, mouseX, mouseY);
    }

    @Override
    protected void drawGuiContainerForegroundLayer(MatrixStack matrixStack, int x, int y) {
        int relX = (this.width - this.xSize) / 2;
        int relY = (this.height - this.ySize) / 2;
        ChunkLoaderTile tile = this.container.getTile();

        this.drawRadiusButtons(matrixStack, tile);
        this.drawIndicatorButton(relX + 10, relY + this.ySize - 30, this.xSize - 20, tile);
        this.drawOwner(matrixStack, this.xSize / 2, 10, tile);
    }

    private void drawRadiusButtons(MatrixStack stack, ChunkLoaderTile tile) {
        int buttonWidth = 20;
        int buttonSpacing = 30;

        int left = (this.xSize / 2) - (buttonWidth * 2 + buttonSpacing) / 2;
        int top = (this.ySize / 2) - 30;

        int buttonsLeft = ((this.width - this.xSize) / 2) + left;
        int buttonsTop = ((this.height - this.ySize) / 2) + top + 20;

        int labelX = left + buttonWidth + (buttonSpacing / 2);
        int labelY = top + 8;
        String labelText = new TranslationTextComponent("chonk.chunk_loader.radius").getString();
        this.font.drawString(stack, labelText, (float)(labelX - font.getStringWidth(labelText) / 2), (float)labelY, 0);

        Button decrementButton = new Button(buttonsLeft, buttonsTop, buttonWidth, 20, new StringTextComponent("-"), (button) ->
                PacketHandler.sendToServer(new PacketChunkLoaderSetRadius(tile.getRadius() - 1, tile.getWorld(), tile.getPos())));
        decrementButton.active = tile.getRadius() > 0;
        this.addButton(decrementButton);

        Button incrementButton = new Button(buttonsLeft + buttonWidth + buttonSpacing, buttonsTop, buttonWidth, 20, new StringTextComponent("+"), (button) ->
                PacketHandler.sendToServer(new PacketChunkLoaderSetRadius(tile.getRadius() + 1, tile.getWorld(), tile.getPos())));
        incrementButton.active = tile.getRadius() < Config.CHUNK_LOADER_MAX_RADIUS.get();
        this.addButton(incrementButton);

        labelX = left + buttonWidth + (buttonSpacing / 2);
        labelY = top + 25;
        labelText = String.valueOf(tile.getRadius());
        this.font.drawString(stack, labelText, (float)(labelX - font.getStringWidth(labelText) / 2), (float)labelY, 0);
    }

    private void drawIndicatorButton(int left, int top, int width, ChunkLoaderTile tile) {
        Button toggleIndicatorButton = new Button(left, top, width, 20, new TranslationTextComponent("chonk.chunk_loader.toggleHighlight"), (button) ->
                PacketHandler.sendToServer(new PacketChunkLoaderToggleHighlight(tile.getWorld(), tile.getPos())));
        this.addButton(toggleIndicatorButton);
    }

    private void drawOwner(MatrixStack stack, int left, int top, ChunkLoaderTile tile) {
        String message = new StringBuilder()
                .append(new TranslationTextComponent("chonk.chunk_loader.owner").getString())
                .append(": ")
                .append(tile.getOwnerName())
                .toString();
        this.font.drawString(stack, message, (float)(left - font.getStringWidth(message) / 2), (float)top, 0);
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(MatrixStack matrixStack, float partialTicks, int x, int y) {
        RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
        this.minecraft.getTextureManager().bindTexture(BG_TEXTURE);
        int relX = (this.width - this.xSize) / 2;
        int relY = (this.height - this.ySize) / 2;
        this.blit(matrixStack, relX, relY, 0, 0, this.xSize, this.ySize);
    }
}
