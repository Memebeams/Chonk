package com.memebeams.chonk.client;

import com.memebeams.chonk.Chonk;
import com.memebeams.chonk.client.gui.ChunkLoaderScreen;
import com.memebeams.chonk.util.RegistryHandler;
import net.minecraft.client.gui.ScreenManager;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

@Mod.EventBusSubscriber(value = Dist.CLIENT, modid = Chonk.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ChonkClient {

    @SubscribeEvent
    public static void setupClient(FMLClientSetupEvent event) {
        ScreenManager.registerFactory(RegistryHandler.CHUNK_LOADER_CONTAINER.get(), ChunkLoaderScreen::new);
    }
}
