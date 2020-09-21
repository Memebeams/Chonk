package com.memebeams.chonk;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber
public class Config {

    public static final String CATEGORY_CHUNK_LOADING = "chunk_loading";

    public static ForgeConfigSpec SERVER_CONFIG;

    public static ForgeConfigSpec.IntValue CHUNK_LOADER_MAX_RADIUS;

    public static ForgeConfigSpec.IntValue CHUNK_LOADERS_PER_PLAYER;

    static {
        ForgeConfigSpec.Builder SERVER_BUILDER = new ForgeConfigSpec.Builder();

        SERVER_BUILDER.comment("Chunk loading settings").push(CATEGORY_CHUNK_LOADING);
        CHUNK_LOADER_MAX_RADIUS = SERVER_BUILDER.comment("Maximum radius of a world anchor in chunks (0 = 1x1, 1 = 3x3, etc.)")
                .defineInRange("maxRadius", 5, 1, Integer.MAX_VALUE);
        CHUNK_LOADERS_PER_PLAYER = SERVER_BUILDER.comment("Maximum number of world anchors a given player can have")
                .defineInRange("maxAnchors", Integer.MAX_VALUE, 1, Integer.MAX_VALUE);
        SERVER_BUILDER.pop();

        SERVER_CONFIG = SERVER_BUILDER.build();
    }
}
