package com.memebeams.chonk.client;

import com.memebeams.chonk.Chonk;
import com.memebeams.chonk.client.gui.ChunkLoaderScreen;
import com.memebeams.chonk.client.render.ChunkLoaderTileRenderer;
import com.memebeams.chonk.util.RegistryHandler;
import net.minecraft.client.gui.ScreenManager;
import net.minecraft.client.renderer.texture.AtlasTexture;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

import static com.memebeams.chonk.client.render.ChunkLoaderTileRenderer.HIGHLIGHT_TEXTURE;

@Mod.EventBusSubscriber(value = Dist.CLIENT, modid = Chonk.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ChonkClient {

    @SubscribeEvent
    public static void setupClient(FMLClientSetupEvent event) {
        ScreenManager.registerFactory(RegistryHandler.CHUNK_LOADER_CONTAINER.get(), ChunkLoaderScreen::new);
        ChunkLoaderTileRenderer.register();
    }

    @SubscribeEvent
    public static void onTextureStitch(TextureStitchEvent.Pre event) {
        if (!event.getMap().getTextureLocation().equals(AtlasTexture.LOCATION_BLOCKS_TEXTURE)) {
            return;
        }
        event.addSprite(HIGHLIGHT_TEXTURE);
    }
}
