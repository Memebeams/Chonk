package com.memebeams.chonk.client.render;

import com.memebeams.chonk.Chonk;
import com.memebeams.chonk.tile.ChunkLoaderTile;
import com.memebeams.chonk.util.RegistryHandler;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.AtlasTexture;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.vector.Quaternion;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraftforge.fml.client.registry.ClientRegistry;

import java.util.HashMap;
import java.util.Map;

public class ChunkLoaderTileRenderer extends TileEntityRenderer<ChunkLoaderTile> {
    public static final ResourceLocation HIGHLIGHT_TEXTURE = new ResourceLocation(Chonk.MOD_ID, "block/chunk_loader_highlight");
    private static final Map<ChunkPos, Float> HIGHLIGHT_PROGRESS = new HashMap<>();

    public ChunkLoaderTileRenderer(TileEntityRendererDispatcher rendererDispatcherIn) {
        super(rendererDispatcherIn);
    }

    @Override
    public void render(ChunkLoaderTile tile, float partialTicks, MatrixStack matrixStack, IRenderTypeBuffer buffer, int combinedLight, int combinedOverlay) {
        ChunkPos chunkPos = new ChunkPos(tile.getPos());

        if (!HIGHLIGHT_PROGRESS.containsKey(chunkPos) || !tile.showHighlight()) {
            HIGHLIGHT_PROGRESS.put(chunkPos, 0f);
            return;
        } else if (HIGHLIGHT_PROGRESS.get(chunkPos) < 1f) {
            HIGHLIGHT_PROGRESS.put(chunkPos, HIGHLIGHT_PROGRESS.get(chunkPos) + 0.05f);
        }

        TextureAtlasSprite sprite = Minecraft.getInstance().getAtlasSpriteGetter(AtlasTexture.LOCATION_BLOCKS_TEXTURE).apply(HIGHLIGHT_TEXTURE);
        IVertexBuilder builder = buffer.getBuffer(RenderType.getTranslucent());

        // Initial push
        matrixStack.push();

        BlockPos pos = tile.getPos();
        float xOffset = 8f - pos.getX() % 16;
        float zOffset = 8f - pos.getZ() % 16;
        float height = pos.getY();
        float scale = tile.getRadius();
        matrixStack.scale(1.0f, 1.0f, 1.0f);

        this.drawFace(matrixStack, builder, sprite, xOffset, zOffset, scale, height, 0);
        this.drawFace(matrixStack, builder, sprite, xOffset, zOffset, scale, height, 90);
        this.drawFace(matrixStack, builder, sprite, xOffset, zOffset, scale, height, 180);
        this.drawFace(matrixStack, builder, sprite, xOffset, zOffset, scale, height, 270);

        matrixStack.pop();

    }

    private void drawFace(MatrixStack matrixStack, IVertexBuilder builder, TextureAtlasSprite sprite, float xOffset, float zOffset, float scale, float height, float angle) {
        matrixStack.push();

        Quaternion rotation = Vector3f.YP.rotationDegrees(angle);
        matrixStack.translate(xOffset, 0f, zOffset);
        matrixStack.rotate(rotation);
        matrixStack.translate(0f, 0f, 8f + 16f * scale);

        add(builder, matrixStack, -8f - 16f * scale, -height, 0f, sprite.getMinU(), sprite.getMinV());
        add(builder, matrixStack, 8f + 16f * scale, -height, 0f, sprite.getMaxU(), sprite.getMinV());
        add(builder, matrixStack, 8f + 16f * scale, 256 - height, 0f, sprite.getMaxU(), sprite.getMaxV());
        add(builder, matrixStack, -8f - 16f * scale, 256 - height, 0f, sprite.getMinU(), sprite.getMaxV());

        add(builder, matrixStack, -8f - 16f * scale, 256 - height, 0f, sprite.getMinU(), sprite.getMaxV());
        add(builder, matrixStack, 8f + 16f * scale, 256 - height, 0f, sprite.getMaxU(), sprite.getMaxV());
        add(builder, matrixStack, 8f + 16f * scale, -height, 0f, sprite.getMaxU(), sprite.getMinV());
        add(builder, matrixStack, -8f - 16f * scale, -height, 0f, sprite.getMinU(), sprite.getMinV());

        matrixStack.pop();
    }

    private void add(IVertexBuilder renderer, MatrixStack stack, float x, float y, float z, float u, float v) {
        renderer.pos(stack.getLast().getMatrix(), x, y, z)
                .color(1.0f, 1.0f, 1.0f, 1.0f)
                .tex(u, v)
                .lightmap(0, 240)
                .normal(1, 0, 0)
                .endVertex();
    }

    public static void register() {
        ClientRegistry.bindTileEntityRenderer(RegistryHandler.CHUNK_LOADER_TILE.get(), ChunkLoaderTileRenderer::new);
    }

    @Override
    public boolean isGlobalRenderer(ChunkLoaderTile te) {
        return true;
    }
}
