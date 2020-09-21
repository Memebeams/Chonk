package com.memebeams.chonk;

import com.memebeams.chonk.util.PacketHandler;
import com.memebeams.chonk.util.PlayerEventHandler;
import com.memebeams.chonk.util.RegistryHandler;
import com.memebeams.chonk.util.WorldTickHandler;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

// The value here should match an entry in the META-INF/mods.toml file
@Mod("chonk")
public class Chonk
{
    // Directly reference a log4j logger.
    private static final Logger LOGGER = LogManager.getLogger();
    public static final String MOD_ID = "chonk";

    public static final WorldTickHandler worldTickHandler = new WorldTickHandler();
    public static final PlayerEventHandler playerHandler = new PlayerEventHandler();

    public Chonk() {
        RegistryHandler.init();
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::setup);
        MinecraftForge.EVENT_BUS.register(this);
        MinecraftForge.EVENT_BUS.register(worldTickHandler);
        MinecraftForge.EVENT_BUS.register(playerHandler);
    }

    private void setup(final FMLCommonSetupEvent event)
    {
        ModLoadingContext.get().registerConfig(ModConfig.Type.SERVER, Config.SERVER_CONFIG);
        PacketHandler.registerMessages();
    }
}
