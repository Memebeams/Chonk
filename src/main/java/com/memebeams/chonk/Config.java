package com.memebeams.chonk;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber
public class Config {

    public static final String CATEGORY_CHUNK_LOADING = "chunk_loading";

    public static ForgeConfigSpec SERVER_CONFIG;

    public static ForgeConfigSpec.IntValue CHUNK_LOADER_MAX_RADIUS;


    static {
        ForgeConfigSpec.Builder SERVER_BUILDER = new ForgeConfigSpec.Builder();

        SERVER_BUILDER.comment("Chunk loading settings").push(CATEGORY_CHUNK_LOADING);
        CHUNK_LOADER_MAX_RADIUS = SERVER_BUILDER.comment("Maximum radius of a chunk loader in chunks")
                .defineInRange("maxRadius", 5, 1, Integer.MAX_VALUE);
        SERVER_BUILDER.pop();

        SERVER_CONFIG = SERVER_BUILDER.build();
    }
}
