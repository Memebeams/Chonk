package com.memebeams.chonk;

import com.memebeams.chonk.network.PacketChunkLoaderSetRadius;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.network.NetworkRegistry;
import net.minecraftforge.fml.network.simple.SimpleChannel;

public class PacketHandler {
    private static SimpleChannel INSTANCE;
    private static int ID = 0;

    private static int nextID() {
        return ID++;
    }

    public static void registerMessages() {
        INSTANCE = NetworkRegistry.newSimpleChannel(new ResourceLocation(Chonk.MOD_ID, ""),
                () -> "1.0",
                s -> true,
                s -> true);

        INSTANCE.messageBuilder(PacketChunkLoaderSetRadius.class, nextID())
                .encoder(PacketChunkLoaderSetRadius::toBytes)
                .decoder(PacketChunkLoaderSetRadius::new)
                .consumer(PacketChunkLoaderSetRadius::handle)
                .add();
    }

//    public static void sendToClient(Object packet, ServerPlayerEntity player) {
//        INSTANCE.sendTo(packet, player.connection.netManager, NetworkDirection.PLAY_TO_CLIENT);
//    }

    public static void sendToServer(Object packet) {
        INSTANCE.sendToServer(packet);
    }
}
