package com.memebeams.chonk.client.gui;

import com.memebeams.chonk.Chonk;
import com.memebeams.chonk.Config;
import com.memebeams.chonk.PacketHandler;
import com.memebeams.chonk.container.ChunkLoaderContainer;
import com.memebeams.chonk.network.PacketChunkLoaderSetRadius;
import com.memebeams.chonk.network.PacketChunkLoaderToggleHighlight;
import com.memebeams.chonk.tile.ChunkLoaderTile;
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

        Button decrementButton = new Button(relX + 10, relY + 10, 20, 20, new StringTextComponent("-"), (button) ->
                PacketHandler.sendToServer(new PacketChunkLoaderSetRadius(tile.getRadius() - 1, tile.getWorld(), tile.getPos())));
        decrementButton.active = tile.getRadius() > 0;
        this.addButton(decrementButton);

        Button incrementButton = new Button(relX + 60, relY + 10, 20, 20, new StringTextComponent("+"), (button) ->
                PacketHandler.sendToServer(new PacketChunkLoaderSetRadius(tile.getRadius() + 1, tile.getWorld(), tile.getPos())));
        incrementButton.active = tile.getRadius() < Config.CHUNK_LOADER_MAX_RADIUS.get();
        this.addButton(incrementButton);

        Button toggleIndicatorButton = new Button(relX + this.xSize - 70, relY + 10, 110, 20, new TranslationTextComponent("chonk.chunk_loader.toggleHighlight"), (button) ->
                PacketHandler.sendToServer(new PacketChunkLoaderToggleHighlight(tile.getWorld(), tile.getPos())));
        this.addButton(toggleIndicatorButton);

        this.drawCenteredString(matrixStack, this.font, String.valueOf(tile.getRadius()), 45, 15, 0);
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
